(ns daydreamer.rules
  "Minimal RuleV1 support for structurally declared, instantiate-only rules.

  This first slice is intentionally narrow: it validates the basic rule shape
  and instantiates consequent schemas from explicit bindings. Matching stays in
  family-specific code until the rule substrate grows a shared matcher."
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
