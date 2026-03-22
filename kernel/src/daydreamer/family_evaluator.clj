(ns daydreamer.family-evaluator
  "Impure evaluator edge for post-plan memory triage.

  This sits outside the pure kernel. It scores an already-produced family plan
  for realism, desirability, and retention policy, then returns a normalized
  evaluation map the kernel can admit safely."
  (:require [clojure.data.json :as json]
            [clojure.string :as str])
  (:import (java.net URI)
           (java.net.http HttpClient HttpRequest HttpRequest$BodyPublishers
                          HttpResponse$BodyHandlers)))

(def ^:private default-model
  "claude-haiku-4-5-20251001")

(def ^:private default-temperature
  0.1)

(def ^:private default-max-output-tokens
  250)

(def ^:private allowed-realism
  #{:imaginary :plausible :counterfactual})

(def ^:private allowed-desirability
  #{:positive :mixed :negative})

(def ^:private allowed-retention-classes
  #{:hot-cues :cold-provenance :payload-exemplar})

(def ^:private allowed-keep-decisions
  #{:keep-hot :archive-cold :keep-exemplar})

(def ^:private allowed-promotion-decisions
  #{:stay-provisional :promote-durable})

(def ^:private system-prompt
  (str
   "You evaluate a completed daydream family plan after it has already been produced.\n\n"
   "You do NOT choose branches or mutate state. You only score what should stay cognitively"
   " accessible.\n\n"
   "Return JSON only with exactly these fields:\n"
   "- realism: one of imaginary, plausible, counterfactual\n"
   "- desirability: one of positive, mixed, negative\n"
   "- retention_class: one of hot-cues, cold-provenance, payload-exemplar\n"
   "- keep_decision: one of keep-hot, archive-cold, keep-exemplar\n"
   "- promotion_decision: one of stay-provisional, promote-durable\n"
   "- evaluation_reasons: array of 1-3 short snake_case strings\n\n"
   "Prefer payload-exemplar only when the plan contains reusable structured material."
   " Prefer archive-cold when the plan should remain recorded but not stay hot in retrieval."))

(def ^:private output-example
  {:realism "plausible"
   :desirability "positive"
   :retention_class "payload-exemplar"
   :keep_decision "keep-exemplar"
   :promotion_decision "promote-durable"
   :evaluation_reasons ["reusable_reframe_payload" "psychologically_coherent"]})

(defn- normalize-text
  [value]
  (some-> value str str/trim))

(defn- json-string
  [value]
  (json/write-str value))

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
      (parse-json-text text)

      :else
      (throw (ex-info "Family evaluator returned no JSON content"
                      {:response parsed})))))

(defn- as-keyword
  [value]
  (cond
    (keyword? value) value
    (string? value) (-> value
                        str/trim
                        str/lower-case
                        (str/replace #"[^a-z0-9_\-]+" "-")
                        keyword)
    (nil? value) nil
    :else (keyword (str value))))

(defn- normalize-reasons
  [values]
  (->> values
       (keep as-keyword)
       distinct
       (take 3)
       vec))

(defn- allowed-or-default
  [raw allowed default]
  (let [value (as-keyword raw)]
    (if (contains? allowed value)
      value
      default)))

(defn normalize-family-evaluation
  "Normalize raw evaluator output against the kernel's allowed taxonomy."
  [raw default-evaluation]
  (let [raw (or raw {})]
    (-> default-evaluation
        (assoc :realism
               (allowed-or-default (:realism raw)
                                   allowed-realism
                                   (:realism default-evaluation)))
        (assoc :desirability
               (allowed-or-default (:desirability raw)
                                   allowed-desirability
                                   (:desirability default-evaluation)))
        (assoc :retention-class
               (allowed-or-default (or (:retention-class raw)
                                       (:retention_class raw))
                                   allowed-retention-classes
                                   (:retention-class default-evaluation)))
        (assoc :keep-decision
               (allowed-or-default (or (:keep-decision raw)
                                       (:keep_decision raw))
                                   allowed-keep-decisions
                                   (:keep-decision default-evaluation)))
        (assoc :promotion-decision
               (allowed-or-default (or (:promotion-decision raw)
                                       (:promotion_decision raw))
                                   allowed-promotion-decisions
                                   (:promotion-decision default-evaluation)))
        (assoc :evaluation-reasons
               (let [reasons (normalize-reasons (or (:evaluation-reasons raw)
                                                    (:evaluation_reasons raw)))]
                 (if (seq reasons)
                   reasons
                   (:evaluation-reasons default-evaluation))))
        (assoc :evaluation-source
               (or (as-keyword (or (:evaluation-source raw)
                                   (:evaluation_source raw)))
                   (:evaluation-source default-evaluation)
                   :external-evaluator)))))

(defn- build-family-evaluation-packet
  [family-plan]
  {:family (:family family-plan)
   :selection (:selection family-plan)
   :episode_payload (:episode-payload family-plan)
   :emotion_shifts (:emotion-shifts family-plan)
   :emotional_state (:emotional-state family-plan)
   :retrieval_indices (:retrieval-indices family-plan)
   :support_indices (:support-indices family-plan)
   :result (select-keys (:result family-plan)
                        [:episode-id
                         :frame-id
                         :frame-goal-id
                         :hope-situation-id
                         :active-indices
                         :reminded-episode-ids
                         :selection-policy])})

(defn- build-user-message
  [family-plan default-evaluation]
  (str "Family plan packet:\n\n"
       (json-string (build-family-evaluation-packet family-plan))
       "\n\nDefault kernel evaluation:\n\n"
       (json-string default-evaluation)
       "\n\nReturn JSON matching this example shape exactly:\n\n"
       (json-string output-example)))

(defn- call-family-evaluator
  [family-plan default-evaluation {:keys [api-key model temperature max-output-tokens]
                                   :or {model default-model
                                        temperature default-temperature
                                        max-output-tokens default-max-output-tokens}}]
  (let [api-key (or api-key (System/getenv "ANTHROPIC_API_KEY"))
        model (or model default-model)
        temperature (or temperature default-temperature)
        max-output-tokens (or max-output-tokens default-max-output-tokens)]
    (when-not (seq api-key)
      (throw (ex-info "ANTHROPIC_API_KEY is required for live family evaluator mode"
                      {:mode :anthropic})))
    (let [payload {:model model
                   :temperature temperature
                   :max_output_tokens max-output-tokens
                   :system system-prompt
                   :messages [{:role "user"
                               :content (build-user-message family-plan
                                                            default-evaluation)}]}
          request (-> (HttpRequest/newBuilder)
                      (.uri (URI/create "https://api.anthropic.com/v1/messages"))
                      (.header "content-type" "application/json")
                      (.header "x-api-key" api-key)
                      (.header "anthropic-version" "2023-06-01")
                      (.POST (HttpRequest$BodyPublishers/ofString
                              (json-string payload)))
                      (.build))
          response (.send (HttpClient/newHttpClient)
                          request
                          (HttpResponse$BodyHandlers/ofString))
          status (.statusCode response)
          body (.body response)]
      (when-not (<= 200 status 299)
        (throw (ex-info "Family evaluator request failed"
                        {:status status
                         :body body
                         :model model})))
      (assoc (parse-anthropic-body body)
             :evaluation-source :llm-backed
             :provider :anthropic
             :model model))))

(defn mock-family-evaluator
  "Deterministic evaluator for tests and cheap local runs."
  [family-plan _default-evaluation]
  (case (:family family-plan)
    :roving {:realism :imaginary
             :desirability :positive
             :retention-class :hot-cues
             :keep-decision :keep-hot
             :promotion-decision :stay-provisional
             :evaluation-reasons [:mock_attentional_surface]
             :evaluation-source :mock-llm}
    :rationalization {:realism :plausible
                      :desirability :positive
                      :retention-class (if (seq (get-in family-plan
                                                        [:episode-payload :reframe-facts]))
                                         :payload-exemplar
                                         :hot-cues)
                      :keep-decision (if (seq (get-in family-plan
                                                      [:episode-payload :reframe-facts]))
                                       :keep-exemplar
                                       :keep-hot)
                      :promotion-decision (if (seq (get-in family-plan
                                                           [:episode-payload :reframe-facts]))
                                            :promote-durable
                                            :stay-provisional)
                      :evaluation-reasons [:mock_psychological_coherence]
                      :evaluation-source :mock-llm}
    :reversal {:realism :counterfactual
               :desirability :mixed
               :retention-class (if (seq (get-in family-plan
                                                 [:episode-payload :input-facts]))
                                  :payload-exemplar
                                  :hot-cues)
               :keep-decision (if (seq (get-in family-plan
                                               [:episode-payload :input-facts]))
                                :keep-exemplar
                                :keep-hot)
               :promotion-decision (if (seq (get-in family-plan
                                                    [:episode-payload :input-facts]))
                                     :promote-durable
                                     :stay-provisional)
               :evaluation-reasons [:mock_counterfactual_material]
               :evaluation-source :mock-llm}
    {:promotion-decision :stay-provisional
     :evaluation-reasons [:mock_default]
     :evaluation-source :mock-llm}))

(defn build-family-evaluator
  "Build a family-plan evaluator function.

  Returns nil for :heuristic mode, otherwise a function:
  `[family-plan default-evaluation] -> normalized-evaluation`."
  [{:keys [mode model temperature max-output-tokens api-key]
    :or {mode :heuristic
         model default-model
         temperature default-temperature
         max-output-tokens default-max-output-tokens}}]
  (let [mode (or mode :heuristic)]
    (case mode
      :heuristic
      nil

      :mock
      (fn [family-plan default-evaluation]
        (normalize-family-evaluation (mock-family-evaluator family-plan
                                                            default-evaluation)
                                     default-evaluation))

      :anthropic
      (fn [family-plan default-evaluation]
        (normalize-family-evaluation
         (call-family-evaluator family-plan
                                default-evaluation
                                {:api-key api-key
                                 :model model
                                 :temperature temperature
                                 :max-output-tokens max-output-tokens})
         default-evaluation))

      (throw (ex-info "Unknown family evaluator mode"
                      {:mode mode})))))
