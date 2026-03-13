(ns daydreamer.goal-families
  "Per-family activation rules and plan bodies.

  Wave 2 starts with REVERSAL because it exercises the context machinery that
  the Python scheduler does not have: copying an old planning state, attaching
  it as a pseudo-sprout under a new branch, clearing emotional residue, and
  rebinding surviving plan structure to a new top-level goal.

  This namespace implements that first kernel. It does not attempt Mueller's
  full search over failure leafs yet; it provides the alternative-past branch
  primitive that later goal-family plans can call."
  (:require [clojure.set :as set]
            [daydreamer.context :as cx]
            [daydreamer.goals :as goals]))

(def ^:private supported-families
  #{:reversal :roving :rationalization :recovery :rehearsal})

(def ^:private terminal-goal-statuses
  #{:failed :succeeded :terminated})

(defn supported-family?
  "Return true when the kernel has a namespace-level implementation target for
  the given family."
  [goal-type]
  (contains? supported-families goal-type))

(def ^:private reversal-leaf-policy
  :emotion_then_depth)

(def ^:private reversal-cause-policy
  :stored_priority)

(def ^:private reverse-leaf-policy
  :intends_weak_leaf)

(def ^:private reverse-leaf-threshold
  0.5)

(declare reversal-sprout-alternative)

(defn- fact-type?
  [fact expected]
  (and (map? fact) (= (:fact/type fact) expected)))

(defn- goal-fact?
  [fact]
  (fact-type? fact :goal))

(defn- emotion-fact?
  [fact]
  (fact-type? fact :emotion))

(defn- dependency-fact?
  [fact]
  (fact-type? fact :dependency))

(defn- intends-fact?
  [fact]
  (fact-type? fact :intends))

(defn- failure-cause-fact?
  [fact]
  (fact-type? fact :failure-cause))

(defn- fact-id
  [fact]
  (cond
    (goal-fact? fact) (:goal-id fact)
    (emotion-fact? fact) (:emotion-id fact)
    :else (or (:fact/id fact)
              (:id fact)
              (:goal-id fact)
              (:emotion-id fact))))

(defn- top-level-owner
  [fact]
  (or (:top-level-goal fact)
      (when (goal-fact? fact)
        (fact-id fact))))

(defn- fact-ref-ids
  [fact]
  (->> [(:emotion-id fact)
        (:goal-id fact)
        (:from-id fact)
        (:to-id fact)
        (:from-goal-id fact)
        (:to-goal-id fact)
        (:from-emotion-id fact)
        (:to-emotion-id fact)
        (:source-id fact)
        (:target-id fact)]
       (remove nil?)
       set))

(defn- goal-status
  [fact]
  (:status fact))

(defn- local-failed-goal-facts
  [world context-id]
  (->> (get-in world [:contexts context-id :add-obs] #{})
       (filter goal-fact?)
       (filter #(= :failed (goal-status %)))
       (sort-by pr-str)
       vec))

(defn- emotion-pressure
  [world context-id]
  (->> (cx/visible-facts world context-id)
       (filter emotion-fact?)
       (map #(double (or (:strength %) 0.0)))
       (reduce max 0.0)))

(defn- leaf-context?
  [world context-id]
  (empty? (cx/children world context-id)))

(defn reversal-leaf-candidates
  "Find candidate failure leaf contexts for REVERSAL.

  Candidate discovery is intentionally conservative for this first pass:
  - the context must be a leaf in the planning tree
  - it must locally assert at least one failed goal fact

  Ranking is provisional until realism metadata exists. For now, candidates are
  ordered by strongest visible emotion, then by context depth, then by failure
  count."
  [world]
  (->> (keys (:contexts world))
       (keep (fn [context-id]
               (let [failed-goals (local-failed-goal-facts world context-id)]
                 (when (and (seq failed-goals)
                            (leaf-context? world context-id))
                   (let [primary-goal (first failed-goals)
                         depth (count (cx/ancestors world context-id))
                         pressure (emotion-pressure world context-id)
                         reasons (cond-> [:failed_leaf]
                                   (pos? pressure) (conj :negative_emotion)
                                   (pos? depth) (conj :deep_context)
                                   (> (count failed-goals) 1) (conj :multiple_failures))]
                     {:old-context-id context-id
                      :old-top-level-goal-id (top-level-owner primary-goal)
                      :failed-goal-id (fact-id primary-goal)
                      :context-depth depth
                      :emotion-pressure pressure
                      :failure-count (count failed-goals)
                      :selection-reasons reasons
                      :selection-policy reversal-leaf-policy})))))
       (sort-by (juxt (comp - :emotion-pressure)
                      (comp - :context-depth)
                      (comp - :failure-count)
                      (comp str :old-context-id)))
       vec))

(defn select-reversal-leaf
  "Choose the strongest candidate failure leaf for REVERSAL."
  [world]
  (first (reversal-leaf-candidates world)))

(defn- goal-strength
  [world goal-fact]
  (double (or (:strength goal-fact)
              (get-in world [:goals (fact-id goal-fact) :strength])
              1.0)))

(defn- goal-objective-facts
  [goal-fact]
  (vec (or (:objective-facts goal-fact)
           (when-let [objective-fact (:objective-fact goal-fact)]
             [objective-fact])
           [])))

(defn- plan-goal-facts
  [world context-id top-level-goal-id]
  (->> (cx/visible-facts world context-id)
       (filter goal-fact?)
       (filter #(= top-level-goal-id (top-level-owner %)))
       (sort-by (comp str fact-id))
       vec))

(defn- plan-intends-facts
  [world context-id top-level-goal-id]
  (->> (cx/visible-facts world context-id)
       (filter intends-fact?)
       (filter #(= top-level-goal-id (top-level-owner %)))
       (sort-by pr-str)
       vec))

(defn- reachable-goal-ids
  [children-by-goal root-goal-id]
  (loop [frontier (conj clojure.lang.PersistentQueue/EMPTY root-goal-id)
         seen #{}]
    (if-let [goal-id (peek frontier)]
      (let [frontier (pop frontier)]
      (if (contains? seen goal-id)
          (recur frontier seen)
          (recur (into frontier (get children-by-goal goal-id []))
                 (conj seen goal-id))))
      seen)))

(defn reverse-leaf-branches
  "Find weak planning leafs reachable from the selected failed top-level goal.

  This corrects the earlier shortcut of scanning the entire context tree. The
  search is scoped to the selected failed goal's `:intends` structure, and it
  returns one candidate per weak leaf, ordered by inverse strength."
  [world {:keys [old-context-id old-top-level-goal-id]}]
  (let [goal-facts (plan-goal-facts world old-context-id old-top-level-goal-id)
        goal-facts-by-id (into {} (map (juxt fact-id identity)) goal-facts)
        children-by-goal (reduce (fn [acc {:keys [from-goal-id to-goal-id]}]
                                   (update acc from-goal-id (fnil conj []) to-goal-id))
                                 {}
                                 (plan-intends-facts world
                                                     old-context-id
                                                     old-top-level-goal-id))
        reachable-ids (reachable-goal-ids children-by-goal old-top-level-goal-id)]
    (->> reachable-ids
         (keep goal-facts-by-id)
         (filter (fn [goal-fact]
                   (empty? (get children-by-goal (fact-id goal-fact) []))))
         (keep (fn [goal-fact]
                 (let [strength (goal-strength world goal-fact)]
                   (when (< strength reverse-leaf-threshold)
                     {:old-context-id (or (:activation-context goal-fact)
                                          old-context-id)
                      :old-top-level-goal-id old-top-level-goal-id
                      :leaf-goal-id (fact-id goal-fact)
                      :leaf-strength strength
                      :ordering (/ 1.0 (max strength 1.0e-9))
                      :objective-facts (goal-objective-facts goal-fact)
                      :selection-policy reverse-leaf-policy
                      :selection-reasons (cond-> [:intends_leaf
                                                  :weak_assumption]
                                           (seq (goal-objective-facts goal-fact))
                                           (conj :objective_retraction))}))))
         (sort-by (juxt (comp - :ordering)
                        (comp str :leaf-goal-id)))
         vec)))

(defn- selected-goal-ids
  [{:keys [failed-goal-id old-top-level-goal-id]}]
  (->> [failed-goal-id old-top-level-goal-id]
       (remove nil?)
       set))

(defn- failure-cause-matches-leaf?
  [fact reversal-leaf]
  (let [goal-ids (selected-goal-ids reversal-leaf)
        fact-goal-ids (->> [(:goal-id fact)
                            (:failed-goal-id fact)
                            (:top-level-goal fact)]
                           (remove nil?)
                           set)]
    (seq (set/intersection goal-ids fact-goal-ids))))

(defn reverse-undo-cause-candidates
  "Find stored causal explanations for a selected failed leaf.

  This first pass is intentionally narrow: it does not infer new causes.
  Instead it looks for explicit `:failure-cause` facts visible from the failed
  leaf and ranks them by stored priority, then by how many counterfactual facts
  they provide."
  [world {:keys [old-context-id] :as reversal-leaf}]
  (->> (cx/visible-facts world old-context-id)
       (filter failure-cause-fact?)
       (filter #(failure-cause-matches-leaf? % reversal-leaf))
       (keep (fn [fact]
               (let [counterfactual-facts (vec (:counterfactual-facts fact []))]
                 (when (seq counterfactual-facts)
                   {:cause-id (fact-id fact)
                    :goal-id (or (:failed-goal-id fact)
                                 (:goal-id fact)
                                 (:top-level-goal fact))
                    :priority (double (or (:priority fact) 0.0))
                    :counterfactual-count (count counterfactual-facts)
                    :counterfactual-facts counterfactual-facts
                    :selection-policy reversal-cause-policy
                    :selection-reasons (cond-> [:stored_failure_cause
                                                :matching_failed_goal]
                                         (> (count counterfactual-facts) 1)
                                         (conj :multi_fact_counterfactual))}))))
       (sort-by (juxt (comp - :priority)
                      (comp - :counterfactual-count)
                      (comp str :cause-id)))
       vec))

(defn reverse-undo-causes
  "Choose stored counterfactual input facts for a selected failed leaf.

  Returns nil when the kernel has no explicit causal explanation for the leaf.
  The runner decides whether that absence is acceptable for a given benchmark."
  [world reversal-leaf]
  (when-let [cause (first (reverse-undo-cause-candidates world reversal-leaf))]
    {:input-facts (:counterfactual-facts cause)
     :counterfactual-fact-ids (mapv fact-id (:counterfactual-facts cause))
     :counterfactual-cause-id (:cause-id cause)
     :counterfactual-goal-id (:goal-id cause)
     :counterfactual-policy (:selection-policy cause)
     :counterfactual-reasons (:selection-reasons cause)}))

(defn reverse-leafs
  "Sprout one alternative-past branch per weak leaf under the selected failed
  top-level goal, retracting the leaf objective in each branch to force
  replanning."
  [world {:keys [input-facts]
          :or {input-facts []}
          :as reversal-target}]
  (reduce (fn [[current-world branch-results] branch]
            (let [[next-world sprouted-context-id]
                  (reversal-sprout-alternative
                   current-world
                   {:old-context-id (:old-context-id branch)
                    :old-top-level-goal-id (:old-top-level-goal-id reversal-target)
                    :new-context-id (:new-context-id reversal-target)
                    :new-top-level-goal-id (:new-top-level-goal-id reversal-target)
                    :ordering (:ordering branch)
                    :input-facts input-facts
                    :retract-facts (:objective-facts branch)})]
              [next-world
               (conj branch-results
                     (assoc branch
                            :sprouted-context-id sprouted-context-id
                            :retracted-fact-ids (mapv fact-id (:objective-facts branch))))]))
          [world []]
          (reverse-leaf-branches world reversal-target)))

(defn- retract-facts
  [world context-id facts]
  (reduce (fn [current-world fact]
            (cx/retract-fact current-world context-id fact))
          world
          facts))

(defn- replace-goal-ref
  [fact key old-goal-id new-goal-id]
  (if (= (get fact key) old-goal-id)
    (assoc fact key new-goal-id)
    fact))

(defn- rebind-planning-fact
  [fact old-top-level-goal-id new-top-level-goal-id context-id]
  (let [fact (-> fact
                 (replace-goal-ref :goal-id old-top-level-goal-id new-top-level-goal-id)
                 (replace-goal-ref :top-level-goal old-top-level-goal-id new-top-level-goal-id)
                 (replace-goal-ref :from-goal-id old-top-level-goal-id new-top-level-goal-id)
                 (replace-goal-ref :to-goal-id old-top-level-goal-id new-top-level-goal-id))]
    (cond-> fact
      (goal-fact? fact)
      (assoc :top-level-goal new-top-level-goal-id
             :activation-context context-id)

      (and (goal-fact? fact)
           (contains? terminal-goal-statuses (:status fact)))
      (assoc :status :runable)

      (and (intends-fact? fact)
           (contains? fact :top-level-goal))
      (assoc :top-level-goal new-top-level-goal-id)

      (contains? fact :activation-context)
      (assoc :activation-context context-id))))

(defn gc-emotions
  "Remove emotion facts and their dependency links from a context."
  [world context-id]
  (let [facts (cx/visible-facts world context-id)
        emotion-ids (->> facts
                         (filter emotion-fact?)
                         (map fact-id)
                         set)
        removable-facts (filter (fn [fact]
                                  (or (emotion-fact? fact)
                                      (and (dependency-fact? fact)
                                           (seq (set/intersection
                                                 emotion-ids
                                                 (fact-ref-ids fact))))))
                                facts)]
    (retract-facts world context-id removable-facts)))

(defn gc-plans
  "Remove planning structure not owned by the specified top-level goals."
  [world context-id top-level-goal-ids]
  (let [allowed-goal-ids (set top-level-goal-ids)
        facts (cx/visible-facts world context-id)
        removed-goal-ids (->> facts
                              (filter goal-fact?)
                              (remove (fn [fact]
                                        (contains? allowed-goal-ids
                                                   (top-level-owner fact))))
                              (map fact-id)
                              set)
        removable-facts (filter (fn [fact]
                                  (or (and (goal-fact? fact)
                                           (not (contains? allowed-goal-ids
                                                           (top-level-owner fact))))
                                      (and (intends-fact? fact)
                                           (or (and (top-level-owner fact)
                                                    (not (contains? allowed-goal-ids
                                                                    (top-level-owner fact))))
                                               (seq (set/intersection
                                                     removed-goal-ids
                                                     (fact-ref-ids fact)))))))
                                facts)]
    (retract-facts world context-id removable-facts)))

(defn- rebind-planning-facts
  [world context-id old-top-level-goal-id new-top-level-goal-id]
  (let [facts (->> (cx/visible-facts world context-id)
                   (filter (fn [fact]
                             (or (goal-fact? fact)
                                 (intends-fact? fact))))
                   (sort-by pr-str))]
    (reduce (fn [current-world fact]
              (let [rebound-fact (rebind-planning-fact fact
                                                       old-top-level-goal-id
                                                       new-top-level-goal-id
                                                       context-id)]
                (if (= fact rebound-fact)
                  current-world
                  (-> current-world
                      (cx/retract-fact context-id fact)
                      (cx/assert-fact context-id rebound-fact)))))
            world
            facts)))

(defn sprout-alternative-past
  "Copy an old planning context into a fresh root, pseudo-sprout it under the
  new planning context, clear emotional residue, and rebind surviving plan
  structure to the new top-level goal."
  [world {:keys [old-context-id old-top-level-goal-id new-context-id
                 new-top-level-goal-id]}]
  (let [[world sprouted-context-id] (cx/copy-context world old-context-id nil)
        world (cx/pseudo-sprout world sprouted-context-id new-context-id)
        world (-> world
                  (assoc-in [:contexts sprouted-context-id :alternative-past?] true)
                  (assoc-in [:contexts sprouted-context-id :generation-switches]
                            {:tense :conditional-present-perfect}))
        world (gc-emotions world sprouted-context-id)
        world (gc-plans world sprouted-context-id [old-top-level-goal-id])
        world (rebind-planning-facts world
                                     sprouted-context-id
                                     old-top-level-goal-id
                                     new-top-level-goal-id)
        world (if (contains? (:goals world) new-top-level-goal-id)
                (goals/set-next-context world
                                        new-top-level-goal-id
                                        sprouted-context-id)
                world)]
    [world sprouted-context-id]))

(defn reversal-sprout-alternative
  "REVERSAL family wrapper around `sprout-alternative-past`.

  Optional keys:
  - `:ordering` -> branch ordering used by backtracking
  - `:input-facts` -> state facts asserted into the alternative past
  - `:retract-facts` -> shaky leaf assumptions removed from the branch"
  [world {:keys [old-context-id ordering input-facts]
          :or {input-facts []
               retract-facts []}
          :as opts}]
  (let [retraction-facts (vec (:retract-facts opts []))
        [world sprouted-context-id] (sprout-alternative-past world opts)
        world (cond-> world
                (some? ordering)
                (assoc-in [:contexts sprouted-context-id :ordering] ordering))
        world (reduce (fn [current-world fact]
                        (cx/assert-fact current-world sprouted-context-id fact))
                      world
                      input-facts)
        world (reduce (fn [current-world fact]
                        (cx/retract-fact current-world sprouted-context-id fact))
                      world
                      retraction-facts)
        world (update world :mutation-events
                      (fnil conj [])
                      {:family :reversal
                       :source-context old-context-id
                       :target-context sprouted-context-id
                       :input-facts (vec input-facts)
                       :retracted-facts retraction-facts})]
    [world sprouted-context-id]))
