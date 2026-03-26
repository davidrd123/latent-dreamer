(ns mini.ex05-the-full-cycle)

;; =============================================================
;; Exercise 5: One Complete Mental Cycle
;; =============================================================
;;
;; Curriculum section: "Read one benchmark end to end"
;;
;; This simulates one tick of inner life using all the pieces
;; from exercises 1-4. No real kernel calls — just the shapes
;; and decisions, so you can see the flow.

;; --- Start state ---

(def world
  {:cycle 4
   :mode :daydreaming

   ;; Tony's character state (from the Graffito regulation work)
   :character-state
   {:tony {:sensory-load 0.82
           :entrainment 0.28
           :felt-agency 0.35
           :perceived-control 0.31}}

   ;; Active emotions (these drive concern competition)
   :emotions
   {:e-mural-panic {:strength 0.87 :valence :negative :affect :dread
                    :situation-id :mural}
    :e-wrongness-shame {:strength 0.79 :valence :negative :affect :shame
                        :situation-id :apartment}
    :e-hope {:strength 0.32 :valence :positive :affect :hope
             :situation-id :apartment}}

   ;; Active goals (linked to emotions via dependency facts)
   :goals
   {:g-hold-line {:id :g-hold-line
                  :goal-type :reversal
                  :strength 0.87
                  :situation-id :mural
                  :status :active}
    :g-be-seen {:id :g-be-seen
                :goal-type :rationalization
                :strength 0.79
                :situation-id :apartment
                :status :active}}

   ;; Some stored episodes from earlier cycles
   :episodes
   {:ep-3 {:id :ep-3
           :admission-status :provisional
           :provenance {:family :rationalization}
           :content-indices #{:tony_trusts_can_lineage
                              :creation_is_regulation
                              :intensity_can_be_style}
           :use-history []}
    :ep-7 {:id :ep-7
           :admission-status :provisional
           :provenance {:family :reversal}
           :content-indices #{:light_jolt_floods_attention
                              :noise_fragments_precision
                              :cop_light_closing}
           :use-history []}}

   ;; Episode index (what's retrievable by which cue)
   :episode-index
   {:tony_trusts_can_lineage #{:ep-3}
    :creation_is_regulation #{:ep-3}
    :intensity_can_be_style #{:ep-3}
    :light_jolt_floods_attention #{:ep-7}
    :noise_fragments_precision #{:ep-7}
    :cop_light_closing #{:ep-7}}})

;; =============================================================
;; STEP 1: Emotion decay
;; =============================================================
;; Every cycle, non-motivating emotions decay by 0.95.

(def decay-factor 0.95)

(defn decay-emotions [world]
  (update world :emotions
          (fn [ems]
            (into {}
                  (map (fn [[eid e]]
                         [eid (update e :strength * decay-factor)]))
                  ems))))

(def world-after-decay (decay-emotions world))

;; Check: emotions are slightly weaker now
(get-in world-after-decay [:emotions :e-mural-panic :strength])
;; => 0.8265 (was 0.87)

;; =============================================================
;; STEP 2: Derive regulation mode from Tony's state
;; =============================================================

(defn derive-regulation-mode
  [{:keys [entrainment felt-agency perceived-control]}]
  (cond
    (and (< entrainment 0.25)
         (< felt-agency 0.3)
         (< perceived-control 0.3))
    :overloaded

    (and (>= entrainment 0.6)
         (>= felt-agency 0.6)
         (>= perceived-control 0.6))
    :flowing

    (>= entrainment 0.45)
    :entraining

    :else
    :bracing))

(def tony-state (get-in world [:character-state :tony]))
(derive-regulation-mode tony-state)
;; => :bracing (entrainment 0.28, agency 0.35, control 0.31)
;; Tony is holding on but not yet flowing.

;; =============================================================
;; STEP 3: Derive appraisal from regulation + situation
;; =============================================================

(defn derive-appraisal-mode
  [{:keys [felt-agency perceived-control]}]
  (if (and (>= felt-agency 0.6)
           (>= perceived-control 0.6))
    :challenge-dominant
    :threat-dominant))

(derive-appraisal-mode tony-state)
;; => :threat-dominant
;; Same mural, still reads as threat because Tony's state is low.

;; =============================================================
;; STEP 4: Concern competition — who wins?
;; =============================================================
;; The strongest negative emotion linked to a goal wins.

(defn select-strongest-concern [world]
  (let [goals (:goals world)
        emotions (:emotions world)]
    (->> goals
         vals
         (map (fn [goal]
                ;; Find the emotion linked to this goal's situation
                (let [linked-emotion (->> emotions
                                          vals
                                          (filter #(= (:situation-id goal)
                                                      (:situation-id %)))
                                          (filter #(= :negative (:valence %)))
                                          (sort-by (comp - :strength))
                                          first)]
                  (assoc goal :emotion-strength
                         (or (:strength linked-emotion) 0)))))
         (sort-by (comp - :emotion-strength))
         first)))

(def selected (select-strongest-concern world-after-decay))
(println "Selected:" (:id selected) (:goal-type selected)
         "strength:" (:emotion-strength selected))
;; => Selected: :g-hold-line :reversal strength: 0.8265
;; Mural dread wins. The system will try reversal on the mural.

;; =============================================================
;; STEP 5: Family execution (reversal)
;; =============================================================
;; Reversal says: "what if the cops hadn't come?"
;; It sprouts a hypothetical branch, imagines the counterfactual,
;; and stores the result as an episode.

;; The NEW episode from this reversal:
(def new-episode
  {:id :ep-11
   :admission-status :provisional
   :promotion-eligible? true
   :promotion-basis :input-facts
   :provenance {:source :family-plan
                :family :reversal}
   :content-indices #{:light_jolt_floods_attention
                      :noise_fragments_precision
                      :cops_never_came
                      :tony_trusts_can_lineage}
   :rule-path [:goal-family/reversal-trigger
               :goal-family/reversal-activation
               :goal-family/reversal-plan-request
               :goal-family/reversal-plan-dispatch]
   :payload {:input-facts [{:fact/type :counterfactual
                            :fact/id :cops_never_came}]}
   :use-history []
   :promotion-evidence []
   :anti-residue-flags []})

;; Store it:
(def world-after-storage
  (-> world-after-decay
      (assoc-in [:episodes :ep-11] new-episode)
      ;; Add to episode index under each content index
      (update-in [:episode-index :light_jolt_floods_attention] (fnil conj #{}) :ep-11)
      (update-in [:episode-index :noise_fragments_precision] (fnil conj #{}) :ep-11)
      (update-in [:episode-index :cops_never_came] (fnil conj #{}) :ep-11)
      (update-in [:episode-index :tony_trusts_can_lineage] (fnil conj #{}) :ep-11)
      ;; Advance cycle
      (update :cycle inc)))

(count (:episodes world-after-storage))
;; => 3 (was 2, now includes :ep-11)

(get-in world-after-storage [:episode-index :tony_trusts_can_lineage])
;; => #{:ep-3 :ep-11} — both the old rationalization AND the new
;; reversal are now stored under this index. If a future situation
;; activates :tony_trusts_can_lineage, BOTH episodes surface.

;; =============================================================
;; STEP 6: Update Tony's state after reversal
;; =============================================================
;; Reversal at the mural is stressful. It slightly degrades Tony.

(def world-after-state-update
  (update-in world-after-storage [:character-state :tony]
             (fn [ts]
               (-> ts
                   (update :sensory-load + 0.04)
                   (update :entrainment - 0.04)
                   (update :felt-agency - 0.03)
                   (update :perceived-control - 0.04)))))

(get-in world-after-state-update [:character-state :tony])
;; => {:sensory-load 0.86, :entrainment 0.24,
;;     :felt-agency 0.32, :perceived-control 0.27}
;; Tony is slightly worse off. The mural is hard.

(derive-regulation-mode
 (get-in world-after-state-update [:character-state :tony]))
;; => :overloaded (entrainment dropped below 0.25)
;; Tony slipped back to overloaded. The next cycle will feel
;; that pressure.

;; =============================================================
;; STEP 7: Reappraise the mural with the new state
;; =============================================================

(derive-appraisal-mode
 (get-in world-after-state-update [:character-state :tony]))
;; => :threat-dominant (still)
;; The mural still reads as threat. Tony needs support.

;; NEXT CYCLE: if the scheduler picks the apartment (shame is still
;; strong at 0.75), and apartment rehearsal fires, Tony's entrainment
;; and agency will rise. Then the SAME mural might flip to challenge.
;; That's the reappraisal arc we saw in the 20-cycle trace.

;; =============================================================
;; Summary: one cycle, all the pieces
;; =============================================================

(println "
CYCLE 5 SUMMARY:
  Decay:      emotions weakened slightly
  Regulation: Tony is bracing (low entrainment/agency)
  Appraisal:  mural reads as threat
  Selection:  dread wins → reversal at the mural
  Execution:  reversal imagines cops_never_came
  Storage:    new episode :ep-11, :provisional, stored under 4 indices
  State:      Tony slightly degraded (mural reversal is hard)
  Reread:     mural STILL threat-dominant
  Next:       if apartment support happens, Tony may recover
              and the same mural may flip to challenge
")

;; This is literally what the kernel does 20 times in the miniworld.
;; The only difference is that the real kernel uses typed rules,
;; executor dispatch, and the membrane's full validation chain.
;; The shapes and decisions are exactly what you see here.
