(ns daydreamer.rules
  "Minimal RuleV1 support for structurally declared rules.

  This first slice is intentionally narrow: it validates the basic rule shape
  and instantiates consequent schemas from explicit bindings. Matching is still
  subset-based and explicit, but it now lives in this shared substrate instead
  of family-specific scans."
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [daydreamer.context :as cx]
            [daydreamer.goals :as goals]))

;; --- Allowed values for rule fields ---
;; These sets define what's valid. A rule with :rule-kind :banana fails validation.

(def ^:private rule-kinds
  #{:inference    ; fires automatically when antecedents are present (forward chaining)
    :planning})   ; fires during goal decomposition (planner invokes it)

(def ^:private mueller-modes
  #{:plan-only       ; only usable as a planning rule
    :inference-only  ; only usable as an inference rule
    :both})          ; can serve as either

(def ^:private executor-kinds
  #{:instantiate  ; fill in consequent template from bindings (Mueller default)
    :clojure-fn   ; run a Clojure function that returns RuleResultV1
    :llm-backed}) ; call an LLM and validate the response

;; Every RuleV1 must have all of these keys. Validation rejects anything missing.
(def ^:private required-rule-keys
  #{:id                  ; unique keyword like :rule/unanswered-repair-bid
    :rule-kind           ; :inference or :planning
    :mueller-mode        ; :plan-only, :inference-only, or :both
    :antecedent-schema   ; vector of patterns that must match for rule to fire
    :consequent-schema   ; vector of patterns the rule produces (graphable shape)
    :plausibility        ; 0.0-1.0, used for realism scoring
    :index-projections   ; {:match [...] :emit [...]} for episode retrieval/storage
    :denotation          ; what state change this rule is SUPPOSED to accomplish
    :executor            ; {:kind :instantiate/:clojure-fn/:llm-backed, :spec ...}
    :graph-cache         ; derived edges — rebuilt from schemas, not source of truth
    :provenance})        ; where this rule came from (book anchors, kernel status)

(def ^:private required-denotation-keys
  #{:intended-effect
    :failure-modes
    :validation-fn})

(def ^:private required-rule-result-keys
  #{:consequents
    :confidence
    :reason
    :aux-indices
    :surface-summary})

(def ^:private rule-access-statuses
  #{:accessible :frontier :quarantined})

(defn logic-var?
  "Is this a logic variable? Convention: symbols starting with ? like '?target"
  [value]
  (and (symbol? value)
       (str/starts-with? (name value) "?")))

(defn- valid-denotation?
  [denotation]
  (and (map? denotation)
       (set/subset? required-denotation-keys
                    (set (keys denotation)))
       (keyword? (:intended-effect denotation))
       (vector? (:failure-modes denotation))
       (every? keyword? (:failure-modes denotation))
       (or (nil? (:validation-fn denotation))
           (ifn? (:validation-fn denotation)))))

(defn valid-rule?
  "Check that a rule has all required fields with valid types.
  Returns true/false — use validate-rule! for the throwing version."
  [rule]
  (and (map? rule)
       (set/subset? required-rule-keys (set (keys rule)))
       (contains? rule-kinds (:rule-kind rule))
       (contains? mueller-modes (:mueller-mode rule))
       (vector? (:antecedent-schema rule))
       (vector? (:consequent-schema rule))
       (number? (:plausibility rule))
       (map? (:index-projections rule))
       (valid-denotation? (:denotation rule))
       (map? (:executor rule))
       (contains? executor-kinds (get-in rule [:executor :kind]))
       (or (nil? (get-in rule [:executor :spec :effect-ops]))
           (and (vector? (get-in rule [:executor :spec :effect-ops]))
                (every? keyword? (get-in rule [:executor :spec :effect-ops]))))
       (or (not (contains? rule :effect-schema))
           (and (vector? (:effect-schema rule))
                (every? map? (:effect-schema rule))
                (every? keyword? (map :op (:effect-schema rule)))
                (if-let [declared-effect-ops (get-in rule [:executor :spec :effect-ops])]
                  (= declared-effect-ops
                     (mapv :op (:effect-schema rule)))
                  true)))
       (map? (:graph-cache rule))
       (map? (:provenance rule))))

(defn validate-rule!
  "Like valid-rule? but throws on failure. Returns the rule on success,
  so you can use it inline: (-> rule validate-rule! do-stuff)"
  [rule]
  (when-not (valid-rule? rule)
    (throw (ex-info "Invalid RuleV1"
                    {:rule-id (:id rule)
                     :rule rule})))
  rule)

(defn- normalize-rule-call
  [rule call]
  (let [call (cond
               (nil? call)
               {}

               (and (map? call)
                    (or (contains? call :bindings)
                        (contains? call :matched-facts)
                        (contains? call :rule-id)))
               call

               (map? call)
               {:bindings call}

               :else
               (throw (ex-info "Invalid RuleCallV1"
                               {:rule-id (:id rule)
                                :call call})))]
    (when-not (map? (or (:bindings call) {}))
      (throw (ex-info "RuleCallV1 bindings must be a map"
                      {:rule-id (:id rule)
                       :call call})))
    (-> call
        (update :bindings #(or % {}))
        (update :matched-facts #(vec (or % [])))
        (assoc :rule-id (:id rule)))))

(defn instantiate-template
  "Walk a template (map, vector, nested structure) and replace every
  logic variable with its bound value. Used to fill in consequent schemas
  after matching produces bindings.

  Example:
    (instantiate-template {:target-ref '?target} {'?target :sister})
    => {:target-ref :sister}"
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
  "Try to match a single pattern against a single value, threading bindings.
  Returns updated bindings on success, nil on failure.

  Four cases:
  1. Logic variable (?x) — bind if new, check consistency if already bound
  2. Map — subset match: every key in the pattern must exist in the value
  3. Vector — exact shape: same length, each element must unify
  4. Literal — must be equal"
  [pattern value bindings]
  (cond
    ;; Case 1: ?variable — either bind it or check it matches existing binding
    (logic-var? pattern)
    (if (contains? bindings pattern)
      ;; Already bound: the value must equal the existing binding, or fail
      (when (= (get bindings pattern) value)
        bindings)
      ;; Not bound yet: record this binding
      (assoc bindings pattern value))

    ;; Case 2: map pattern — every key in the pattern must exist in the value
    ;; and each nested value must unify. Extra keys in the value are fine.
    (map? pattern)
    (when (map? value)
      (reduce-kv (fn [acc key nested-pattern]
                   (if (nil? acc)
                     (reduced nil)  ; previous key failed — short-circuit
                     (if (contains? value key)
                       (unify-node nested-pattern (get value key) acc)
                       (reduced nil))))  ; key missing from value — fail
                 bindings
                 pattern))

    ;; Case 3: vector pattern — both must be vectors of same length
    (vector? pattern)
    (when (and (vector? value)
               (= (count pattern) (count value)))
      (reduce (fn [acc [nested-pattern nested-value]]
                (if (nil? acc)
                  (reduced nil)
                  (unify-node nested-pattern nested-value acc)))
              bindings
              (map vector pattern value)))

    ;; Case 4: literal — must be exactly equal
    :else
    (when (= pattern value)
      bindings)))

(defn match-fact-pattern
  "Try to match one pattern against one fact. Returns bindings on success, nil on failure.
  Optionally takes initial bindings to enforce consistency with earlier matches."
  ([pattern fact]
   (match-fact-pattern pattern fact {}))
  ([pattern fact initial-bindings]
   (unify-node pattern fact initial-bindings)))

(defn- candidate-facts
  "Pre-filter: only try facts whose :fact/type matches the pattern's.
  Returns [index, fact] pairs so we can track which facts are used."
  [pattern facts]
  (let [expected-type (when (map? pattern)
                        (:fact/type pattern))]
    (->> facts
         (keep-indexed (fn [idx fact]
                         (when (or (not (keyword? expected-type))
                                   (= expected-type (:fact/type fact)))
                           [idx fact]))))))

(defn- remove-fact-at
  "Remove the fact at idx from the vector. Used to prevent the same
  fact from matching two different patterns in the same antecedent."
  [facts idx]
  (into (subvec facts 0 idx)
        (subvec facts (inc idx))))

(defn match-antecedent-schema
  "Find all ways to satisfy multiple patterns against a set of facts.

  Each pattern must match a DIFFERENT fact, and logic variables shared
  across patterns must bind consistently. Returns a vector of
  {:bindings {?var value}, :matched-facts [fact1 fact2 ...]} for every
  valid combination.

  The search is depth-first with immutable bindings — each branch gets
  its own bindings map, so backtracking is free (no state to undo)."
  ([patterns facts]
   (match-antecedent-schema patterns facts {}))
  ([patterns facts initial-bindings]
   (let [patterns (vec patterns)
         facts (vec facts)]
     (letfn [(search [remaining-patterns available-facts bindings matched-facts]
               (if (empty? remaining-patterns)
                 ;; All patterns matched — emit this solution
                 [{:bindings bindings
                   :matched-facts matched-facts}]
                 ;; Try each candidate fact for the next pattern
                 (mapcat (fn [[idx fact]]
                           (when-let [next-bindings (match-fact-pattern
                                                     (first remaining-patterns)
                                                     fact
                                                     bindings)]
                             ;; This fact matched — remove it so later patterns
                             ;; can't reuse it, and recurse with updated bindings
                             (search (subvec remaining-patterns 1)
                                     (remove-fact-at available-facts idx)
                                     next-bindings
                                     (conj matched-facts fact))))
                         ;; Pre-filter: only try facts whose :fact/type matches
                         (candidate-facts (first remaining-patterns)
                                          available-facts))))]
       (search patterns facts initial-bindings [])))))

(defn match-rule
  "Match a rule's antecedent schema against a set of facts.
  Validates the rule first, then delegates to match-antecedent-schema.
  Returns all valid binding sets (may be empty if no match)."
  ([rule facts]
   (match-rule rule facts {}))
  ([rule facts initial-bindings]
   (validate-rule! rule)
   (match-antecedent-schema (:antecedent-schema rule)
                            facts
                            initial-bindings)))

(defn graphable-pattern?
  "Can this pattern participate in the connection graph?
  Only map patterns with a keyword :fact/type are graphable —
  bare values, vectors, and untyped maps are not."
  [pattern]
  (and (map? pattern)
       (keyword? (:fact/type pattern))))

(defn projection-basis
  "Analyze a pattern for graph-connection purposes.

  Separates a pattern into:
  - :open-fields    — keys with logic vars (holes to fill at runtime)
  - :constant-fields — keys with fixed values (must match for an edge to form)

  Example:
    {:fact/type :goal, :goal-id '?gid, :status :failed}
    => {:fact-type :goal
        :open-fields #{:goal-id}
        :constant-fields {:status :failed}}

  Two rules connect when one's output and the other's input have
  the same :fact-type AND compatible constant fields."
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
  "Fill in a rule's :graph-cache from its schemas.

  :out-edge-bases — what this rule PRODUCES (from consequent patterns)
  :in-edge-bases  — what this rule NEEDS (from antecedent patterns)

  Edges form when one rule's out-basis matches another's in-basis."
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
  "Do two sets of constant fields agree on their shared keys?
  {:status :failed} and {:status :failed, :type :goal} — yes (shared key :status matches)
  {:status :failed} and {:status :succeeded} — no (shared key :status conflicts)"
  [left right]
  (every? (fn [shared-key]
            (= (get left shared-key)
               (get right shared-key)))
          (set/intersection (set (keys left))
                            (set (keys right)))))

(defn- shared-required-keys
  [from-basis to-basis]
  (set/intersection (:required-keys from-basis)
                    (:required-keys to-basis)))

(defn compatible-projection-bases?
  "Can these two projections form a candidate edge?
  Requires the same :fact/type, at least one shared structural key, and no
  conflicting constant fields on those shared keys."
  [from-basis to-basis]
  (let [shared-keys (shared-required-keys from-basis to-basis)]
    (and (= (:fact-type from-basis)
            (:fact-type to-basis))
         (seq shared-keys)
         (compatible-constant-fields? (:constant-fields from-basis)
                                      (:constant-fields to-basis)))))

(defn- infer-edge-kind
  "Label the edge based on what kind of fact flows through it."
  [from-basis]
  (case (:fact-type from-basis)
    :goal :goal-decomposition
    :emotion :emotion-trigger
    :repair-step :repair-step
    :state-transition))

(defn connection-edge-basis
  "Build one edge between two rules if their projections are compatible.
  Returns nil if they can't connect (different fact types or conflicting constants)."
  [from-rule to-rule from-basis to-basis]
  (let [shared-keys (shared-required-keys from-basis to-basis)]
    (when (and (seq shared-keys)
               (compatible-projection-bases? from-basis to-basis))
      {:from-rule (:id from-rule)
       :to-rule (:id to-rule)
       :from-projection (:projection from-basis)  ; what from-rule produces
       :to-projection (:projection to-basis)       ; what to-rule needs
       :bindings {}
       :shared-keys shared-keys
       :edge-kind (infer-edge-kind from-basis)})))

(defn connection-edges
  "Compute all edges in the rule connection graph.

  For every pair of rules A→B, check if any of A's output projections
  (from consequent schema) is compatible with any of B's input projections
  (from antecedent schema). Compatible means: same :fact/type AND any
  shared structural keys exist AND any shared constant fields have the same
  value.

  This is the O(n²) pairwise comparison. Fine for hundreds of rules.
  The graph is a candidate-adjacency graph: it says one rule's output could
  feed another rule's input, not that the full downstream rule is verified
  fireable in context."
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
  "Build the full connection graph over a set of rules.

  Returns:
  - :rules       — the rules with graph caches filled in
  - :rules-by-id — lookup table: rule-id → rule
  - :edges       — flat list of all edges
  - :outgoing    — {rule-id → [edges FROM this rule]}
  - :incoming    — {rule-id → [edges TO this rule]}

  This graph is a candidate-adjacency substrate for later serendipity search.
  It is structurally derived and does not change based on usage — a
  never-traversed path is equally findable as one used a hundred times."
  [rules]
  (let [rules (mapv derive-graph-cache rules)
        edges (connection-edges rules)]
    {:rules rules
     :rules-by-id (into {} (map (juxt :id identity)) rules)
     :edges edges
     :outgoing (group-by :from-rule edges)
     :incoming (group-by :to-rule edges)}))

(defn outgoing-edges
  "What rules can fire AFTER this one? (this rule's output feeds their input)"
  [graph rule-id]
  (vec (get-in graph [:outgoing rule-id] [])))

(defn incoming-edges
  "What rules must fire BEFORE this one? (their output feeds this rule's input)"
  [graph rule-id]
  (vec (get-in graph [:incoming rule-id] [])))

(defn explain-path
  "Given a sequence of rule IDs, explain the edges connecting them.
  Returns nil if any hop in the path has no edge (broken path)."
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
  "Walk the connection graph from a starting rule up to max-depth hops.
  Returns all reachable paths with explanations (which edges, which fact types).
  These are candidate paths through rule adjacency, not yet verified firing
  sequences."
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

(defn bridge-paths
  "Find paths of length 2-4 between two specific rules.
  This is the serendipity question: 'is there a non-obvious connection
  between my current concern and this salient thing?' The result is a candidate
  bridge, not yet a verified executable chain."
  ([graph start-rule-id target-rule-id]
   (bridge-paths graph
                 start-rule-id
                 target-rule-id
                 {:min-depth 2
                  :max-depth 4}))
  ([graph start-rule-id target-rule-id {:keys [min-depth max-depth]
                                        :or {min-depth 2
                                             max-depth 4}}]
   (->> (reachable-paths graph start-rule-id {:max-depth max-depth})
        (filter #(= target-rule-id
                    (last (:rule-path %))))
        (filter #(<= min-depth
                     (:depth %)
                     max-depth))
        vec)))

(defn remove-rules
  "Remove rules by ID. Used by intervention-delta to test what breaks."
  [rules rule-ids]
  (let [rule-id-set (set rule-ids)]
    (->> rules
         (remove #(contains? rule-id-set (:id %)))
         vec)))

(defn graph-without-rules
  "Rebuild the connection graph with some rules removed."
  [graph rule-ids]
  (build-connection-graph
   (remove-rules (:rules graph) rule-ids)))

(defn- rules-from-source
  [graph-or-rules]
  (cond
    (vector? graph-or-rules)
    graph-or-rules

    (map? graph-or-rules)
    (vec (:rules graph-or-rules))

    :else
    []))

(defn- deployment-role
  [rule]
  (get-in rule [:provenance :deployment-role] :authored-core))

(defn- default-rule-access-status
  [rule]
  (case (deployment-role rule)
    (:authored-core :core) :accessible
    (:authored-frontier :frontier :induced) :frontier
    :quarantined :quarantined
    :quarantined))

(defn- default-rule-access-entry
  [world rule]
  (let [status (default-rule-access-status rule)
        source (deployment-role rule)]
    {:status status
     :source source
     :opened-cycle (when (= :accessible status)
                     (:cycle world))
     :opened-by nil
     :history []}))

(defn- default-derived-rule-access-entry?
  [entry]
  (and entry
       (empty? (:history entry))
       (nil? (:opened-by entry))))

(defn- merged-rule-access-entry
  [world rule explicit-entry]
  (let [default-entry (default-rule-access-entry world rule)
        default-source (:source default-entry)]
    (cond
      (nil? explicit-entry)
      default-entry

      (and (default-derived-rule-access-entry? explicit-entry)
           (or (not= (:source explicit-entry) default-source)
               (not= (:status explicit-entry)
                     (default-rule-access-status rule))))
      default-entry

      :else
      (assoc explicit-entry :source default-source))))

(defn rule-access-registry
  "Return the effective rule-access registry for the given structural rule set.

  World state overrides defaults. Rules without explicit world entries derive
  their status from provenance deployment metadata. Default-derived entries are
  refreshed when the rule's deployment metadata changes; entries with explicit
  runtime history keep their dynamic status."
  [world graph-or-rules]
  (let [explicit (or (:rule-access world) {})]
    (reduce (fn [registry rule]
              (assoc registry
                     (:id rule)
                     (merged-rule-access-entry
                      world
                      rule
                      (get registry (:id rule)))))
            explicit
            (rules-from-source graph-or-rules))))

(defn sync-rule-access-registry
  "Materialize missing default rule-access entries into world state."
  [world graph-or-rules]
  (assoc world :rule-access (rule-access-registry world graph-or-rules)))

(defn rule-access-info
  "Return the effective access entry for a rule in the given structural rule set."
  [world graph-or-rules rule-id]
  (get (rule-access-registry world graph-or-rules) rule-id))

(defn accessible-rule-ids
  [world graph-or-rules]
  (->> (rule-access-registry world graph-or-rules)
       (keep (fn [[rule-id entry]]
               (when (= :accessible (:status entry))
                 rule-id)))
       set))

(defn frontier-rule-ids
  [world graph-or-rules]
  (->> (rule-access-registry world graph-or-rules)
       (keep (fn [[rule-id entry]]
               (when (= :frontier (:status entry))
                 rule-id)))
       set))

(defn quarantined-rule-ids
  [world graph-or-rules]
  (->> (rule-access-registry world graph-or-rules)
       (keep (fn [[rule-id entry]]
               (when (= :quarantined (:status entry))
                 rule-id)))
       set))

(defn planning-graph
  "Filter a structural graph to the planner-visible rule set."
  [world graph]
  (graph-without-rules
   graph
   (->> (:rules graph)
        (keep (fn [rule]
                (let [status (:status (rule-access-info world graph (:id rule)))]
                  (when (not= :accessible status)
                    (:id rule)))))
        vec)))

(defn serendipity-graph
  "Filter a structural graph to the serendipity-visible rule set.

  Serendipity may traverse `:accessible` and `:frontier` rules, but not
  `:quarantined` ones."
  [world graph]
  (graph-without-rules
   graph
   (->> (:rules graph)
        (keep (fn [rule]
                (let [status (:status (rule-access-info world graph (:id rule)))]
                  (when (= :quarantined status)
                    (:id rule)))))
        vec)))

(defn- rule-access-reason
  [to-status]
  (case to-status
    :accessible :durable-episode-opened-rule
    :quarantined :outcome-driven-rule-quarantine
    :rule-access-transition))

(defn set-rule-access-status
  "Update a rule's dynamic accessibility status with structured history.

  Returns `[world transition]`, where `transition` is nil when no state change
  was needed."
  [world graph-or-rules rule-id to-status transition]
  (when-not (contains? rule-access-statuses to-status)
    (throw (ex-info "Unsupported rule access status"
                    {:rule-id rule-id
                     :to-status to-status
                     :allowed-statuses rule-access-statuses})))
  (let [registry (rule-access-registry world graph-or-rules)
        current-entry (get registry rule-id)]
    (when-not current-entry
      (throw (ex-info "Unknown rule access entry"
                      {:rule-id rule-id})))
    (let [from-status (:status current-entry)]
      (if (= from-status to-status)
        [world nil]
        (let [transition-record (cond-> {:rule-id rule-id
                                         :from-status from-status
                                         :to-status to-status
                                         :cycle (:cycle world)
                                         :reason (or (:reason transition)
                                                     (rule-access-reason to-status))}
                                  (:episode-id transition)
                                  (assoc :episode-id (:episode-id transition))

                                  (:branch-context-id transition)
                                  (assoc :branch-context-id (:branch-context-id transition)))
              next-entry (cond-> (assoc current-entry :status to-status)
                           (= :accessible to-status)
                           (assoc :opened-cycle (:cycle world)
                                  :opened-by (:episode-id transition))

                           true
                           (update :history (fnil conj []) transition-record))]
          [(assoc-in world [:rule-access rule-id] next-entry)
           transition-record])))))

(defn reconcile-episode-rule-access
  "Apply the current episode admission transition to dynamic rule accessibility.

  Durable promotion can open `:frontier` rules. Hard-failure demotion can
  quarantine non-core rules. Returns `[world transitions]`."
  [world graph-or-rules episode transition {:keys [branch-context-id]}]
  (let [rule-path (vec (:rule-path episode))
        hard-failure? (boolean
                       (seq (set/intersection #{:backfired :contradicted}
                                              (set (:anti-residue-flags episode)))))
        promoted? (and (= :durable (:to-status transition))
                       (not hard-failure?))
        demoted? (and (contains? #{:provisional :trace} (:to-status transition))
                      hard-failure?)]
    (reduce (fn [[current-world transitions] rule-id]
              (let [entry (rule-access-info current-world graph-or-rules rule-id)
                    source (:source entry)
                    target-status (cond
                                    (and promoted?
                                         (= :frontier (:status entry)))
                                    :accessible

                                    (and demoted?
                                         (not (contains? #{:authored-core :core} source))
                                         (contains? #{:accessible :frontier}
                                                    (:status entry)))
                                    :quarantined

                                    :else nil)]
                (if-not target-status
                  [current-world transitions]
                  (let [[next-world access-transition]
                        (set-rule-access-status
                         current-world
                         graph-or-rules
                         rule-id
                         target-status
                         {:episode-id (:id episode)
                          :branch-context-id branch-context-id
                          :reason (rule-access-reason target-status)})]
                    [next-world
                     (cond-> transitions
                       access-transition
                       (conj access-transition))]))))
            [world []]
            rule-path)))

(defn intervention-delta
  "Evaluation ladder Level 2: intervention sensitivity.

  Remove some rules from the graph, then compare reachable paths
  before and after. If removing a rule kills downstream paths,
  the rule is structurally load-bearing. If nothing changes,
  the rule was decorative.

  Returns :removed-paths (paths that broke) and :preserved-paths
  (paths that survived)."
  ([graph start-rule-id rule-ids]
   (intervention-delta graph start-rule-id rule-ids {:max-depth 3}))
  ([graph start-rule-id rule-ids opts]
   (let [rule-ids (vec (sort-by str (set rule-ids)))
         before-paths (reachable-paths graph start-rule-id opts)
         after-graph (graph-without-rules graph rule-ids)
         after-paths (reachable-paths after-graph start-rule-id opts)
         after-path-set (->> after-paths
                             (map :rule-path)
                             set)]
     {:removed-rule-ids rule-ids
      :before-paths before-paths
      :after-paths after-paths
      :removed-paths (->> before-paths
                          (remove #(contains? after-path-set
                                              (:rule-path %)))
                          vec)
      :preserved-paths (->> before-paths
                            (filter #(contains? after-path-set
                                                (:rule-path %)))
                            vec)})))

(defn- instantiate-executor-result
  [rule {:keys [bindings]}]
  {:consequents (mapv #(instantiate-template % bindings)
                      (:consequent-schema rule))
   :confidence (double (:plausibility rule))
   :reason (str (or (get-in rule [:denotation :intended-effect])
                    (:id rule)))
   :aux-indices (mapv #(instantiate-template % bindings)
                      (get-in rule [:index-projections :emit] []))
   :surface-summary nil})

(defn- invoke-clojure-executor
  [rule call]
  (let [executor-id (get-in rule [:executor :spec :executor-id])
        executor-fn (or (get-in rule [:executor :spec :fn])
                        (get-in rule [:executor :spec :executor-fn])
                        (get-in call [:executor-registry executor-id]))]
    (when-not (ifn? executor-fn)
      (throw (ex-info "Missing :clojure-fn executor"
                      {:rule-id (:id rule)
                       :executor-id executor-id
                       :executor (get rule :executor)})))
    (executor-fn {:rule rule
                  :call call})))

(defn- invoke-llm-executor
  [rule _call]
  (throw (ex-info "LLM-backed rule execution is not implemented yet"
                  {:rule-id (:id rule)
                   :executor (get rule :executor)})))

(defn- dispatch-executor
  [rule call]
  (case (get-in rule [:executor :kind])
    :instantiate (instantiate-executor-result rule call)
    :clojure-fn (invoke-clojure-executor rule call)
    :llm-backed (invoke-llm-executor rule call)))

(defn- validate-rule-result-shape!
  [rule call result]
  (when-not (map? result)
    (throw (ex-info "RuleResultV1 must be a map"
                    {:rule-id (:id rule)
                     :call call
                     :result result})))
  (when-not (set/subset? required-rule-result-keys
                         (set (keys result)))
    (throw (ex-info "RuleResultV1 is missing required keys"
                    {:rule-id (:id rule)
                     :call call
                     :result result
                     :required-keys required-rule-result-keys})))
  (when-not (vector? (:consequents result))
    (throw (ex-info "RuleResultV1 :consequents must be a vector"
                    {:rule-id (:id rule)
                     :call call
                     :result result})))
  (when-not (and (number? (:confidence result))
                 (<= 0.0 (double (:confidence result)) 1.0))
    (throw (ex-info "RuleResultV1 :confidence must be in [0.0, 1.0]"
                    {:rule-id (:id rule)
                     :call call
                     :result result})))
  (when-not (string? (:reason result))
    (throw (ex-info "RuleResultV1 :reason must be a string"
                    {:rule-id (:id rule)
                     :call call
                     :result result})))
  (when-not (vector? (:aux-indices result))
    (throw (ex-info "RuleResultV1 :aux-indices must be a vector"
                    {:rule-id (:id rule)
                     :call call
                     :result result})))
  (when-not (or (nil? (:surface-summary result))
                (string? (:surface-summary result)))
    (throw (ex-info "RuleResultV1 :surface-summary must be nil or a string"
                    {:rule-id (:id rule)
                     :call call
                     :result result})))
  result)

(defn- validate-effects!
  [rule call result]
  (if-not (contains? result :effects)
    result
    (let [effects (:effects result)
          declared-ops (get-in rule [:executor :spec :effect-ops])
          allowed-ops (set declared-ops)
          effect-validator (:effect-validator call)]
      (when-not (vector? effects)
        (throw (ex-info "RuleResultV1 :effects must be a vector"
                        {:rule-id (:id rule)
                         :call call
                         :result result})))
      (when-not (seq declared-ops)
        (throw (ex-info "RuleResultV1 effects require declared :effect-ops"
                        {:rule-id (:id rule)
                         :call call
                         :result result
                         :executor (get rule :executor)})))
      (doseq [effect effects]
        (when-not (map? effect)
          (throw (ex-info "RuleResultV1 effect entries must be maps"
                          {:rule-id (:id rule)
                           :call call
                           :effect effect
                           :result result})))
        (when-not (keyword? (:op effect))
          (throw (ex-info "RuleResultV1 effects must declare keyword :op"
                          {:rule-id (:id rule)
                           :call call
                           :effect effect
                           :result result})))
        (when-not (contains? allowed-ops (:op effect))
          (throw (ex-info "RuleResultV1 effect op is not declared for rule"
                          {:rule-id (:id rule)
                           :call call
                           :effect effect
                           :allowed-ops allowed-ops
                           :result result})))
        (when effect-validator
          (when-not (ifn? effect-validator)
            (throw (ex-info "RuleCallV1 :effect-validator must be callable"
                            {:rule-id (:id rule)
                             :call call
                             :effect-validator effect-validator})))
          (effect-validator {:rule rule
                             :call call
                             :result result
                             :effect effect})))
      result)))

(defn- validate-effect-program!
  [rule call result]
  (let [effect-program-validator (:effect-program-validator call)]
    (cond
      (nil? effect-program-validator) result
      (not (ifn? effect-program-validator))
      (throw (ex-info "RuleCallV1 :effect-program-validator must be callable"
                      {:rule-id (:id rule)
                       :call call
                       :effect-program-validator effect-program-validator}))
      :else
      (do
        (effect-program-validator {:rule rule
                                   :call call
                                   :result result})
        result))))

(defn- validate-effect-schema!
  [rule call {:keys [effects] :as result}]
  (if-not (contains? rule :effect-schema)
    result
    (let [effect-schema (:effect-schema rule)
          bindings (:bindings call)]
      (when-not (contains? result :effects)
        (throw (ex-info "RuleResultV1 is missing :effects for declared effect-schema"
                        {:rule-id (:id rule)
                         :call call
                         :result result
                         :effect-schema effect-schema})))
      (when (not= (count effect-schema) (count effects))
        (throw (ex-info "RuleResultV1 effect count mismatch"
                        {:rule-id (:id rule)
                         :bindings bindings
                         :expected-count (count effect-schema)
                         :actual-count (count effects)
                         :effect-schema effect-schema
                         :effects effects})))
      (when-not (reduce (fn [current-bindings [pattern effect]]
                          (when current-bindings
                            (match-fact-pattern pattern effect current-bindings)))
                        bindings
                        (map vector effect-schema effects))
        (throw (ex-info "RuleResultV1 effects do not satisfy effect-schema"
                        {:rule-id (:id rule)
                         :bindings bindings
                         :effect-schema effect-schema
                         :effects effects})))
      result)))

(defn- validate-consequents!
  [rule call {:keys [consequents] :as result}]
  (let [schema (:consequent-schema rule)
        bindings (:bindings call)]
    (when (not= (count schema) (count consequents))
      (throw (ex-info "RuleResultV1 consequent count mismatch"
                      {:rule-id (:id rule)
                       :bindings bindings
                       :expected-count (count schema)
                       :actual-count (count consequents)
                       :schema schema
                       :consequents consequents})))
    (when-not (seq (match-antecedent-schema schema consequents bindings))
      (throw (ex-info "RuleResultV1 consequents do not satisfy consequent-schema"
                      {:rule-id (:id rule)
                       :bindings bindings
                       :schema schema
                       :consequents consequents})))
    result))

(defn- validation-failures
  [rule call result]
  (let [validation-fn (get-in rule [:denotation :validation-fn])]
    (if validation-fn
      (let [failures (validation-fn {:rule rule
                                     :call call
                                     :result result})]
        (cond
          (nil? failures) []
          (vector? failures) failures
          (sequential? failures) (vec failures)
          :else
          (throw (ex-info "validation-fn must return a vector of failure keywords"
                          {:rule-id (:id rule)
                           :returned failures}))))
      [])))

(defn- validate-denotation!
  [rule call result]
  (let [failures (validation-failures rule call result)
        allowed (set (get-in rule [:denotation :failure-modes]))]
    (when-not (every? allowed failures)
      (throw (ex-info "validation-fn returned undeclared failure mode(s)"
                      {:rule-id (:id rule)
                       :returned failures
                       :allowed allowed
                       :call call
                       :result result})))
    (when (seq failures)
      (throw (ex-info "RuleResultV1 failed denotational validation"
                      {:rule-id (:id rule)
                       :failures failures
                       :call call
                       :result result})))
    result))

(defn execute-rule
  "Execute a RuleV1 through its declared executor and validate the resulting
  RuleResultV1 before callers admit any outputs."
  [rule call]
  (let [rule (validate-rule! rule)
        call (normalize-rule-call rule call)
        result (dispatch-executor rule call)]
    (cond
      (nil? result) nil
      (false? result)
      (throw (ex-info "Executor returned illegal false result"
                      {:rule-id (:id rule)
                       :call call}))
      :else
      (->> result
           (validate-rule-result-shape! rule call)
           (validate-effects! rule call)
           (validate-effect-schema! rule call)
           (validate-effect-program! rule call)
           (validate-consequents! rule call)
           (validate-denotation! rule call)))))

(defn matched-rule-applications
  "Match a rule against facts, then execute it for every successful match.
  Returns binding/match pairs together with the normalized call and result."
  ([rule facts]
   (matched-rule-applications rule facts {} {}))
  ([rule facts initial-bindings call-base]
   (->> (match-rule rule facts initial-bindings)
        (keep (fn [{:keys [bindings matched-facts]}]
                (let [call (merge call-base
                                  {:rule-id (:id rule)
                                   :bindings bindings
                                   :matched-facts matched-facts})
                      result (execute-rule rule call)]
                  (when result
                    {:bindings bindings
                     :matched-facts matched-facts
                     :call call
                     :result result}))))
        vec)))

(defn instantiate-rule
  "Compatibility wrapper for callers that still expect the instantiate-only
  execution path."
  [rule bindings]
  (validate-rule! rule)
  (when-not (= :instantiate (get-in rule [:executor :kind]))
    (throw (ex-info "instantiate-rule only supports :instantiate executors"
                    {:rule-id (:id rule)
                     :executor-kind (get-in rule [:executor :kind])})))
  (execute-rule rule {:bindings bindings}))

(defn- resolve-effect-context-id
  [world effect-state context-ref]
  (cond
    (contains? (:context-refs effect-state) context-ref)
    (get-in effect-state [:context-refs context-ref])

    (contains? (:contexts world) context-ref)
    context-ref

    :else
    (throw (ex-info "Unknown effect context ref"
                    {:context-ref context-ref
                     :known-context-refs (sort (keys (:context-refs effect-state)))
                     :known-context-ids (sort (keys (:contexts world)))}))))

(defn- handle-context-sprout
  [{:keys [world effect effect-state]}]
  (let [[world sprouted-context-id] (cx/sprout world (:source-context-id effect))]
    [world (assoc-in effect-state [:context-refs (:ref effect)] sprouted-context-id)]))

(defn- handle-fact-assert
  [{:keys [world effect effect-state]}]
  (let [context-id (resolve-effect-context-id world effect-state (:context-ref effect))]
    [(cx/assert-fact world context-id (:fact effect))
     effect-state]))

(defn- handle-facts-assert-many
  [{:keys [world effect effect-state]}]
  (let [context-id (resolve-effect-context-id world effect-state (:context-ref effect))]
    [(reduce (fn [current-world fact]
               (cx/assert-fact current-world context-id fact))
             world
             (:facts effect))
     effect-state]))

(defn- handle-context-set-ordering
  [{:keys [world effect effect-state]}]
  (let [context-id (resolve-effect-context-id world effect-state (:context-ref effect))]
    [(assoc-in world [:contexts context-id :ordering] (:ordering effect))
     effect-state]))

(defn- handle-goal-set-next-context
  [{:keys [world effect effect-state]}]
  (let [context-id (resolve-effect-context-id world effect-state (:context-ref effect))]
    [(if (contains? (:goals world) (:goal-id effect))
       (goals/set-next-context world (:goal-id effect) context-id)
       world)
     effect-state]))

(defn- builtin-effect-handlers
  []
  {:context/sprout handle-context-sprout
   :fact/assert handle-fact-assert
   :facts/assert-many handle-facts-assert-many
   :context/set-ordering handle-context-set-ordering
   :goal/set-next-context handle-goal-set-next-context})

(defn apply-effects
  "Apply a typed effect program through a caller-supplied effect handler.

  The generic runtime owns reduction and effect-state threading; callers still
  own the concrete op semantics."
  [world effects {:keys [effect-handler effect-handlers initial-effect-state]
                  :as opts}]
  (when (and (contains? opts :effect-handler)
             (not (ifn? effect-handler)))
    (throw (ex-info "apply-effects :effect-handler must be callable"
                    {:effect-handler effect-handler})))
  (when (and (contains? opts :effect-handlers)
             (not (map? effect-handlers)))
    (throw (ex-info "apply-effects :effect-handlers must be a map"
                    {:effect-handlers effect-handlers})))
  (when-not (vector? effects)
    (throw (ex-info "apply-effects requires a vector of effects"
                    {:effects effects})))
  (let [builtin-handlers (builtin-effect-handlers)
        attempted-overrides (seq (set/intersection (set (keys builtin-handlers))
                                                   (set (keys (or effect-handlers {})))))]
    (when attempted-overrides
      (throw (ex-info "Builtin effect handlers cannot be overridden"
                      {:attempted-overrides (sort attempted-overrides)
                       :builtin-ops (sort (keys builtin-handlers))})))
    (let [merged-handlers (merge builtin-handlers
                                 (or effect-handlers {}))]
      (reduce (fn [[current-world effect-state] effect]
                (let [builtin-handler (get builtin-handlers (:op effect))
                      handler (or builtin-handler
                                  (get merged-handlers (:op effect))
                                  effect-handler)]
                  (when-not (ifn? handler)
                    (throw (ex-info "No effect handler registered for op"
                                    {:effect effect
                                     :known-ops (sort (keys merged-handlers))})))
                  (let [result (handler {:world current-world
                                         :effect effect
                                         :effect-state effect-state})]
                    (when-not (and (vector? result)
                                   (= 2 (count result)))
                      (throw (ex-info "Effect handler must return [world effect-state]"
                                      {:effect effect
                                       :result result})))
                    result)))
              [world (if (contains? opts :initial-effect-state)
                       initial-effect-state
                       {})]
              effects))))
