(ns mini.ex02-facts-and-contexts)

;; =============================================================
;; Exercise 2: Facts and Contexts
;; =============================================================
;;
;; Curriculum section: "Learn the core control substrate"
;;
;; Mueller's system thinks in FACTS asserted into CONTEXTS.
;; A context is a branch of hypothetical reality. Facts are what
;; the system believes is true in that branch.
;;
;; The kernel's context tree is how daydreaming works:
;; - The root context is "reality"
;; - Sprouting creates a hypothetical branch ("what if...")
;; - Facts in a child context include everything from the parent
;;   plus whatever the child adds
;; - Reversal creates pseudo-sprouts (alternative pasts)
;; - Backtracking abandons a branch

;; --- What a fact looks like ---
;; Facts are plain maps with a :fact/type key.
;; The type determines what kind of information it carries.

(def a-situation-fact
  {:fact/type :situation
   :fact/id :s1_seeing_through})

(def an-emotion-fact
  {:fact/type :emotion
   :emotion-id :e-dread
   :strength 0.74
   :valence :negative
   :affect :dread
   :situation-id :s1_seeing_through})

(def a-goal-fact
  {:fact/type :goal
   :goal-id :g-fail-1
   :top-level-goal :g-fail-1
   :status :failed
   :activation-context :cx-1})

(def a-dependency-fact
  {:fact/type :dependency
   :from-id :e-dread
   :to-id :g-fail-1})

;; That dependency says: :e-dread is linked to :g-fail-1.
;; When the kernel finds a failed goal with a linked negative emotion,
;; that's a trigger for rationalization or reversal.

;; --- Building a context tree ---
;; Let's build one from scratch. This is what the kernel does.

(def root-context
  {:id :cx-1
   :parent-id nil
   :children #{}
   :ancestors []
   :all-obs #{a-situation-fact an-emotion-fact a-goal-fact a-dependency-fact}
   :add-obs #{a-situation-fact an-emotion-fact a-goal-fact a-dependency-fact}
   :remove-obs #{}})

;; :all-obs = everything visible in this context
;; :add-obs = what THIS context added (vs what it inherited)
;; :remove-obs = what THIS context retracted

;; Now simulate sprouting: create a child that inherits everything
;; and adds new facts.

(def rationalization-branch
  {:id :cx-2
   :parent-id :cx-1
   :children #{}
   :ancestors [:cx-1]
   :all-obs (conj (:all-obs root-context)
                  {:fact/type :rationalization
                   :fact/id :seam_is_honesty})
   :add-obs #{{:fact/type :rationalization
               :fact/id :seam_is_honesty}}
   :remove-obs #{}})

;; The child sees everything the parent sees, plus the new fact:
(count (:all-obs root-context))
;; => 4

(count (:all-obs rationalization-branch))
;; => 5

;; The new fact is ONLY in the child's :add-obs:
(:add-obs rationalization-branch)
;; => #{{:fact/type :rationalization, :fact/id :seam_is_honesty}}

;; --- Why this matters ---
;; When the kernel rationalizes, it:
;; 1. Sprouts a new context from the current one
;; 2. Asserts the reframe facts into that branch
;; 3. Computes emotional consequences
;; 4. Stores the whole branch as an episode
;;
;; The parent context is UNCHANGED. If the rationalization turns out
;; to be bad (backfired, contradicted), the branch can be abandoned
;; without affecting the parent.
;;
;; This is Mueller's key insight: daydreaming is SAFE because it
;; happens in hypothetical branches, not in reality.

;; --- Typed facts in the Graffito miniworld ---
;; The miniworld uses richer fact types:

(def graffito-facts
  [{:fact/type :present-actor       :fact/id :tony_is_here}
   {:fact/type :present-actor       :fact/id :monk_is_here}
   {:fact/type :relationship        :fact/id :tony_trusts_monk}
   {:fact/type :role-obligation     :fact/id :monk_owes_presence}
   {:fact/type :artifact-state      :fact/id :can_is_inheritance}
   {:fact/type :exposure            :fact/id :cop_light_closing}
   {:fact/type :sensorimotor-input  :fact/id :siren_pulse_hits_body}
   {:fact/type :person-object-relation :fact/id :tony_trusts_can_lineage}
   {:fact/type :cross-layer-correspondence :fact/id :grandma_counterpart_of_motherload}
   {:fact/type :appraisal           :fact/id :same_light_reads_as_threat}
   {:fact/type :recent-event        :fact/id :cops_interrupted_mural}])

;; Each :fact/type is a category from the 9-category situation schema.
;; The :fact/id is a named keyword that becomes a retrieval index.

;; How many fact types are there?
(->> graffito-facts
     (map :fact/type)
     distinct
     sort)

;; Which facts are about exposure?
(filter #(= :exposure (:fact/type %)) graffito-facts)

;; --- Retrieval indices come from fact ids ---
;; When an episode is stored, its retrieval indices are drawn from
;; the fact ids in its context. Later, when a new situation has
;; overlapping fact ids, the episode can be retrieved.

;; This is coincidence-mark retrieval: "how many of my current
;; indices match this stored episode's indices?"

(def episode-indices #{:tony_trusts_can_lineage
                       :siren_pulse_hits_body
                       :can_is_inheritance
                       :same_light_reads_as_threat})

(def current-situation-indices #{:siren_pulse_hits_body
                                 :cop_light_closing
                                 :can_is_inheritance
                                 :noise_fragments_precision})

;; How many overlap?
(count (clojure.set/intersection episode-indices
                                 current-situation-indices))
;; => 2 (:siren_pulse_hits_body and :can_is_inheritance)

;; If the episode's retrieval threshold is 2, this episode is
;; retrievable. If the threshold is 3, it's not.
;; That's the entire retrieval mechanism. No embeddings.

;; =============================================================
;; Exercise: build a reversal branch
;; =============================================================
;; Reversal imagines "what if the cops hadn't come?"
;; Create a pseudo-sprout that retracts the cop fact and adds
;; a counterfactual.

(def reversal-branch
  {:id :cx-3
   :parent-id :cx-1
   :children #{}
   :ancestors [:cx-1]
   ;; Start with parent's facts, REMOVE the cop fact, ADD counterfactual
   :all-obs (-> (:all-obs root-context)
                (disj a-goal-fact)  ;; the failure didn't happen
                (conj {:fact/type :counterfactual
                       :fact/id :cops_never_came}))
   :add-obs #{{:fact/type :counterfactual
               :fact/id :cops_never_came}}
   :remove-obs #{a-goal-fact}})

;; The reversal branch sees an alternative past:
(contains? (:all-obs reversal-branch)
           {:fact/type :counterfactual :fact/id :cops_never_came})
;; => true

;; But the failure is gone:
(contains? (:all-obs reversal-branch) a-goal-fact)
;; => false

;; The root context is still unchanged:
(contains? (:all-obs root-context) a-goal-fact)
;; => true
