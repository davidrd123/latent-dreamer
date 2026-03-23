(ns daydreamer.benchmarks.membrane-fixture
  "Minimal membrane-integration benchmark.

  This is intentionally narrower than Puppet Knows. It does not try to be a
  psychologically rich autonomous world; it tries to force the first live
  dynamic stored-source races through the real kernel paths so the membrane can
  be observed in runtime rather than only in isolated unit tests."
  (:require [daydreamer.context :as cx]
            [daydreamer.episodic-memory :as episodic]
            [daydreamer.family-evaluator :as family-evaluator]
            [daydreamer.goal-families :as families]
            [daydreamer.goals :as goals]
            [daydreamer.runner :as runner]
            [daydreamer.trace :as trace]))

(def benchmark-brief-path
  "Source spec for the membrane fixture."
  "daydreaming/Notes/membrane-fixture-spec.md")

(def ^:private shared-failed-goal-id :g_performance_failure)
(def ^:private shared-cue-fact
  {:fact/type :situation
   :fact/id :shared_stage_light})
(def ^:private shared-leaf-objective-fact
  {:fact/type :assumption
   :fact/id :performance_stays_hidden})
(def ^:private authored-rationalization-frame-id :rf_doubt_is_preparation)
(def ^:private authored-reversal-cause-id :fc_absence_filled)

(def ^:private situations
  {:a {:key :a
       :id :situation_a
       :label "rehearsal room"
       :emotion-id :e_rehearsal_dread
       :emotion-strength 0.78
       :leaf-goal-id :g_performance_failure_leaf_a
       :shared-facts [{:fact/type :situation :fact/id :mirror}
                      {:fact/type :situation :fact/id :voice}
                      {:fact/type :situation :fact/id :preparation}
                      {:fact/type :counterfactual :fact/id :absence_is_filled}
                      shared-cue-fact
                      shared-leaf-objective-fact]}
   :b {:key :b
       :id :situation_b
       :label "empty house"
       :emotion-id :e_empty_house_dread
       :emotion-strength 0.74
       :leaf-goal-id :g_performance_failure_leaf_b
       :shared-facts [{:fact/type :situation :fact/id :silence}
                      {:fact/type :situation :fact/id :dust}
                      {:fact/type :situation :fact/id :memory}
                      {:fact/type :rationalization
                       :fact/id :doubt_is_preparation}
                      shared-cue-fact
                      shared-leaf-objective-fact]}})

(def ^:private authored-rationalization-reframe-facts
  [shared-cue-fact
   {:fact/type :rationalization
    :fact/id :doubt_is_preparation}])

(def ^:private authored-reversal-counterfactual-facts
  [{:fact/type :counterfactual
    :fact/id :absence_is_filled}])

(def ^:private roving-seeds
  [{:rule :membrane-fixture-roving-a
    :indices [:mirror :preparation]
    :selection-key :a}
   {:rule :membrane-fixture-roving-b
    :indices [:silence :memory]
    :selection-key :b}])

(def ^:private cycle-pattern
  [{:family :rationalization
    :situation-key :a}
   {:family :reversal
    :situation-key :b}
   {:family :rationalization
    :situation-key :b}
   {:family :reversal
    :situation-key :a}])

(defn- assert-facts
  [world context-id facts]
  (reduce (fn [current-world fact]
            (cx/assert-fact current-world context-id fact))
          world
          facts))

(defn- seed-roving-episodes
  [world]
  (reduce (fn [current-world {:keys [rule indices]}]
            (let [[current-world episode-id]
                  (episodic/add-episode current-world {:rule rule})
                  current-world
                  (reduce (fn [indexed-world index]
                            (episodic/store-episode indexed-world
                                                    episode-id
                                                    index
                                                    {:reminding? true}))
                          current-world
                          indices)]
              (update current-world :roving-episodes (fnil conj []) episode-id)))
          world
          roving-seeds))

(defn- seed-situation-context
  [world root-id {:keys [id emotion-id emotion-strength leaf-goal-id shared-facts]}]
  (let [[world context-id] (cx/sprout world root-id)
        facts (concat shared-facts
                      [{:fact/type :situation
                        :fact/id id}
                       {:fact/type :goal
                        :goal-id shared-failed-goal-id
                        :top-level-goal shared-failed-goal-id
                        :status :failed
                        :activation-context context-id}
                       {:fact/type :goal
                        :goal-id leaf-goal-id
                        :top-level-goal shared-failed-goal-id
                        :status :runable
                        :strength 0.32
                        :activation-context context-id
                        :objective-fact shared-leaf-objective-fact}
                       {:fact/type :intends
                        :from-goal-id shared-failed-goal-id
                        :to-goal-id leaf-goal-id
                        :top-level-goal shared-failed-goal-id}
                       {:fact/type :emotion
                        :emotion-id emotion-id
                        :strength emotion-strength
                        :valence :negative
                        :affect :dread}
                       {:fact/type :dependency
                        :from-id emotion-id
                        :to-id shared-failed-goal-id}])]
    [(assert-facts world context-id facts) context-id]))

(defn build-world
  "Create the narrow two-situation membrane fixture world."
  []
  (let [world (seed-roving-episodes (runner/initial-world))
        root-id (:reality-context world)
        [world a-context-id] (seed-situation-context world root-id (:a situations))
        [world b-context-id] (seed-situation-context world root-id (:b situations))
        world (-> world
                  (cx/assert-fact a-context-id
                                  {:fact/type :rationalization-frame
                                   :fact/id authored-rationalization-frame-id
                                   :goal-id shared-failed-goal-id
                                   :priority 0.82
                                   :reframe-facts authored-rationalization-reframe-facts})
                  (cx/assert-fact b-context-id
                                  {:fact/type :failure-cause
                                   :fact/id authored-reversal-cause-id
                                   :goal-id shared-failed-goal-id
                                   :priority 0.86
                                   :counterfactual-facts authored-reversal-counterfactual-facts}))
        fixture {:root-id root-id
                 :situation-contexts {:a a-context-id
                                      :b b-context-id}
                 :authored {:rationalization-frame-id authored-rationalization-frame-id
                            :reversal-cause-id authored-reversal-cause-id}}]
    (assoc world :membrane-fixture fixture)))

(defn- activate-family-goal
  [world {:keys [family situation-key]}]
  (let [{:keys [id emotion-id emotion-strength]}
        (get situations situation-key)
        context-id (get-in world [:membrane-fixture :situation-contexts situation-key])]
    (goals/activate-top-level-goal
     world
     context-id
     {:goal-type family
      :planning-type :imaginary
      :strength emotion-strength
      :main-motiv emotion-id
      :situation-id id
      :trigger-context-id context-id
      :trigger-failed-goal-id shared-failed-goal-id
      :trigger-emotion-id emotion-id
      :trigger-emotion-strength emotion-strength})))

(defn- plan-opts
  [world goal-id {:keys [family situation-key]}]
  (let [context-id (get-in world [:membrane-fixture :situation-contexts situation-key])
        next-context-id (get-in world [:goals goal-id :next-cx])
        {:keys [emotion-id emotion-strength]} (get situations situation-key)]
    (case family
      :rationalization
      {:goal-id goal-id
       :context-id next-context-id
       :trigger-context-id context-id
       :failed-goal-id shared-failed-goal-id
       :family-evaluator family-evaluator/mock-family-evaluator}

      :reversal
      (let [candidates (families/reverse-undo-cause-candidates
                        world
                        {:old-context-id context-id
                         :old-top-level-goal-id shared-failed-goal-id
                         :failed-goal-id shared-failed-goal-id})
            selected-cause (first candidates)]
        {:goal-id goal-id
         :old-context-id context-id
         :old-top-level-goal-id shared-failed-goal-id
         :failed-goal-id shared-failed-goal-id
         :trigger-emotion-id emotion-id
         :trigger-emotion-strength emotion-strength
         :new-context-id next-context-id
         :new-top-level-goal-id goal-id
         :input-facts (:counterfactual-facts selected-cause)
         :counterfactual-fact-ids (mapv :fact/id (:counterfactual-facts selected-cause))
         :counterfactual-cause-id (:cause-id selected-cause)
         :counterfactual-goal-id (:goal-id selected-cause)
         :counterfactual-policy (:selection-policy selected-cause)
         :counterfactual-reasons (:selection-reasons selected-cause)
         :counterfactual-winner-origin (:candidate-origin selected-cause)
         :counterfactual-candidates
         (mapv (fn [candidate]
                 (cond-> {:origin (:candidate-origin candidate)
                          :rank (:candidate-rank candidate)
                          :source-id (:cause-id candidate)
                          :goal-id (:goal-id candidate)
                          :selection-policy (:selection-policy candidate)
                          :priority (:priority candidate)
                          :counterfactual-count (:counterfactual-count candidate)
                          :selection-reasons (:selection-reasons candidate)}
                   (:admission-status candidate)
                   (assoc :admission-status (:admission-status candidate))))
               candidates)
         :family-evaluator family-evaluator/mock-family-evaluator}))))

(defn- trace-selected-goal
  [world goal-id]
  (trace/goal-summary world goal-id))

(defn- winner-origin
  [family-plan]
  (case (:family family-plan)
    :rationalization (get-in family-plan [:selection :rationalization_frame_winner_origin])
    :reversal (get-in family-plan [:selection :reversal_counterfactual_winner_origin])
    nil))

(defn- source-episode-id
  [family-plan]
  (case (:family family-plan)
    :rationalization (get-in family-plan [:selection :rationalization_source_episode])
    :reversal (get-in family-plan [:selection :reversal_counterfactual_source])
    nil))

(defn- candidate-race
  [family-plan]
  (case (:family family-plan)
    :rationalization (get-in family-plan [:selection :rationalization_frame_candidates])
    :reversal (get-in family-plan [:selection :reversal_counterfactual_candidates])
    []))

(defn- candidate-kind
  [family-plan]
  (case (:family family-plan)
    :rationalization :frame
    :reversal :counterfactual
    :candidate))

(defn- summarize-cycle
  [step family-plan]
  (let [winner (winner-origin family-plan)
        source-episode (source-episode-id family-plan)
        candidate-race (candidate-race family-plan)
        use-records (get-in family-plan [:result :episode-use-records] [])
        situation-id (get-in situations [(:situation-key step) :id])]
    {:cycle (:cycle step)
     :family (:family step)
     :situation-id situation-id
     :goal-id (:goal-id step)
     :family-plan-episode-id (:family-episode-id family-plan)
     :winner-origin winner
     :source-episode-id source-episode
     :candidate-kind (candidate-kind family-plan)
     :candidate-race candidate-race
     :use-records use-records
     :admission-status (:admission-status family-plan)}))

(defn summary-line
  "Compact terminal summary for one membrane fixture cycle."
  [cycle-summary]
  (str "Cycle " (:cycle cycle-summary)
       " | " (name (:family cycle-summary))
       " @ " (name (:situation-id cycle-summary))
       " | winner: " (or (some-> (:winner-origin cycle-summary) name) "none")
       " | source: " (or (some-> (:source-episode-id cycle-summary) name) "authored")
       " | uses: " (count (:use-records cycle-summary))))

(defn run-cycle
  "Run one deterministic membrane-fixture cycle."
  [world step]
  (let [[world goal-id] (activate-family-goal world step)
        situation-id (get-in situations [(:situation-key step) :id])
        world (-> world
                  (assoc :mutation-events []
                         :retrieval-events []
                         :backtrack-events []
                         :termination-events [])
                  (update :cycle inc)
                  (trace/append-cycle {:selected-goal (trace-selected-goal world goal-id)
                                       :fixture-step {:family (:family step)
                                                      :situation-id situation-id}}))
        [world family-plan] (families/run-family-plan world
                                                      (plan-opts world goal-id step))]
    (if-not family-plan
      [(trace/merge-latest-cycle world {:selection {:family (:family step)
                                                    :situation-id situation-id
                                                    :status :no-plan}})
       {:cycle (:cycle world)
        :family (:family step)
        :situation-id situation-id
        :goal-id goal-id
        :family-plan-episode-id nil
        :winner-origin nil
        :source-episode-id nil
        :candidate-kind nil
        :candidate-race []
        :use-records []
        :admission-status nil}]
      (let [cycle-summary (summarize-cycle (assoc step :cycle (:cycle world)
                                                  :goal-id goal-id)
                                           family-plan)
            world (trace/merge-latest-cycle
                   world
                   {:selection (:selection family-plan)
                    :sprouted (:sprouted-context-ids family-plan)
                    :mutations (:mutation-events world)
                    :rule-provenance (:rule-provenance family-plan)
                    :family-plan-episode-id (:family-plan-episode-id cycle-summary)
                    :membrane-selection {:winner-origin (:winner-origin cycle-summary)
                                         :source-episode-id (:source-episode-id cycle-summary)
                                         :candidate-kind (:candidate-kind cycle-summary)
                                         :candidate-race (:candidate-race cycle-summary)}
                    :episode-use-records (:use-records cycle-summary)})]
        [world cycle-summary]))))

(defn benchmark-metadata
  ([] (benchmark-metadata {}))
  ([overrides]
   (merge {:started_at "2026-03-22T00:00:00Z"
           :engine_path "kernel/src/daydreamer"
           :seed "membrane-fixture"
           :benchmark "membrane_fixture"
           :world_path benchmark-brief-path
           :graph_path "benchmark: code-defined"
           :feedback_path nil
           :palette_path "membrane_fixture"
           :graph_nodes 8
           :graph_edges 8}
          overrides)))

(defn run-soak
  "Run a deterministic membrane-fixture soak.

  The cycle pattern is intentionally small:
  1. authored rationalization in situation A
  2. authored reversal in situation B
  3. dynamic rationalization trial in situation B
  4. dynamic reversal trial in situation A

  Longer runs simply repeat this pattern."
  ([cycles]
   (run-soak cycles {}))
  ([cycles {:keys [world]
            :or {world (build-world)}}]
   (let [steps (->> cycle-pattern
                    cycle
                    (take cycles)
                    vec)
         [world cycle-summaries]
         (reduce (fn [[current-world summaries] step]
                   (let [[next-world summary] (run-cycle current-world step)]
                     [next-world (conj summaries summary)]))
                 [world []]
                 steps)]
     {:world world
      :cycle-summaries cycle-summaries
      :summaries (mapv summary-line cycle-summaries)
      :log (trace/reporter-log world (benchmark-metadata {:benchmark
                                                          (str "membrane_fixture_"
                                                               cycles)}))})))

(comment
  (run-soak 4))
