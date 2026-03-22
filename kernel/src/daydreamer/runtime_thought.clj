(ns daydreamer.runtime-thought
  "Impure runtime thought edge for per-cycle inner-life realization.

  This namespace sits outside the pure kernel. It receives a compact runtime
  packet from the autonomous benchmark, optionally calls Anthropic, normalizes
  the returned beat, and writes only distilled residue back into episodic
  memory."
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [daydreamer.episodic-memory :as episodic])
  (:import (java.net URI)
           (java.net.http HttpClient HttpRequest HttpRequest$BodyPublishers
                          HttpResponse$BodyHandlers)))

(def ^:private default-model
  "claude-haiku-4-5-20251001")

(def ^:private default-temperature
  0.5)

(def ^:private default-max-output-tokens
  400)

(def ^:private default-routing-policy
  :fixed)

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
   "- residue_summary: one sentence carrying forward what should linger into the next cycle\n"
   "- residue_indices: array of 1-3 strings chosen only from allowed_residue_indices\n"
   "- image_hint: short phrase for visual direction\n"
   "- audio_hint: short phrase for audio/musical direction\n\n"
   "Do not mention packets, operators, schemas, or system internals in the output."))

(def ^:private output-example
  {:thought_beat_text
   "He recasts the delay as prudence, but the explanation still hooks on the accusation it is trying to pass around."
   :mood_tags ["guarded" "self-dividing" "pressurized"]
   :residue_summary
   "Delay keeps its cover story, but the honest edge remains active underneath it."
   :residue_indices ["honesty" "performance"]
   :image_hint "hand stalled over the object that would force contact"
   :audio_hint "contained tension with a held metallic hum underneath"})

(defn- ordered-unique
  [values]
  (reduce (fn [acc value]
            (if (some #{value} acc)
              acc
              (conj acc value)))
          []
          values))

(defn- as-keyword
  [value]
  (cond
    (keyword? value) value
    (string? value) (keyword value)
    (nil? value) nil
    :else (keyword (str value))))

(defn- normalize-text
  [value]
  (some-> value str str/trim))

(defn- normalize-mood-tag
  [value]
  (some-> value
          str
          str/trim
          str/lower-case
          (str/replace #"[^a-z0-9_\- ]+" "")
          (str/replace #"\s+" "-")))

(defn- json-string
  [value]
  (json/write-str value))

(defn- http-client
  []
  (HttpClient/newHttpClient))

(defn- parse-json-text
  [text]
  (let [cleaned (-> text
                    str
                    str/trim
                    (str/replace #"(?s)^```json\s*" "")
                    (str/replace #"(?s)^```\s*" "")
                    (str/replace #"(?s)\s*```$" "")
                    str/trim)
        cleaned (if (and (str/includes? cleaned "{")
                         (str/includes? cleaned "}"))
                  (subs cleaned
                        (.indexOf cleaned "{")
                        (inc (.lastIndexOf cleaned "}")))
                  cleaned)]
    (json/read-str cleaned :key-fn keyword)))

(defn- parse-anthropic-body
  [body]
  (let [parsed (json/read-str body :key-fn keyword)
        text (->> (:content parsed)
                  (filter #(= "text" (:type %)))
                  first
                  :text
                  normalize-text)]
    (cond
      (map? (:parsed parsed))
      (:parsed parsed)

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
  [packet]
  (str "RuntimeThoughtBeatV1 packet:\n\n"
       (json-string packet)
       "\n\nReturn JSON matching this example shape exactly:\n\n"
       (json-string output-example)))

(defn- parse-csv-set
  [raw]
  (->> (str/split (or raw "") #",")
       (map str/trim)
       (filter seq)
       set))

(defn- route-model-for-packet
  [packet {:keys [routing-policy base-model escalation-model escalation-goals]}]
  (let [routing-policy (or routing-policy default-routing-policy)
        goal-type (or (get-in packet [:selected_goal :goal_type])
                      (get-in packet [:selected-goal :goal-type]))
        branch-events (or (:branch_events packet)
                          (:branch-events packet))
        escalation-goals (or escalation-goals #{})]
    (if (= routing-policy :fixed)
      [base-model []]
      (let [reasons (cond-> []
                      (and goal-type (contains? escalation-goals goal-type))
                      (conj (str "goal_family:" goal-type))

                      (and (seq branch-events)
                           goal-type
                           (contains? escalation-goals goal-type))
                      (conj "branch_event"))]
        [(if (seq reasons) escalation-model base-model) reasons]))))

(defn call-runtime-thought
  "Call Anthropic with the runtime thought packet and return raw JSON."
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

(defn mock-runtime-thought
  "Deterministic local runtime thought for tests and replay control runs."
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

(defn build-thought-fn
  "Return a runtime thought function for the requested mode, or nil."
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

(defn- fallback-residue-indices
  [packet]
  (->> (concat (:allowed_residue_indices packet)
               (:active_indices packet))
       (map as-keyword)
       (remove nil?)
       ordered-unique
       (take 3)
       vec))

(defn normalize-feedback
  "Normalize raw runtime thought JSON into a stable internal shape."
  [packet raw-feedback]
  (let [allowed-indices (->> (:allowed_residue_indices packet)
                             (map as-keyword)
                             (remove nil?)
                             ordered-unique
                             vec)
        allowed-set (set allowed-indices)
        residue-indices (->> (or (:residue_indices raw-feedback)
                                 (:residue-indices raw-feedback))
                             (map as-keyword)
                             (filter allowed-set)
                             ordered-unique
                             vec)
        residue-indices (if (seq residue-indices)
                          residue-indices
                          (fallback-residue-indices packet))
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

(defn apply-feedback
  "Write distilled runtime-thought residue back into episodic memory.

  Returns `[world feedback-applied]`."
  [world packet raw-feedback]
  (let [feedback (normalize-feedback packet raw-feedback)
        residue-indices (:residue-indices feedback)
        episode-spec {:rule :runtime-thought-residue
                      :goal-id (some-> packet :selected_goal :id as-keyword)
                      :context-id (some-> packet :selected_goal :context_id as-keyword)
                      :realism :imaginary
                      :desirability :mixed
                      :indices (set residue-indices)}
        [world episode-id] (episodic/add-episode world episode-spec)
        world (reduce (fn [current-world index]
                        (episodic/store-episode current-world
                                                episode-id
                                                index
                                                {:plan? true
                                                 :reminding? true}))
                      world
                      residue-indices)
        world (reduce episodic/add-recent-index world residue-indices)
        feedback-applied (assoc feedback :episode-id episode-id)]
    [(assoc-in world [:autonomous :runtime-thought-last] feedback-applied)
     feedback-applied]))
