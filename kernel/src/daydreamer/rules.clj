(ns daydreamer.rules
  "Minimal RuleV1 support for structurally declared rules.

  This first slice is intentionally narrow: it validates the basic rule shape
  and instantiates consequent schemas from explicit bindings. Matching is still
  subset-based and explicit, but it now lives in this shared substrate instead
  of family-specific scans."
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.walk :as walk]))

(def ^:private rule-kinds
  #{:inference :planning})

(def ^:private mueller-modes
  #{:plan-only :inference-only :both})

(def ^:private executor-kinds
  #{:instantiate :clojure-fn :llm-backed})

(def ^:private required-rule-keys
  #{:id
    :rule-kind
    :mueller-mode
    :antecedent-schema
    :consequent-schema
    :plausibility
    :index-projections
    :denotation
    :executor
    :graph-cache
    :provenance})

(defn logic-var?
  [value]
  (and (symbol? value)
       (str/starts-with? (name value) "?")))

(defn valid-rule?
  [rule]
  (and (map? rule)
       (set/subset? required-rule-keys (set (keys rule)))
       (contains? rule-kinds (:rule-kind rule))
       (contains? mueller-modes (:mueller-mode rule))
       (vector? (:antecedent-schema rule))
       (vector? (:consequent-schema rule))
       (number? (:plausibility rule))
       (map? (:index-projections rule))
       (map? (:denotation rule))
       (map? (:executor rule))
       (contains? executor-kinds (get-in rule [:executor :kind]))
       (map? (:graph-cache rule))
       (map? (:provenance rule))))

(defn validate-rule!
  [rule]
  (when-not (valid-rule? rule)
    (throw (ex-info "Invalid RuleV1"
                    {:rule-id (:id rule)
                     :rule rule})))
  rule)

(defn instantiate-template
  [template bindings]
  (walk/postwalk
   (fn [node]
     (if (logic-var? node)
       (if (contains? bindings node)
         (get bindings node)
         (throw (ex-info "Missing binding for logic variable"
                         {:variable node
                          :bindings bindings})))
       node))
   template))

(defn- unify-node
  [pattern value bindings]
  (cond
    (logic-var? pattern)
    (if (contains? bindings pattern)
      (when (= (get bindings pattern) value)
        bindings)
      (assoc bindings pattern value))

    (map? pattern)
    (when (map? value)
      (reduce-kv (fn [acc key nested-pattern]
                   (if (nil? acc)
                     (reduced nil)
                     (if (contains? value key)
                       (unify-node nested-pattern (get value key) acc)
                       (reduced nil))))
                 bindings
                 pattern))

    (vector? pattern)
    (when (and (vector? value)
               (= (count pattern) (count value)))
      (reduce (fn [acc [nested-pattern nested-value]]
                (if (nil? acc)
                  (reduced nil)
                  (unify-node nested-pattern nested-value acc)))
              bindings
              (map vector pattern value)))

    :else
    (when (= pattern value)
      bindings)))

(defn match-fact-pattern
  ([pattern fact]
   (match-fact-pattern pattern fact {}))
  ([pattern fact initial-bindings]
   (unify-node pattern fact initial-bindings)))

(defn- candidate-facts
  [pattern facts]
  (let [expected-type (when (map? pattern)
                        (:fact/type pattern))]
    (->> facts
         (keep-indexed (fn [idx fact]
                         (when (or (not (keyword? expected-type))
                                   (= expected-type (:fact/type fact)))
                           [idx fact]))))))

(defn- remove-fact-at
  [facts idx]
  (into (subvec facts 0 idx)
        (subvec facts (inc idx))))

(defn match-antecedent-schema
  ([patterns facts]
   (match-antecedent-schema patterns facts {}))
  ([patterns facts initial-bindings]
   (let [patterns (vec patterns)
         facts (vec facts)]
     (letfn [(search [remaining-patterns available-facts bindings matched-facts]
               (if (empty? remaining-patterns)
                 [{:bindings bindings
                   :matched-facts matched-facts}]
                 (mapcat (fn [[idx fact]]
                           (when-let [next-bindings (match-fact-pattern
                                                     (first remaining-patterns)
                                                     fact
                                                     bindings)]
                             (search (subvec remaining-patterns 1)
                                     (remove-fact-at available-facts idx)
                                     next-bindings
                                     (conj matched-facts fact))))
                         (candidate-facts (first remaining-patterns)
                                          available-facts))))]
       (search patterns facts initial-bindings [])))))

(defn match-rule
  ([rule facts]
   (match-rule rule facts {}))
  ([rule facts initial-bindings]
   (validate-rule! rule)
   (match-antecedent-schema (:antecedent-schema rule)
                            facts
                            initial-bindings)))

(defn graphable-pattern?
  [pattern]
  (and (map? pattern)
       (keyword? (:fact/type pattern))))

(defn projection-basis
  [pattern]
  (when (graphable-pattern? pattern)
    {:fact-type (:fact/type pattern)
     :projection pattern
     :required-keys (->> (keys pattern)
                         (remove #{:fact/type})
                         set)
     :open-fields (->> pattern
                       (keep (fn [[key value]]
                               (when (logic-var? value)
                                 key)))
                       set)
     :constant-fields (into {}
                            (keep (fn [[key value]]
                                    (when (and (not= key :fact/type)
                                               (not (logic-var? value)))
                                      [key value])))
                            pattern)}))

(defn derive-graph-cache
  [rule]
  (validate-rule! rule)
  (assoc rule
         :graph-cache
         {:out-edge-bases (->> (:consequent-schema rule)
                               (keep projection-basis)
                               vec)
          :in-edge-bases (->> (:antecedent-schema rule)
                              (keep projection-basis)
                              vec)}))

(defn- compatible-constant-fields?
  [left right]
  (every? (fn [shared-key]
            (= (get left shared-key)
               (get right shared-key)))
          (set/intersection (set (keys left))
                            (set (keys right)))))

(defn compatible-projection-bases?
  [from-basis to-basis]
  (and (= (:fact-type from-basis)
          (:fact-type to-basis))
       (compatible-constant-fields? (:constant-fields from-basis)
                                    (:constant-fields to-basis))))

(defn- infer-edge-kind
  [from-basis]
  (case (:fact-type from-basis)
    :goal :goal-decomposition
    :emotion :emotion-trigger
    :repair-step :repair-step
    :state-transition))

(defn connection-edge-basis
  [from-rule to-rule from-basis to-basis]
  (when (compatible-projection-bases? from-basis to-basis)
    {:from-rule (:id from-rule)
     :to-rule (:id to-rule)
     :from-projection (:projection from-basis)
     :to-projection (:projection to-basis)
     :bindings {}
     :shared-keys (set/intersection (:required-keys from-basis)
                                    (:required-keys to-basis))
     :edge-kind (infer-edge-kind from-basis)}))

(defn connection-edges
  [rules]
  (let [rules (mapv derive-graph-cache rules)]
    (->> (for [from-rule rules
               to-rule rules
               :when (not= (:id from-rule) (:id to-rule))
               from-basis (get-in from-rule [:graph-cache :out-edge-bases])
               to-basis (get-in to-rule [:graph-cache :in-edge-bases])
               :let [edge (connection-edge-basis
                           from-rule
                           to-rule
                           from-basis
                           to-basis)]
               :when edge]
           edge)
         distinct
         (sort-by (juxt (comp str :from-rule)
                        (comp str :to-rule)
                        (comp str :edge-kind)
                        (comp pr-str :from-projection)
                        (comp pr-str :to-projection)))
         vec)))

(defn build-connection-graph
  [rules]
  (let [rules (mapv derive-graph-cache rules)
        edges (connection-edges rules)]
    {:rules rules
     :rules-by-id (into {} (map (juxt :id identity)) rules)
     :edges edges
     :outgoing (group-by :from-rule edges)
     :incoming (group-by :to-rule edges)}))

(defn outgoing-edges
  [graph rule-id]
  (vec (get-in graph [:outgoing rule-id] [])))

(defn incoming-edges
  [graph rule-id]
  (vec (get-in graph [:incoming rule-id] [])))

(defn explain-path
  [graph rule-path]
  (let [rule-path (vec rule-path)
        edge-pairs (partition 2 1 rule-path)
        edges (mapv (fn [[from-rule to-rule]]
                      (some (fn [edge]
                              (when (= to-rule (:to-rule edge))
                                edge))
                            (outgoing-edges graph from-rule)))
                    edge-pairs)]
    (when (every? some? edges)
      {:rule-path rule-path
       :edges edges
       :fact-types (mapv (comp :fact/type :from-projection) edges)
       :edge-kinds (mapv :edge-kind edges)
       :shared-keys (mapv :shared-keys edges)})))

(defn reachable-paths
  ([graph start-rule-id]
   (reachable-paths graph start-rule-id {:max-depth 3}))
  ([graph start-rule-id {:keys [max-depth]
                         :or {max-depth 3}}]
   (letfn [(walk-paths [current-rule-id current-path visited remaining-depth]
             (if (<= remaining-depth 0)
               []
               (mapcat (fn [edge]
                         (let [next-rule-id (:to-rule edge)
                               next-path (conj current-path next-rule-id)
                               explained (explain-path graph next-path)]
                           (if (contains? visited next-rule-id)
                             (if explained
                               [explained]
                               [])
                             (let [descendants (walk-paths next-rule-id
                                                           next-path
                                                           (conj visited next-rule-id)
                                                           (dec remaining-depth))]
                               (cond-> []
                                 explained
                                 (conj explained)

                                 (seq descendants)
                                 (into descendants))))))
                       (outgoing-edges graph current-rule-id))))]
     (->> (walk-paths start-rule-id
                      [start-rule-id]
                      #{start-rule-id}
                      max-depth)
          (map (fn [path]
                 (assoc path :depth (dec (count (:rule-path path))))))
          distinct
          (sort-by (juxt :depth
                         (comp pr-str :rule-path)))
          vec))))

(defn instantiate-rule
  [rule bindings]
  (validate-rule! rule)
  (when-not (= :instantiate (get-in rule [:executor :kind]))
    (throw (ex-info "instantiate-rule only supports :instantiate executors"
                    {:rule-id (:id rule)
                     :executor-kind (get-in rule [:executor :kind])})))
  {:consequents (mapv #(instantiate-template % bindings)
                      (:consequent-schema rule))
   :confidence (double (:plausibility rule))
   :reason (str (or (get-in rule [:denotation :intended-effect])
                    (:id rule)))
   :aux-indices (mapv #(instantiate-template % bindings)
                      (get-in rule [:index-projections :emit] []))
   :surface-summary nil})
