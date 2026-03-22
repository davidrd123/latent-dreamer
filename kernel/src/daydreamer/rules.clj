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
