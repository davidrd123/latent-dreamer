(ns daydreamer.issue-entry
  "Kernel-owned typed issue admission, activation, retrieval, and lifecycle logs.

  This namespace is intentionally narrow:
  - the model may propose issue packets
  - the kernel validates, admits, activates, retrieves, and logs
  - there is no direct path from proposal to durable authority"
  (:require [clojure.set :as set]
            [clojure.string :as str]))

(def ^:private max-proposals-per-packet 3)
(def ^:private max-provisional-admits-per-packet 2)
(def ^:private max-new-active-per-packet 1)

(def ^:private allowed-issue-kinds
  #{:question :decision :contradiction :hypothesis :commitment :aesthetic-bet})

(def ^:private allowed-issue-statuses
  #{:trace :provisional :durable})

(def ^:private allowed-issue-activations
  #{:cold :warm :active})

(def ^:private allowed-timing-hints
  #{:now :soon :later})

(def ^:private allowed-span-roles
  #{:support :counterpressure})

(def ^:private allowed-uptake-signals
  #{:freeze :dismiss :respond :cut})

(def ^:private repo-jargon
  #{"kernel"
    "membrane"
    "rule access"
    "retrieval reformulation"
    "typed concern"
    "issue-centered"
    "proposer-committer"
    "episode-lifecycle"})

(def ^:private unresolvedness-regex
  #"(?i)(\?|next|later|blocked|not yet|unresolved|decision|decide|whether|should|vs|contradict|conflict|warning|boundary|move now|harden first|not ready|ready now)")

(def ^:private recoverability-stopwords
  #{"a" "an" "and" "are" "as" "at" "be" "by" "for" "from" "if" "in" "into"
    "is" "it" "its" "now" "of" "on" "or" "that" "the" "this" "to" "vs" "with"})

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
    (string? value) (-> value str/trim str/lower-case (str/replace #"[^a-z0-9_\-/ ]+" "")
                        (str/replace #"\s+" "-")
                        keyword)
    (nil? value) nil
    :else (keyword (str value))))

(defn- slugify
  [value]
  (-> (or value "")
      str
      str/trim
      str/lower-case
      (str/replace #"[^a-z0-9]+" "-")
      (str/replace #"^-+|-+$" "")))

(defn- namespaced-keyword
  [namespace-part value]
  (let [slug (slugify value)]
    (when (seq slug)
      (keyword namespace-part slug))))

(defn- phrase-vector
  [value]
  (->> value
       (map #(some-> % str str/trim))
       (filter seq)
       vec))

(defn- quoted-span->key
  [{:keys [doc span-id]}]
  [doc span-id])

(defn- source-span-index
  [source-packet]
  (into {}
        (map (fn [span]
               [(quoted-span->key span) span]))
        (:spans source-packet)))

(defn- source-span-surface
  [source-packet]
  (or (:surface source-packet) :unknown-surface))

(defn- normalize-source-span
  [{:keys [doc span-id quote] :as span}]
  (let [role (or (:span/role span) (:span-role span) :support)]
    {:doc doc
     :span-id span-id
     :quote (some-> quote str)
     :span/role (if (contains? allowed-span-roles role) role :support)}))

(defn- normalize-anchor-phrases
  [anchor-phrases]
  {:topics (phrase-vector (:topics anchor-phrases))
   :tensions (->> (:tensions anchor-phrases)
                  (map (fn [value]
                         (cond
                           (vector? value) (vec (phrase-vector value))
                           (sequential? value) (vec (phrase-vector value))
                           :else (some-> value str str/trim))))
                  (remove nil?)
                  vec)
   :stakes (phrase-vector (:stakes anchor-phrases))
   :actors (phrase-vector (:actors anchor-phrases))
   :projects (phrase-vector (:projects anchor-phrases))
   :tasks (phrase-vector (:tasks anchor-phrases))
   :terms (phrase-vector (:terms anchor-phrases))})

(defn- normalize-proposal
  [idx proposal]
  (let [issue-kind (as-keyword (:issue/kind proposal))
        timing-hint (as-keyword (:timing/hint proposal))]
    {:proposal/id (or (as-keyword (:proposal/id proposal))
                      (keyword (str "prop-" (inc idx))))
     :proposal/type (or (:proposal/type proposal) :issue)
     :issue/kind issue-kind
     :issue/title (some-> (:issue/title proposal) str str/trim)
     :source/phrasing (phrase-vector (:source/phrasing proposal))
     :why/unresolved (some-> (:why/unresolved proposal) str str/trim)
     :why/now (some-> (:why/now proposal) str str/trim)
     :source/spans (mapv normalize-source-span (:source/spans proposal))
     :anchor/phrases (normalize-anchor-phrases (:anchor/phrases proposal))
     :timing/hint (when (contains? allowed-timing-hints timing-hint)
                    timing-hint)
     :opposing/span-ids (->> (:opposing/span-ids proposal)
                             (map str)
                             (filter seq)
                             vec)}))

(defn normalize-proposal-packet
  "Normalize a model proposal packet into the kernel's canonical keys."
  [packet]
  {:schema/version (or (:schema/version packet) 1)
   :packet/id (or (as-keyword (:packet/id packet)) :packet-1)
   :source/packet-id (as-keyword (:source/packet-id packet))
   :proposals (->> (:proposals packet)
                   (map-indexed normalize-proposal)
                   (take max-proposals-per-packet)
                   vec)
   :abstain/reason (some-> (:abstain/reason packet) str str/trim)})

(defn create-source-packet
  "Create a bounded source packet that a proposer may read."
  [{:keys [source-packet-id surface standing-note? settled-labels spans metadata]
    :or {surface :architecture-review
         settled-labels #{}
         spans []
         metadata {}}}]
  {:source-packet-id source-packet-id
   :surface surface
   :standing-note? (boolean standing-note?)
   :settled-labels (set settled-labels)
   :spans (mapv (fn [{:keys [doc span-id quote]
                      :as span}]
                  {:doc doc
                   :span-id span-id
                   :quote (some-> quote str)
                   :surface (or (:surface span) surface)})
                spans)
   :metadata metadata})

(defn create-issue
  [issue]
  (let [issue-id (:issue/id issue)
        issue-kind (:issue/kind issue)
        issue-status (or (:issue/status issue) :provisional)
        issue-activation (or (:issue/activation issue) :cold)
        issue-title (:issue/title issue)
        issue-source-packet-id (:issue/source-packet-id issue)
        issue-source-spans (:issue/source-spans issue)
        issue-source-phrasing (:issue/source-phrasing issue)
        issue-normalized-label (:issue/normalized-label issue)
        issue-content-cues (:issue/content-cues issue)
        issue-content-cue-set (:issue/content-cue-set issue)
        issue-support-tags (:issue/support-tags issue)
        issue-provenance (:issue/provenance issue)
        issue-outcome-log (or (:issue/outcome-log issue) [])
        issue-human-uptake (or (:issue/human-uptake issue)
                               {:freeze 0 :dismiss 0 :respond 0 :cut 0})
        issue-corroboration-count (or (:issue/corroboration-count issue) 1)
        issue-activation-score (:issue/activation-score issue)
        issue-disposition (or (:issue/disposition issue) :open)
        issue-dedupe-key (:issue/dedupe-key issue)
        issue-timing-hint (:issue/timing-hint issue)]
    {:schema/version 1
     :object/type :issue
     :issue/id issue-id
     :issue/kind issue-kind
     :issue/status (if (contains? allowed-issue-statuses issue-status)
                     issue-status
                     :provisional)
     :issue/activation (if (contains? allowed-issue-activations issue-activation)
                         issue-activation
                         :cold)
     :issue/title issue-title
     :issue/source-packet-id issue-source-packet-id
     :issue/source-spans (vec issue-source-spans)
     :issue/source-phrasing (vec issue-source-phrasing)
     :issue/normalized-label issue-normalized-label
     :issue/content-cues issue-content-cues
     :issue/content-cue-set (set issue-content-cue-set)
     :issue/support-tags (vec issue-support-tags)
     :issue/provenance issue-provenance
     :issue/outcome-log (vec issue-outcome-log)
     :issue/human-uptake issue-human-uptake
     :issue/corroboration-count issue-corroboration-count
     :issue/activation-score issue-activation-score
     :issue/disposition issue-disposition
     :issue/dedupe-key issue-dedupe-key
     :issue/timing-hint issue-timing-hint}))

(defn initialize-issue-state
  [world]
  (-> world
      (update :issues #(or % {}))
      (update :issue-proposal-log #(vec (or % [])))
      (update :issue-lifecycle-log #(vec (or % [])))
      (update :issue-uptake-log #(vec (or % [])))
      (update :issue-retrieval-log #(vec (or % [])))
      (update :issue-counter #(or % 0))
      (update :issue-event-counter #(or % 0))))

(defn- next-issue-id
  [world]
  (let [next-id (inc (or (:issue-counter world) 0))]
    [(assoc world :issue-counter next-id)
     (keyword (str "iss-" next-id))]))

(defn- next-issue-event-order
  [world]
  (let [next-order (inc (or (:issue-event-counter world) 0))]
    [(assoc world :issue-event-counter next-order) next-order]))

(defn- title-token-set
  [text]
  (->> (str/split (str/lower-case (or text "")) #"\W+")
       (map str/trim)
       (remove #(or (empty? %)
                    (contains? recoverability-stopwords %)))
       set))

(defn- proposal-quotes
  [proposal]
  (map :quote (:source/spans proposal)))

(defn- issue-normalized-label
  [proposal]
  (or (:normalized-label proposal)
      (namespaced-keyword "issue" (:issue/title proposal))
      :issue/unnamed))

(defn- normalize-tension
  [value]
  (cond
    (vector? value)
    (when (= 2 (count value))
      (namespaced-keyword "tension"
                          (str (first value) " vs " (second value))))

    (sequential? value)
    (when (= 2 (count value))
      (namespaced-keyword "tension"
                          (str (first value) " vs " (second value))))

    :else
    (namespaced-keyword "tension" value)))

(defn proposal-bridge-indices
  "Map source phrasing into shared bridge-index families."
  [proposal]
  (let [{:keys [topics tensions stakes actors projects tasks terms]}
        (:anchor/phrases proposal)
        normalize #(->> % (keep identity) vec)]
    {:topic (normalize (map #(namespaced-keyword "topic" %) topics))
     :tension (normalize (map normalize-tension tensions))
     :stake (normalize (map #(namespaced-keyword "stake" %) stakes))
     :actor (normalize (map #(namespaced-keyword "actor" %) actors))
     :project (normalize (map #(namespaced-keyword "project" %) projects))
     :task (normalize (map #(namespaced-keyword "task" %) tasks))
     :term (normalize (map #(namespaced-keyword "term" %) terms))}))

(defn flatten-content-cues
  [content-cues]
  (->> [:topic :tension :stake :actor :project :task :term]
       (mapcat #(get content-cues %))
       ordered-unique
       set))

(defn- proposal-support-tags
  [source-packet proposal status]
  (ordered-unique
   (concat [(namespaced-keyword "issue-kind" (name (:issue/kind proposal)))
            (namespaced-keyword "surface" (name (source-span-surface source-packet)))
            (namespaced-keyword "admission" (name status))]
           (when-let [timing (:timing/hint proposal)]
             [(namespaced-keyword "timing" (name timing))]))))

(defn- recoverable-title?
  [proposal]
  (let [title-tokens (title-token-set (:issue/title proposal))
        source-tokens (->> (concat (:source/phrasing proposal)
                                   (proposal-quotes proposal))
                           (mapcat #(title-token-set %))
                           set)]
    (seq (set/intersection title-tokens source-tokens))))

(defn- quoted-jargon-allowed?
  [proposal]
  (let [source-text (str/lower-case (str/join " " (concat (:source/phrasing proposal)
                                                          (proposal-quotes proposal))))
        title (str/lower-case (or (:issue/title proposal) ""))]
    (every? (fn [jargon]
              (or (not (str/includes? title jargon))
                  (str/includes? source-text jargon)))
            repo-jargon)))

(defn- proposal-unresolved?
  [proposal]
  (boolean
   (some #(re-find unresolvedness-regex (or % ""))
         (concat (proposal-quotes proposal)
                 [(:why/unresolved proposal)
                  (:why/now proposal)
                  (:issue/title proposal)]))))

(defn- validate-source-spans
  [source-packet proposal]
  (let [span-index (source-span-index source-packet)
        spans (:source/spans proposal)]
    (cond
      (< (count spans) 2)
      {:result :rejected
       :reasons [:insufficient-grounding-spans]}

      (< (count (distinct (map quoted-span->key spans))) 2)
      {:result :rejected
       :reasons [:duplicate-grounding-spans]}

      :else
      (let [mismatches (->> spans
                            (keep (fn [span]
                                    (let [source-span (get span-index (quoted-span->key span))]
                                      (when-not (= (:quote source-span) (:quote span))
                                        (quoted-span->key span)))))
                            vec)]
        (if (seq mismatches)
          {:result :rejected
           :reasons [:quoted-span-mismatch]
           :details {:mismatched-spans mismatches}}
          {:result :ok
           :reasons []})))))

(defn- settled-label?
  [source-packet normalized-label]
  (contains? (:settled-labels source-packet) normalized-label))

(defn- dedupe-key
  [proposal normalized-label]
  {:issue-kind (:issue/kind proposal)
   :normalized-label normalized-label
   :content-cues (flatten-content-cues (proposal-bridge-indices proposal))})

(defn- open-issue?
  [issue]
  (= :open (:issue/disposition issue)))

(defn- find-duplicate-issue-id
  [world dedupe-key]
  (->> (:issues world)
       (keep (fn [[issue-id issue]]
               (when (and (open-issue? issue)
                          (= dedupe-key (:issue/dedupe-key issue)))
                 issue-id)))
       sort
       first))

(defn- active-issue-duplicate?
  [world issue-id]
  (= :active (get-in world [:issues issue-id :issue/activation])))

(defn activation-score
  [world source-packet proposal normalized-label]
  (let [doc-count (->> (:source/spans proposal)
                       (map :doc)
                       distinct
                       count)
        source-surface-count (->> (:source/spans proposal)
                                  (map #(or (:surface %) (source-span-surface source-packet)))
                                  distinct
                                  count)
        dedupe-key* (dedupe-key proposal normalized-label)
        duplicate-id (find-duplicate-issue-id world dedupe-key*)
        active-bridge-cues (set (or (get-in source-packet [:metadata :active-bridge-cues])
                                    []))
        proposal-cues (flatten-content-cues (proposal-bridge-indices proposal))
        bridge-overlap? (seq (set/intersection proposal-cues active-bridge-cues))]
    (+ (if (contains? #{:decision :contradiction} (:issue/kind proposal)) 2 0)
       (if (:standing-note? source-packet) 2 0)
       (max 0 (dec doc-count))
       (if bridge-overlap? 1 0)
       (if (and duplicate-id (active-issue-duplicate? world duplicate-id)) -2 0)
       (if (= 1 source-surface-count) -2 0))))

(defn- append-proposal-log
  [world record]
  (update world :issue-proposal-log conj record))

(defn- append-lifecycle-event
  [world issue-id event data]
  (let [[world order] (next-issue-event-order world)
        record (merge {:issue-id issue-id
                       :event event
                       :event-order order
                       :cycle-or-session (or (:cycle world)
                                             (:session-id world)
                                             order)}
                      data)]
    [(update world :issue-lifecycle-log conj record)
     record]))

(defn- admit-issue
  [world source-packet proposal status activation activation-score*]
  (let [[world issue-id] (next-issue-id world)
        normalized-label (issue-normalized-label proposal)
        content-cues (proposal-bridge-indices proposal)
        issue (create-issue
               {:issue/id issue-id
                :issue/kind (:issue/kind proposal)
                :issue/status status
                :issue/activation activation
                :issue/title (:issue/title proposal)
                :issue/source-packet-id (:source-packet-id source-packet)
                :issue/source-spans (:source/spans proposal)
                :issue/source-phrasing (:source/phrasing proposal)
                :issue/normalized-label normalized-label
                :issue/content-cues content-cues
                :issue/content-cue-set (flatten-content-cues content-cues)
                :issue/support-tags (proposal-support-tags source-packet proposal status)
                :issue/provenance {:proposed-by :llm
                                   :validated-by :kernel
                                   :proposal-id (:proposal/id proposal)}
                :issue/activation-score activation-score*
                :issue/dedupe-key (dedupe-key proposal normalized-label)
                :issue/timing-hint (:timing/hint proposal)})]
    [(assoc-in world [:issues issue-id] issue) issue]))

(defn- maybe-activate-issue
  [world issue newly-active-count]
  (if (or (= :trace (:issue/status issue))
          (< (or (:issue/activation-score issue) 0) 4)
          (>= newly-active-count max-new-active-per-packet))
    [world issue newly-active-count]
    (let [issue-id (:issue/id issue)
          world (assoc-in world [:issues issue-id :issue/activation] :active)
          [world _] (append-lifecycle-event world
                                            issue-id
                                            :activated
                                            {:activation-score (:issue/activation-score issue)
                                             :related-object-id nil})]
      [world (assoc issue :issue/activation :active) (inc newly-active-count)])))

(defn- merge-into-issue
  [world issue-id proposal]
  (let [existing (get-in world [:issues issue-id])
        merged-spans (->> (concat (:issue/source-spans existing)
                                  (:source/spans proposal))
                          distinct
                          vec)
        merged-phrasing (ordered-unique (concat (:issue/source-phrasing existing)
                                                (:source/phrasing proposal)))
        world (-> world
                  (assoc-in [:issues issue-id :issue/source-spans] merged-spans)
                  (assoc-in [:issues issue-id :issue/source-phrasing] merged-phrasing)
                  (update-in [:issues issue-id :issue/corroboration-count] (fnil inc 1)))
        [world _] (append-lifecycle-event world
                                          issue-id
                                          :merged
                                          {:evidence {:proposal-id (:proposal/id proposal)
                                                      :source-span-ids (mapv :span-id
                                                                             (:source/spans proposal))}})]
    world))

(defn process-proposal-packet
  "Validate and admit a model proposal packet into the issue store.

  Returns `[world summary]`. The kernel:
  - logs every proposal
  - admits only trace/provisional issues
  - activates at most one issue per packet"
  [world source-packet proposal-packet]
  (let [world (initialize-issue-state world)
        source-packet (create-source-packet source-packet)
        proposal-packet (normalize-proposal-packet proposal-packet)]
    (loop [world world
           proposals (:proposals proposal-packet)
           provisional-admits 0
           newly-active 0
           summary {:packet-id (:packet/id proposal-packet)
                    :source-packet-id (:source/packet-id proposal-packet)
                    :proposal-count (count (:proposals proposal-packet))
                    :accepted-issue-ids []
                    :trace-issue-ids []
                    :merged-issue-ids []
                    :rejected-proposal-ids []}]
      (if-not (seq proposals)
        [world summary]
        (let [proposal (first proposals)
              normalized-label (issue-normalized-label proposal)
              dedupe-key* (dedupe-key proposal normalized-label)
              duplicate-id (find-duplicate-issue-id world dedupe-key*)
              span-validation (validate-source-spans source-packet proposal)
              activation-score* (activation-score world source-packet proposal normalized-label)
              base-log-record {:proposal-id (:proposal/id proposal)
                               :source-packet-id (:source/packet-id proposal-packet)
                               :issue-kind (:issue/kind proposal)
                               :source-span-ids (mapv :span-id (:source/spans proposal))
                               :normalized-label normalized-label
                               :bridge-indices (proposal-bridge-indices proposal)}]
          (cond
            (not= :issue (:proposal/type proposal))
            (recur (append-proposal-log world
                                        (assoc base-log-record
                                               :validator/result :rejected
                                               :validator/reasons [:unsupported-proposal-type]))
                   (rest proposals)
                   provisional-admits
                   newly-active
                   (update summary :rejected-proposal-ids conj (:proposal/id proposal)))

            (not (contains? allowed-issue-kinds (:issue/kind proposal)))
            (recur (append-proposal-log world
                                        (assoc base-log-record
                                               :validator/result :rejected
                                               :validator/reasons [:invalid-issue-kind]))
                   (rest proposals)
                   provisional-admits
                   newly-active
                   (update summary :rejected-proposal-ids conj (:proposal/id proposal)))

            (not= :ok (:result span-validation))
            (recur (append-proposal-log world
                                        (assoc base-log-record
                                               :validator/result :rejected
                                               :validator/reasons (:reasons span-validation)
                                               :validator/details (:details span-validation)))
                   (rest proposals)
                   provisional-admits
                   newly-active
                   (update summary :rejected-proposal-ids conj (:proposal/id proposal)))

            (not (proposal-unresolved? proposal))
            (let [[world issue] (admit-issue world
                                             source-packet
                                             proposal
                                             :trace
                                             :cold
                                             activation-score*)
                  [world _] (append-lifecycle-event world
                                                    (:issue/id issue)
                                                    :admitted
                                                    {:status :trace
                                                     :evidence {:proposal-id (:proposal/id proposal)}})
                  world (append-proposal-log world
                                             (assoc base-log-record
                                                    :validator/result :trace-only
                                                    :validator/reasons [:no-visible-unresolvedness]))]
              (recur world
                     (rest proposals)
                     provisional-admits
                     newly-active
                     (update summary :trace-issue-ids conj (:issue/id issue))))

            (not (recoverable-title? proposal))
            (recur (append-proposal-log world
                                        (assoc base-log-record
                                               :validator/result :rejected
                                               :validator/reasons [:title-not-recoverable]))
                   (rest proposals)
                   provisional-admits
                   newly-active
                   (update summary :rejected-proposal-ids conj (:proposal/id proposal)))

            (not (quoted-jargon-allowed? proposal))
            (recur (append-proposal-log world
                                        (assoc base-log-record
                                               :validator/result :rejected
                                               :validator/reasons [:title-jargon-not-grounded]))
                   (rest proposals)
                   provisional-admits
                   newly-active
                   (update summary :rejected-proposal-ids conj (:proposal/id proposal)))

            (settled-label? source-packet normalized-label)
            (recur (append-proposal-log world
                                        (assoc base-log-record
                                               :validator/result :rejected
                                               :validator/reasons [:already-settled]))
                   (rest proposals)
                   provisional-admits
                   newly-active
                   (update summary :rejected-proposal-ids conj (:proposal/id proposal)))

            duplicate-id
            (let [world (merge-into-issue world duplicate-id proposal)
                  world (append-proposal-log world
                                             (assoc base-log-record
                                                    :validator/result :merged
                                                    :validator/reasons [:duplicate-merged]
                                                    :merged-into duplicate-id))]
              (recur world
                     (rest proposals)
                     provisional-admits
                     newly-active
                     (update summary :merged-issue-ids conj duplicate-id)))

            :else
            (let [status (if (< provisional-admits max-provisional-admits-per-packet)
                           :provisional
                           :trace)
                  activation (if (= :trace status) :cold :warm)
                  [world issue] (admit-issue world
                                             source-packet
                                             proposal
                                             status
                                             activation
                                             activation-score*)
                  [world _] (append-lifecycle-event world
                                                    (:issue/id issue)
                                                    :admitted
                                                    {:status status
                                                     :evidence {:proposal-id (:proposal/id proposal)}})
                  [world issue newly-active]
                  (maybe-activate-issue world issue newly-active)
                  world (append-proposal-log world
                                             (assoc base-log-record
                                                    :validator/result (if (= :trace status)
                                                                        :trace-only
                                                                        :accepted)
                                                    :validator/reasons (if (= :trace status)
                                                                         [:packet-scarcity-cap]
                                                                         [:grounded-accepted])))]
              (recur world
                     (rest proposals)
                     (if (= :provisional status)
                       (inc provisional-admits)
                       provisional-admits)
                     newly-active
                     (cond-> summary
                       (= :trace status)
                       (update :trace-issue-ids conj (:issue/id issue))

                       (= :provisional status)
                       (update :accepted-issue-ids conj (:issue/id issue)))))))))))

(defn retrieve-issues
  "Retrieve issue objects via the shared bridge-index layer.

  Query cues must satisfy one of:
  - topic + tension
  - topic + stake
  - topic + actor + project"
  ([world query-cues]
   (retrieve-issues world query-cues {}))
  ([world query-cues {:keys [max-results related-object-id]
                      :or {max-results 5}}]
   (let [world (initialize-issue-state world)
         query-cues (proposal-bridge-indices {:anchor/phrases query-cues})
         query-topic (set (:topic query-cues))
         query-tension (set (:tension query-cues))
         query-stake (set (:stake query-cues))
         query-actor (set (:actor query-cues))
         query-project (set (:project query-cues))
         query-all (flatten-content-cues query-cues)
         valid-query? (or (and (seq query-topic) (seq query-tension))
                          (and (seq query-topic) (seq query-stake))
                          (and (seq query-topic) (seq query-actor) (seq query-project)))
         hits (if-not valid-query?
                []
                (->> (:issues world)
                     vals
                     (filter open-issue?)
                     (map (fn [issue]
                            (let [overlap (set/intersection query-all
                                                            (:issue/content-cue-set issue))
                                  overlap-groups (select-keys
                                                  (into {}
                                                        (map (fn [[k cues]]
                                                               [k (vec (set/intersection
                                                                        (set cues)
                                                                        overlap))]))
                                                        (:issue/content-cues issue))
                                                  [:topic :tension :stake :actor :project :task :term])
                                  valid-overlap? (or (and (seq (:topic overlap-groups))
                                                          (seq (:tension overlap-groups)))
                                                     (and (seq (:topic overlap-groups))
                                                          (seq (:stake overlap-groups)))
                                                     (and (seq (:topic overlap-groups))
                                                          (seq (:actor overlap-groups))
                                                          (seq (:project overlap-groups))))]
                              (when (and valid-overlap? (seq overlap))
                                {:issue-id (:issue/id issue)
                                 :status (:issue/status issue)
                                 :activation (:issue/activation issue)
                                 :score (+ (count overlap)
                                           (if (= :active (:issue/activation issue)) 1 0)
                                           (if (= :durable (:issue/status issue)) 1 0))
                                 :overlap overlap-groups
                                 :origin :issue}))))
                     (remove nil?)
                     (sort-by (juxt (comp - :score)
                                    (comp - #(case % :active 2 :warm 1 0) :activation)
                                    (comp - #(case % :durable 2 :provisional 1 0) :status)
                                    (comp str :issue-id)))
                     (take max-results)
                     vec))]
     (if-not (seq hits)
       [world []]
       (let [[world logged-hits]
             (reduce (fn [[current-world acc] hit]
                       (let [issue-id (:issue-id hit)
                             [current-world _]
                             (append-lifecycle-event current-world
                                                     issue-id
                                                     :retrieved
                                                     {:related-object-id related-object-id
                                                      :evidence {:overlap (:overlap hit)}})
                             retrieval-record {:issue-id issue-id
                                               :related-object-id related-object-id
                                               :overlap (:overlap hit)
                                               :score (:score hit)}]
                         [(update current-world :issue-retrieval-log conj retrieval-record)
                          (conj acc hit)]))
                     [world []]
                     hits)]
         [world logged-hits])))))

(defn record-issue-uptake
  [world issue-id signal context]
  (let [world (initialize-issue-state world)
        signal (as-keyword signal)]
    (when-not (contains? allowed-uptake-signals signal)
      (throw (ex-info "Invalid issue uptake signal"
                      {:signal signal})))
    (let [world (update-in world [:issues issue-id :issue/human-uptake signal] (fnil inc 0))
          uptake-record {:issue-id issue-id
                         :signal signal
                         :session-id (or (:session-id world) (:cycle world))
                         :context context}]
      (-> world
          (update :issue-uptake-log conj uptake-record)
          (append-proposal-log {:proposal-id nil
                                :source-packet-id nil
                                :issue-kind nil
                                :source-span-ids []
                                :normalized-label nil
                                :bridge-indices {}
                                :validator/result :uptake
                                :validator/reasons [signal]})))))

(defn issue-counters
  [world]
  (let [proposal-log (:issue-proposal-log world)
        lifecycle-log (:issue-lifecycle-log world)
        uptake-log (:issue-uptake-log world)]
    {:proposal-count (count (filter #(some? (:proposal-id %)) proposal-log))
     :abstain-count (count (filter #(= :abstain (:validator/result %)) proposal-log))
     :ungrounded-reject-count (count (filter #(contains? (set (:validator/reasons %))
                                                         :insufficient-grounding-spans)
                                             proposal-log))
     :duplicate-merge-count (count (filter #(= :merged (:validator/result %)) proposal-log))
     :settled-reject-count (count (filter #(contains? (set (:validator/reasons %))
                                                      :already-settled)
                                          proposal-log))
     :trace-only-count (count (filter #(= :trace-only (:validator/result %)) proposal-log))
     :provisional-admit-count (count (filter #(= :provisional (:issue/status %))
                                             (vals (:issues world))))
     :active-count (count (filter #(= :active (:issue/activation %))
                                  (vals (:issues world))))
     :issue-retrieval-count (count (filter #(= :retrieved (:event %)) lifecycle-log))
     :issue->episode-bridge-count 0
     :issue->issue-bridge-count (count (:issue-retrieval-log world))
     :promotion-count (count (filter #(= :promoted (:event %)) lifecycle-log))
     :demotion-count (count (filter #(= :demoted (:event %)) lifecycle-log))
     :same-surface-loop-count (count (filter #(= :same-surface-loop (:event %)) lifecycle-log))
     :uptake-count (count uptake-log)}))
