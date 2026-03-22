(ns daydreamer.runtime-thought
  "Impure runtime thought edge for per-cycle inner-life realization.

  This namespace sits OUTSIDE the pure kernel. It:
  1. Receives a compact runtime packet describing one cognitive cycle
  2. Optionally calls an LLM (Anthropic) to generate a thought beat
  3. Normalizes and filters the LLM's response
  4. Writes distilled residue back into episodic memory

  The residue is what makes the feedback loop structural, not just
  prompt-level. The episode gets real indices that participate in
  coincidence-mark retrieval on future cycles."
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [daydreamer.episodic-memory :as episodic])
  (:import (java.net URI)
           (java.net.http HttpClient HttpRequest HttpRequest$BodyPublishers
                          HttpResponse$BodyHandlers)))

;; =============================================================================
;; Defaults — model, temperature, token limit for LLM calls
;; =============================================================================

(def ^:private default-model
  "claude-haiku-4-5-20251001")

(def ^:private default-temperature
  0.5)

(def ^:private default-max-output-tokens
  400)

(def ^:private default-routing-policy
  :fixed)

;; =============================================================================
;; System prompt — tells the LLM how to generate a thought beat
;; =============================================================================

(def ^:private system-prompt
  (str
   "You realize one beat of runtime inner life from a cognitive cycle packet.\n\n"
   "Write from inside the character's thought process, not as an external scene "
   "summary. Stay tightly grounded in the selected operator, current situation, "
   "retrieved fragments, and emotional state. Use 2-3 sentences only. Keep it "
   "specific, readable, and psychologically legible.\n\n"
   "Return JSON only with exactly these fields:\n"
   "- thought_beat_text: string\n"
   "- mood_tags: array of 2-4 short strings\n"
   ;; GradMem-inspired: ask for what's NEW, not a general summary.
   ;; The LLM can see retrieved_fragments in the packet, so "not what
   ;; the retrieved fragments already cover" is a checkable instruction.
   "- residue_summary: one sentence capturing what is NEW or UNRESOLVED in this "
   "cycle — not what the retrieved fragments already cover, but what shifted, "
   "surprised, or remained unsettled. Prefer the novel edge over a general summary\n"
   ;; residue_indices must come from the allowed set — the LLM can't
   ;; invent arbitrary keywords. This is the narrow-interface discipline.
   "- residue_indices: array of 1-3 strings chosen only from allowed_residue_indices\n"
   "- image_hint: short phrase for visual direction\n"
   "- audio_hint: short phrase for audio/musical direction\n\n"
   "Do not mention packets, operators, schemas, or system internals in the output."))

;; Example output shape — sent to the LLM as a format reference
(def ^:private output-example
  {:thought_beat_text
   "He recasts the delay as prudence, but the explanation still hooks on the accusation it is trying to pass around."
   :mood_tags ["guarded" "self-dividing" "pressurized"]
   :residue_summary
   "Delay keeps its cover story, but the honest edge remains active underneath it."
   :residue_indices ["honesty" "performance"]
   :image_hint "hand stalled over the object that would force contact"
   :audio_hint "contained tension with a held metallic hum underneath"})

;; =============================================================================
;; Small helpers
;; =============================================================================

(defn- ordered-unique
  "Remove duplicates while preserving insertion order.
  (ordered-unique [:a :b :a :c]) => [:a :b :c]"
  [values]
  (reduce (fn [acc value]
            (if (some #{value} acc)
              acc
              (conj acc value)))
          []
          values))

(defn- as-keyword
  "Coerce a value to a keyword. Strings become keywords, nils stay nil.
  Used to normalize index names from LLM JSON (strings) to kernel format (keywords)."
  [value]
  (cond
    (keyword? value) value
    (string? value) (keyword value)
    (nil? value) nil
    :else (keyword (str value))))

(defn- normalize-text
  "Trim whitespace, return nil for nil input.
  some-> is 'thread if non-nil' — stops early if the value is nil."
  [value]
  (some-> value str str/trim))

(defn- normalize-mood-tag
  "Clean a mood tag: lowercase, strip special chars, spaces become hyphens.
  'Self Dividing!' => 'self-dividing'"
  [value]
  (some-> value
          str
          str/trim
          str/lower-case
          (str/replace #"[^a-z0-9_\- ]+" "")
          (str/replace #"\s+" "-")))

;; =============================================================================
;; Rule provenance — which rules fired this cycle, for episode metadata
;; =============================================================================

(defn- latest-rule-provenance
  "Extract which rules and graph edges were active during this cycle.
  This gets attached to the residue episode so future retrieval can
  find episodes by which rules produced them."
  [world goal-id]
  (let [snapshot (last (:trace world))
        ;; Check if the current goal had a rule-based activation
        activation-provenance (some (fn [activation]
                                      (when (= goal-id (:goal-id activation))
                                        (:rule-provenance activation)))
                                    (:activations snapshot))
        ;; Check if the planning step had rule provenance
        planning-provenance (:rule-provenance snapshot)
        ;; Merge both into a combined rule path
        rule-path (->> [activation-provenance planning-provenance]
                       (mapcat #(or (:rule-path %) []))
                       ordered-unique
                       vec)
        edge-path (->> [activation-provenance planning-provenance]
                       (mapcat #(or (:edge-path %) []))
                       vec)
        provenance (cond-> {:source :runtime-thought-feedback
                            :cycle (:cycle world)}
                     goal-id
                     (assoc :goal-id goal-id)

                     activation-provenance
                     (assoc :activation-rule-provenance activation-provenance)

                     planning-provenance
                     (assoc :planning-rule-provenance planning-provenance))]
    {:rule-path rule-path
     :edge-path edge-path
     :provenance provenance}))

;; =============================================================================
;; JSON and HTTP plumbing — calling the Anthropic API
;; =============================================================================

(defn- json-string
  [value]
  (json/write-str value))

(defn- http-client
  "Create a new Java HTTP client. One per request — simple, no pooling."
  []
  (HttpClient/newHttpClient))

(defn- parse-json-text
  "Extract JSON from LLM text output. Handles markdown code fences,
  leading/trailing whitespace, and finds the outermost {...} block."
  [text]
  (let [cleaned (-> text
                    str
                    str/trim
                    ;; Strip ```json ... ``` markdown fences
                    (str/replace #"(?s)^```json\s*" "")
                    (str/replace #"(?s)^```\s*" "")
                    (str/replace #"(?s)\s*```$" "")
                    str/trim)
        ;; Find the outermost JSON object
        cleaned (if (and (str/includes? cleaned "{")
                         (str/includes? cleaned "}"))
                  (subs cleaned
                        (.indexOf cleaned "{")
                        (inc (.lastIndexOf cleaned "}")))
                  cleaned)]
    (json/read-str cleaned :key-fn keyword)))

(defn- parse-anthropic-body
  "Parse the Anthropic API response body.
  Handles both structured (:parsed) and text-based responses."
  [body]
  (let [parsed (json/read-str body :key-fn keyword)
        ;; Extract text from the content blocks
        text (->> (:content parsed)
                  (filter #(= "text" (:type %)))
                  first
                  :text
                  normalize-text)]
    (cond
      ;; Structured response — already parsed
      (map? (:parsed parsed))
      (:parsed parsed)

      ;; Text response — parse JSON from it
      (seq text)
      (try
        (parse-json-text text)
        (catch Exception exc
          (throw (ex-info "Runtime thought returned invalid JSON text"
                          {:text text
                           :raw-body body}
                          exc))))

      :else
      (throw (ex-info "Runtime thought returned no JSON content"
                      {:response parsed})))))

(defn- build-user-message
  "Build the user message: the packet JSON + the expected output format."
  [packet]
  (str "RuntimeThoughtBeatV1 packet:\n\n"
       (json-string packet)
       "\n\nReturn JSON matching this example shape exactly:\n\n"
       (json-string output-example)))

;; =============================================================================
;; Model routing — Haiku for cheap cycles, Sonnet for important ones
;; =============================================================================

(defn- parse-csv-set
  "Parse a comma-separated string into a set. 'reversal,roving' => #{\"reversal\" \"roving\"}"
  [raw]
  (->> (str/split (or raw "") #",")
       (map str/trim)
       (filter seq)
       set))

(defn- route-model-for-packet
  "Decide which model to use for this packet.

  :fixed policy — always use the base model.
  :haiku_default policy — use base model unless the goal family is in the
  escalation set, in which case use the more expensive escalation model.
  This is how the hybrid routing works: Haiku for most cycles, Sonnet for reversal."
  [packet {:keys [routing-policy base-model escalation-model escalation-goals]}]
  (let [routing-policy (or routing-policy default-routing-policy)
        goal-type (or (get-in packet [:selected_goal :goal_type])
                      (get-in packet [:selected-goal :goal-type]))
        branch-events (or (:branch_events packet)
                          (:branch-events packet))
        escalation-goals (or escalation-goals #{})]
    (if (= routing-policy :fixed)
      [base-model []]
      ;; Collect reasons for escalation
      (let [reasons (cond-> []
                      ;; Goal family is in the escalation set
                      (and goal-type (contains? escalation-goals goal-type))
                      (conj (str "goal_family:" goal-type))

                      ;; Branch event + escalation goal = definitely escalate
                      (and (seq branch-events)
                           goal-type
                           (contains? escalation-goals goal-type))
                      (conj "branch_event"))]
        [(if (seq reasons) escalation-model base-model) reasons]))))

;; =============================================================================
;; LLM call — the actual API request
;; =============================================================================

(defn call-runtime-thought
  "Call Anthropic with the runtime thought packet and return raw JSON.

  Builds an HTTP request to the Anthropic messages API with:
  - system prompt (how to generate the beat)
  - user message (the packet + expected output format)

  Returns the parsed JSON response with :provider and :model added."
  ([packet]
   (call-runtime-thought packet {}))
  ([packet {:keys [api-key model temperature max-output-tokens]
            :or {model default-model
                 temperature default-temperature
                 max-output-tokens default-max-output-tokens}}]
   (let [api-key (or api-key (System/getenv "ANTHROPIC_API_KEY"))
         model (or model default-model)
         temperature (or temperature default-temperature)
         max-output-tokens (or max-output-tokens default-max-output-tokens)]
     (when-not (seq api-key)
       (throw (ex-info "ANTHROPIC_API_KEY is required for runtime thought mode"
                       {:mode :anthropic})))
     (let [payload {:model model
                    :system system-prompt
                    :max_tokens max-output-tokens
                    :temperature temperature
                    :messages [{:role "user"
                                :content (build-user-message packet)}]}
           ;; Build the HTTP request using Java's HttpClient
           request (-> (HttpRequest/newBuilder)
                       (.uri (URI/create "https://api.anthropic.com/v1/messages"))
                       (.header "Content-Type" "application/json")
                       (.header "x-api-key" api-key)
                       (.header "anthropic-version" "2023-06-01")
                       (.POST (HttpRequest$BodyPublishers/ofString
                               (json/write-str payload)))
                       .build)
           response (.send (http-client)
                           request
                           (HttpResponse$BodyHandlers/ofString))
           status (.statusCode response)
           body (.body response)]
       (when-not (<= 200 status 299)
         (throw (ex-info "Runtime thought HTTP request failed"
                         {:status status
                          :body body
                          :model model})))
       (assoc (parse-anthropic-body body)
              :provider :anthropic
              :model model)))))

;; =============================================================================
;; Mock — deterministic thought generation for tests (no API call)
;; =============================================================================

(defn mock-runtime-thought
  "Deterministic local runtime thought for tests and replay control runs.

  Generates formulaic text that still produces real residue indices,
  so you can test the writeback machinery without spending API money.
  The indices come from the allowed set in the packet."
  [packet]
  (let [goal-type (or (get-in packet [:selected_goal :goal_type])
                      (get-in packet [:selected-goal :goal-type])
                      "roving")
        situation-id (or (get-in packet [:selected_goal :situation_id])
                         (get-in packet [:selected-goal :situation-id])
                         "unknown")
        retrieval-label (or (some-> packet :retrieved_fragments first :node_id)
                            (some-> packet :retrieved_fragments first :episode_id)
                            "the remembered fragment")
        ;; Pick the first 3 allowed indices as residue
        residue-indices (->> (:allowed_residue_indices packet)
                             (take 3)
                             vec)]
    {:provider :mock
     :model "mock-runtime-thought-v1"
     :thought_beat_text
     (format "The %s move in %s tries to hold its line, but %s keeps catching on the part that has not settled."
             goal-type
             situation-id
             retrieval-label)
     :mood_tags ["contained" "pressurized" "self-observing"]
     :residue_summary
     (format "The %s line stays active, and %s keeps its pressure on the next cycle."
             goal-type
             retrieval-label)
     :residue_indices residue-indices
     :image_hint "attention snagging on the object that keeps the scene unresolved"
     :audio_hint "tight held tone with unresolved movement underneath"}))

;; =============================================================================
;; Factory — build a thought function from config
;; =============================================================================

(defn build-thought-fn
  "Return a runtime thought function for the requested mode, or nil.

  Modes:
  - nil       => no thought generation (baseline run)
  - :mock     => deterministic local generation
  - :anthropic => real API call, optionally with hybrid routing"
  [{:keys [mode model temperature max-output-tokens api-key routing-policy
           escalation-model escalation-goals]
    :or {model default-model
         temperature default-temperature
         max-output-tokens default-max-output-tokens
         routing-policy default-routing-policy}}]
  (case mode
    nil nil
    :mock mock-runtime-thought
    :anthropic
    (fn [packet]
      (let [escalation-model (or escalation-model model)
            escalation-goals (cond
                               (set? escalation-goals) escalation-goals
                               (string? escalation-goals) (parse-csv-set escalation-goals)
                               :else #{})
            ;; Route: use cheap model by default, expensive model for important families
            [selected-model route-reasons]
            (route-model-for-packet packet
                                    {:routing-policy routing-policy
                                     :base-model model
                                     :escalation-model escalation-model
                                     :escalation-goals escalation-goals})]
        (assoc (call-runtime-thought packet {:api-key api-key
                                             :model selected-model
                                             :temperature temperature
                                             :max-output-tokens max-output-tokens})
               :route-reasons route-reasons)))
    (throw (ex-info "Unsupported runtime thought mode"
                    {:mode mode}))))

;; =============================================================================
;; Normalization — clean and filter raw LLM output
;; =============================================================================

(defn- fallback-residue-indices
  "Safety net: if the LLM returned no valid residue indices (all were
  outside the allowed set), use the first 3 from the allowed + active sets.
  This ensures the writeback loop never drops to zero."
  [packet]
  (->> (concat (:allowed_residue_indices packet)
               (:active_indices packet))
       (map as-keyword)
       (remove nil?)
       ordered-unique
       (take 3)
       vec))

(defn normalize-feedback
  "Normalize raw runtime thought JSON into a stable internal shape.

  Key discipline:
  - residue_indices are FILTERED against allowed_residue_indices
    (the LLM can't invent arbitrary keywords — only select from
    what was structurally active this cycle)
  - if all LLM indices are rejected, fall back to allowed set
  - mood tags are cleaned and deduplicated
  - handles both snake_case and kebab-case (LLMs are inconsistent)"
  [packet raw-feedback]
  (let [;; Build the allowed index set from the packet
        allowed-indices (->> (:allowed_residue_indices packet)
                             (map as-keyword)
                             (remove nil?)
                             ordered-unique
                             vec)
        allowed-set (set allowed-indices)
        ;; Filter LLM's residue indices — keep only allowed ones
        residue-indices (->> (or (:residue_indices raw-feedback)
                                 (:residue-indices raw-feedback))
                             (map as-keyword)
                             (filter allowed-set)  ; <-- the narrow-interface gate
                             ordered-unique
                             vec)
        ;; Fallback if nothing survived filtering
        residue-indices (if (seq residue-indices)
                          residue-indices
                          (fallback-residue-indices packet))
        ;; Clean mood tags
        mood-tags (->> (:mood_tags raw-feedback)
                       (map normalize-mood-tag)
                       (filter seq)
                       ordered-unique
                       (take 4)
                       vec)]
    {:provider (or (:provider raw-feedback) :unknown)
     :model (normalize-text (:model raw-feedback))
     :route-reasons (vec (or (:route-reasons raw-feedback) []))
     :thought-beat-text (or (normalize-text (:thought_beat_text raw-feedback))
                            (normalize-text (:thought-beat-text raw-feedback))
                            "")
     :mood-tags mood-tags
     :residue-summary (or (normalize-text (:residue_summary raw-feedback))
                          (normalize-text (:residue-summary raw-feedback))
                          "")
     :residue-indices residue-indices
     :image-hint (or (normalize-text (:image_hint raw-feedback))
                     (normalize-text (:image-hint raw-feedback))
                     "")
     :audio-hint (or (normalize-text (:audio_hint raw-feedback))
                     (normalize-text (:audio-hint raw-feedback))
                     "")
     :allowed-residue-indices allowed-indices}))

;; =============================================================================
;; Writeback — the critical function that closes the feedback loop
;; =============================================================================

(defn apply-feedback
  "Write distilled runtime-thought residue back into episodic memory.

  This is where the feedback loop becomes structural, not just prompt-level.
  Steps:
  1. Normalize the raw LLM output (filter indices, clean text)
  2. Gather rule provenance (which rules fired this cycle)
  3. Create an episode marked :realism :imaginary (it's LLM-generated, not real)
  4. Store the episode under each residue index
     -> {:plan? true :reminding? true} means these indices participate in
        both planning retrieval and the reminding cascade
  5. Store the episode under each rule that fired
     -> {:plan? false :reminding? false} means rule provenance is queryable
        but doesn't trigger associative drift
  6. Add residue indices to the recent-index FIFO
     -> this immediately influences NEXT cycle's retrieval
  7. Save the feedback for next cycle's prompt (previous_residue_summary)

  Returns [world feedback-applied]."
  [world packet raw-feedback]
  (let [feedback (normalize-feedback packet raw-feedback)
        residue-indices (:residue-indices feedback)
        goal-id (some-> packet :selected_goal :id as-keyword)
        context-id (some-> packet :selected_goal :context_id as-keyword)
        ;; Get rule provenance — which rules and graph edges were active
        {rule-path :rule-path
         edge-path :edge-path
         provenance :provenance}
        (latest-rule-provenance world goal-id)
        ;; Build the episode spec — note :realism :imaginary
        episode-spec (cond-> {:rule :runtime-thought-residue
                              :goal-id goal-id
                              :context-id context-id
                              :realism :imaginary       ; flagged as not-real
                              :desirability :mixed
                              :indices (set residue-indices)}
                       provenance
                       (assoc :provenance provenance)

                       (seq rule-path)
                       (assoc :rule-path rule-path)

                       (seq edge-path)
                       (assoc :edge-path edge-path))
        ;; Step 4: Create the episode in the kernel's episode store
        [world episode-id] (episodic/add-episode world episode-spec)
        ;; Step 5a: Store under each residue index (retrievable + remindable)
        world (reduce (fn [current-world index]
                        (episodic/store-episode current-world
                                                episode-id
                                                index
                                                {:plan? true
                                                 :reminding? true}))
                      world
                      residue-indices)
        ;; Step 5b: Store under each rule ID (queryable, not remindable)
        world (reduce (fn [current-world rule-id]
                        (episodic/store-episode current-world
                                                episode-id
                                                rule-id
                                                {:plan? false
                                                 :reminding? false
                                                 :zone :provenance}))
                      world
                      rule-path)
        ;; Step 6: Update the recent-index FIFO — changes next cycle's retrieval
        world (reduce episodic/add-recent-index world residue-indices)
        ;; Package up everything we did for the trace
        feedback-applied (assoc feedback :episode-id episode-id)]
    ;; Step 7: Save for next cycle's prompt + return
    [(assoc-in world [:autonomous :runtime-thought-last] feedback-applied)
     feedback-applied]))
