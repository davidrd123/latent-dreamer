(ns daydreamer.director
  "Impure Director edge for conducted-daydreaming feedback loops.

  This namespace is intentionally outside the pure Mueller kernel. It loads the
  creative brief and style extensions, assembles the Director prompt, calls the
  live Gemini HTTP API when requested, and applies the resulting feedback to the
  conducted-system benchmark state."
  (:require [clj-yaml.core :as yaml]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [daydreamer.episodic-memory :as episodic])
  (:import (java.net URI)
           (java.net.http HttpClient HttpRequest HttpRequest$BodyPublishers
                          HttpResponse$BodyHandlers)))

(def ^:private default-model
  "gemini-3-flash-preview")

(def ^:private default-temperature
  0.2)

(def ^:private default-max-output-tokens
  1024)

(def ^:private system-prompt
  (str
   "You are the Director of a conducted dream. Your job is to interpret each "
   "dream scene through a creative brief and notice what the dreamer has not "
   "said yet.\n\n"
   "You will receive:\n"
   "- A dream scene from the kernel\n"
   "- A creative brief\n"
   "- Style extensions with capstone templates and negative space\n\n"
   "Return a small structured feedback echo, not a visual prompt. Introduce "
   "1-3 novel concepts grounded in the brief, optionally boost up to 2 world "
   "situations, keep valence_delta small, use emotional episodes sparingly, "
   "and write a short interpretation_note.\n\n"
   "Prefer dormant situations over reinforcing the currently active one. "
   "Do not simply echo the scene's existing indices back."))

(def ^:private output-schema
  {:type "object"
   :properties {"director_concepts" {:type "array"
                                     :items {:type "string"}
                                     :maxItems 3}
                "situation_boosts" {:type "object"
                                    :maxProperties 2
                                    :properties {}}
                "valence_delta" {:type "number"
                                 :minimum -0.10
                                 :maximum 0.10}
                "surprise" {:type "number"
                            :minimum 0.0
                            :maximum 0.25}
                "emotional_episodes"
                {:type "array"
                 :maxItems 2
                 :items {:type "object"
                         :properties {"affect" {:type "string"}
                                      "target" {:type "string"}
                                      "source_situation" {:type "string"}
                                      "intensity" {:type "number"
                                                   :minimum 0.01
                                                   :maximum 0.30}
                                      "decay" {:type "number"
                                               :minimum 0.80
                                               :maximum 0.99}
                                      "indices" {:type "array"
                                                 :items {:type "string"}
                                                 :minItems 1}}
                         :required ["affect" "intensity" "decay" "indices"]}}
                "interpretation_note" {:type "string"}}
   :required ["director_concepts"
              "situation_boosts"
              "valence_delta"
              "surprise"
              "emotional_episodes"
              "interpretation_note"]})

(defn- clamp
  ([value]
   (clamp value 0.0 1.0))
  ([value minimum maximum]
   (-> (double (or value 0.0))
       (max minimum)
       (min maximum))))

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

(defn- normalize-concept
  [value]
  (let [value (some-> value
                      str
                      str/trim
                      str/lower-case
                      (str/replace #"[^a-z0-9]+" "_")
                      (str/replace #"^_+|_+$" ""))]
    (when (seq value)
      (keyword value))))

(defn- json-string
  [value]
  (json/write-str value))

(defn- resolve-sibling-path
  [world-path explicit field-value default-name]
  (cond
    explicit
    (.getCanonicalPath (io/file explicit))

    (and world-path field-value)
    (let [candidate (io/file field-value)
          resolved (if (.isAbsolute candidate)
                     candidate
                     (io/file (.getParentFile (io/file world-path))
                              field-value))]
      (.getCanonicalPath resolved))

    world-path
    (let [candidate (io/file (.getParentFile (io/file world-path))
                             default-name)]
      (when (.exists candidate)
        (.getCanonicalPath candidate)))

    :else
    nil))

(defn load-assets
  "Load creative brief and style extensions for a Director loop."
  [{:keys [world-path world-data creative-brief-path style-extensions-path]}]
  (let [brief-path (resolve-sibling-path world-path
                                         creative-brief-path
                                         (:creative_brief_path world-data)
                                         "creative_brief.yaml")
        style-path (resolve-sibling-path world-path
                                         style-extensions-path
                                         (:style_extensions_path world-data)
                                         "style_extensions.yaml")]
    (when-not (and brief-path (.exists (io/file brief-path)))
      (throw (ex-info "Could not locate creative_brief.yaml for Director mode"
                      {:world-path world-path
                       :creative-brief-path brief-path})))
    (when-not (and style-path (.exists (io/file style-path)))
      (throw (ex-info "Could not locate style_extensions.yaml for Director mode"
                      {:world-path world-path
                       :style-extensions-path style-path})))
    {:creative-brief (yaml/parse-string (slurp brief-path) :keywords true)
     :style-extensions (yaml/parse-string (slurp style-path) :keywords true)
     :creative-brief-path brief-path
     :style-extensions-path style-path}))

(defn build-user-message
  "Assemble the Director user message from assets and the current cycle input."
  [{:keys [creative-brief style-extensions]}
   {:keys [packet scene world-situations previous-director-feedback]}]
  (str "## Creative Brief\n\n"
       (json-string creative-brief)
       "\n\n## Style Extensions\n\n"
       (json-string {:capstone_templates (:capstone_templates style-extensions)
                     :negative_space (:negative_space style-extensions)})
       "\n\n## This Cycle's Scene\n\n"
       (json-string scene)
       "\n\n## Dreamer State Packet\n\n"
       (json-string packet)
       "\n\n## World Situations\n\n"
       (json-string world-situations)
       "\n\n## Previous Director Feedback\n\n"
       (json-string previous-director-feedback)
       "\n\nRespond with JSON matching the response schema exactly."))

(defn- http-client
  []
  (HttpClient/newHttpClient))

(defn- parse-gemini-body
  [body]
  (let [parsed (json/read-str body :key-fn keyword)
        text (or (get-in parsed [:candidates 0 :content :parts 0 :text])
                 (get-in parsed [:candidates 0 :output]))
        text (some-> text str/trim)]
    (cond
      (map? (:parsed parsed))
      (:parsed parsed)

      (seq text)
      (let [cleaned (-> text
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
        (try
          (json/read-str cleaned :key-fn keyword)
          (catch Exception exc
            (throw (ex-info "Director returned invalid JSON text"
                            {:text cleaned
                             :raw-body body}
                            exc)))))

      :else
      (throw (ex-info "Director returned no JSON content"
                      {:response parsed})))))

(defn call-director
  "Call Gemini with the current cycle input and return raw Director JSON."
  ([assets director-input]
   (call-director assets director-input {}))
  ([assets director-input {:keys [api-key model temperature max-output-tokens]
                           :or {model default-model
                                temperature default-temperature
                                max-output-tokens default-max-output-tokens}}]
   (let [api-key (or api-key (System/getenv "GEMINI_API_KEY"))
         model (or model default-model)
         temperature (or temperature default-temperature)
         max-output-tokens (or max-output-tokens default-max-output-tokens)]
     (when-not (seq api-key)
       (throw (ex-info "GEMINI_API_KEY is required for live Director mode"
                       {:mode :gemini})))
     (let [endpoint (str "https://generativelanguage.googleapis.com/v1beta/models/"
                         model
                         ":generateContent?key="
                         api-key)
           payload {:systemInstruction {:parts [{:text system-prompt}]}
                    :contents [{:role "user"
                                :parts [{:text (build-user-message assets
                                                                   director-input)}]}]
                    :generationConfig {:temperature temperature
                                       :maxOutputTokens max-output-tokens
                                       :thinkingConfig {:thinkingLevel "low"}
                                       :responseMimeType "application/json"
                                       :responseSchema output-schema}}
           request (-> (HttpRequest/newBuilder)
                       (.uri (URI/create endpoint))
                       (.header "Content-Type" "application/json")
                       (.POST (HttpRequest$BodyPublishers/ofString
                               (json/write-str payload)))
                       .build)
           response (.send (http-client)
                           request
                           (HttpResponse$BodyHandlers/ofString))
           status (.statusCode response)
           body (.body response)]
       (when-not (<= 200 status 299)
         (throw (ex-info "Director HTTP request failed"
                         {:status status
                          :body body
                          :model model})))
       (parse-gemini-body body)))))

(defn mock-director
  "Deterministic local Director for tests and offline runs."
  [_assets {:keys [scene packet]}]
  (let [goal-type (keyword (or (get-in scene [:mind :goal_type])
                               (get packet "goal_type")
                               "roving"))
        situation-ids (mapv as-keyword (or (get-in scene [:world :situation_ids])
                                           []))
        active-indices (mapv as-keyword (or (get packet "active_indices") []))
        sees-seam? (some #{:seam :stored_scenery :awareness} active-indices)
        sees-ring? (some #{:ritual :performance :honesty} active-indices)]
    (case goal-type
      :rationalization
      {:director_concepts (cond-> ["honesty" "performance"]
                            sees-seam? (conj "mercy"))
       :situation_boosts {"s4_the_ring" 0.12}
       :valence_delta 0.04
       :surprise 0.03
       :emotional_episodes []
       :interpretation_note
       "The seam reads as an honest stage rather than a threat. The ring wakes as a place where admission changes the pressure."}

      :reversal
      {:director_concepts ["hinge" "alternative" "ring"]
       :situation_boosts {"s4_the_ring" 0.10}
       :valence_delta 0.03
       :surprise 0.07
       :emotional_episodes []
       :interpretation_note
       "The scene implies an alternate bend in the performance. The ring becomes salient as the place where the admission could have happened."}

      :roving
      {:director_concepts (cond-> ["horizon" "ritual"]
                            sees-ring? (conj "light"))
       :situation_boosts {"s4_the_ring" 0.08}
       :valence_delta 0.02
       :surprise 0.02
       :emotional_episodes []
       :interpretation_note
       "Relief drift exposes a gentler performance frame. The scene glances toward the ring instead of staying trapped in overload."}

      {:director_concepts ["apparatus"]
       :situation_boosts (if (some #{:s1_seeing_through} situation-ids)
                           {"s4_the_ring" 0.06}
                           {})
       :valence_delta 0.0
       :surprise 0.0
       :emotional_episodes []
       :interpretation_note
       "The scene reveals the apparatus behind the moment."})))

(defn build-director-fn
  "Return a callable Director function for benchmark loops."
  [assets {:keys [mode model temperature max-output-tokens api-key]
           :or {mode :mock
                model default-model
                temperature default-temperature
                max-output-tokens default-max-output-tokens}}]
  (let [model (or model default-model)
        temperature (or temperature default-temperature)
        max-output-tokens (or max-output-tokens default-max-output-tokens)]
    (case mode
      :mock
      (fn [director-input]
        (mock-director assets director-input))

      :gemini
      (fn [director-input]
        (call-director assets
                       director-input
                       {:api-key api-key
                        :model model
                        :temperature temperature
                        :max-output-tokens max-output-tokens}))

      (throw (ex-info "Unknown Director mode"
                      {:mode mode})))))

(defn normalize-feedback
  "Normalize raw Director output into the kernel's feedback echo shape."
  [raw]
  {:director-concepts (->> (get raw :director_concepts
                                (get raw "director_concepts" []))
                           (keep normalize-concept)
                           ordered-unique
                           vec)
   :situation-boosts (into {}
                           (keep (fn [[situation-id boost]]
                                   (let [situation-id (as-keyword situation-id)
                                         boost (double (or boost 0.0))]
                                     (when (and situation-id (pos? boost))
                                       [situation-id (clamp boost 0.0 0.15)]))))
                           (or (get raw :situation_boosts)
                               (get raw "situation_boosts")
                               {}))
   :valence-delta (clamp (or (get raw :valence_delta)
                             (get raw "valence_delta")
                             0.0)
                         -0.10
                         0.10)
   :surprise (clamp (or (get raw :surprise)
                        (get raw "surprise")
                        0.0)
                    0.0
                    0.25)
   :emotional-episodes
   (->> (or (get raw :emotional_episodes)
            (get raw "emotional_episodes")
            [])
        (keep (fn [episode]
                (when (map? episode)
                  {:affect (as-keyword (or (:affect episode)
                                           (get episode "affect")))
                   :target (as-keyword (or (:target episode)
                                           (get episode "target")))
                   :source-situation (as-keyword (or (:source_situation episode)
                                                     (get episode "source_situation")))
                   :intensity (clamp (or (:intensity episode)
                                         (get episode "intensity")
                                         0.0)
                                     0.0
                                     0.30)
                   :decay (clamp (or (:decay episode)
                                     (get episode "decay")
                                     0.92)
                                 0.80
                                 0.99)
                   :indices (->> (or (:indices episode)
                                     (get episode "indices")
                                     [])
                                 (keep normalize-concept)
                                 ordered-unique
                                 vec)})))
        vec)
   :notes (str (or (get raw :interpretation_note)
                   (get raw "interpretation_note")
                   (get raw :notes)
                   (get raw "notes")
                   ""))})

(defn- target-situation-ids
  [selected-situation-id feedback]
  (ordered-unique
   (concat (keys (:situation-boosts feedback))
           (when selected-situation-id
             [selected-situation-id]))))

(defn- update-situation
  [world situation-id update-fn]
  (if (contains? (get world :situations-state {}) situation-id)
    (update-in world [:situations-state situation-id] update-fn)
    world))

(defn- apply-valence-delta
  [world situation-id delta]
  (update-situation
   world
   situation-id
   (fn [situation]
     (if (pos? delta)
       (-> situation
           (update :hope #(clamp (+ (double (or % 0.0)) delta)))
           (update :waiting #(clamp (+ (double (or % 0.0)) (* delta 0.4))))
           (update :anger #(clamp (- (double (or % 0.0)) (* delta 0.2))))
           (update :threat #(clamp (- (double (or % 0.0)) (* delta 0.15)))))
       (let [magnitude (Math/abs (double delta))]
         (-> situation
             (update :threat #(clamp (+ (double (or % 0.0)) magnitude)))
             (update :anger #(clamp (+ (double (or % 0.0)) (* magnitude 0.25))))
             (update :hope #(clamp (- (double (or % 0.0)) (* magnitude 0.2))))))))))

(defn- apply-emotional-episode
  [world {:keys [affect target source-situation intensity indices decay]}]
  (let [world (reduce episodic/add-recent-index world indices)
        world (if source-situation
                (update-situation
                 world
                 source-situation
                 (fn [situation]
                   (case affect
                     :anger (update situation :anger #(clamp (+ (double (or % 0.0))
                                                                (* intensity 0.35))))
                     :dread (update situation :threat #(clamp (+ (double (or % 0.0))
                                                                 (* intensity 0.35))))
                     :fear (update situation :threat #(clamp (+ (double (or % 0.0))
                                                                (* intensity 0.35))))
                     :hope (update situation :hope #(clamp (+ (double (or % 0.0))
                                                              (* intensity 0.35))))
                     :grief (update situation :grief #(clamp (+ (double (or % 0.0))
                                                                (* intensity 0.35))))
                     situation)))
                world)
        world (if (and (= affect :anger) target)
                (reduce-kv (fn [current-world situation-id situation]
                             (if (= (:directed-target situation) target)
                               (update-situation
                                current-world
                                situation-id
                                #(update % :anger
                                         (fn [value]
                                           (clamp (+ (double (or value 0.0))
                                                     (* intensity 0.35))))))
                               current-world))
                           world
                           (:situations-state world))
                world)]
    [world {:affect affect
            :target target
            :source_situation source-situation
            :intensity (clamp intensity 0.0 0.30)
            :decay decay
            :indices indices}]))

(defn apply-feedback
  "Apply Director feedback to the conducted-system benchmark edge.

  The feedback mutates only the benchmark-facing world state: situation
  activation, recent indices, serendipity bias, and trace echo. It does not
  rewrite the already-selected current cycle; the effects influence the next
  cycle."
  [world {:keys [selected-situation-id]} feedback]
  (let [{:keys [director-concepts situation-boosts valence-delta surprise
                emotional-episodes notes]}
        (normalize-feedback feedback)
        world (reduce episodic/add-recent-index world director-concepts)
        world (update world
                      :director-recent-concepts
                      (fn [recent]
                        (->> (concat (or recent []) director-concepts)
                             ordered-unique
                             (take-last 6)
                             vec)))
        world (reduce-kv (fn [current-world situation-id boost]
                           (update-situation
                            current-world
                            situation-id
                            #(update % :activation
                                     (fn [value]
                                       (clamp (+ (double (or value 0.0))
                                                 boost))))))
                         world
                         situation-boosts)
        world (reduce (fn [current-world situation-id]
                        (apply-valence-delta current-world
                                             situation-id
                                             valence-delta))
                      world
                      (target-situation-ids selected-situation-id
                                            {:situation-boosts situation-boosts}))
        [world applied-episodes]
        (reduce (fn [[current-world applied] episode]
                  (let [[next-world applied-episode]
                        (apply-emotional-episode current-world episode)]
                    [next-world (conj applied applied-episode)]))
                [world []]
                emotional-episodes)
        world (if (pos? surprise)
                (update world :serendipity-bias
                        (fn [current]
                          (clamp (max (double (or current 0.0))
                                      surprise)
                                 0.0
                                 0.35)))
                world)]
    [world {:director_concepts director-concepts
            :situation_boosts situation-boosts
            :valence_delta valence-delta
            :surprise surprise
            :emotional_episodes applied-episodes
            :notes notes}]))
