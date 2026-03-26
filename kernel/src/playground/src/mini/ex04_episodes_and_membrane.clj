(ns mini.ex04-episodes-and-membrane)

;; =============================================================
;; Exercise 4: Episodes and the Memory Membrane
;; =============================================================
;;
;; Curriculum section: "Learn the memory membrane in depth"
;; Key file: episodic_memory.clj
;;
;; This is the kernel's real differentiator. Episodes are stored
;; cognitive traces. The membrane controls what gets to influence
;; future thinking.

;; --- What an episode looks like ---

(def a-rationalization-episode
  {:id :ep-8
   :rule :goal-family/rationalization-plan-dispatch
   :goal-id :g-tony-be-seen-rightly
   :context-id :cx-3
   :realism :plausible
   :desirability :positive
   :admission-status :provisional    ;; THE KEY FIELD
   :promotion-eligible? true
   :promotion-basis :reframe-facts
   :retention-class :payload-exemplar
   :keep-decision :keep-exemplar
   :content-indices #{:tony_trusts_can_lineage
                      :can_is_inheritance
                      :creation_is_regulation
                      :intensity_can_be_style}
   :provenance-indices #{:goal-family/rationalization-plan-dispatch}
   :support-indices #{:realism/plausible
                      :keep/keep-exemplar
                      :evaluation-source/heuristic}
   :provenance {:source :family-plan
                :family :rationalization
                :selection {:rationalization_frame_id :rf_intensity_becomes_style}}
   :rule-path [:goal-family/rationalization-trigger
               :goal-family/rationalization-activation
               :goal-family/rationalization-plan-request
               :goal-family/rationalization-plan-dispatch]
   :payload {:reframe-facts [{:fact/type :rationalization
                              :fact/id :intensity_can_be_style}]}
   :use-history []
   :promotion-evidence []
   :anti-residue-flags []})

;; Key things to notice:
;;
;; 1. :admission-status is :provisional — not yet trusted
;; 2. :promotion-eligible? is true (because it has :reframe-facts)
;; 3. :content-indices are the retrieval keys — named keywords
;; 4. :provenance-indices and :support-indices DON'T drive retrieval
;; 5. :use-history is empty — nobody has reused this yet
;; 6. :promotion-evidence is empty — no cross-family successes yet
;; 7. :rule-path shows exactly which rules produced this episode

;; --- The three admission tiers ---
;;
;; :trace       — raw cognitive trace, not retrievable for planning
;; :provisional — retrievable with ranking penalty, must earn trust
;; :durable     — fully retrievable, proven useful across families

;; Why does this matter? Without tiers, the system would just keep
;; reusing its most recent thoughts. "I rationalized this once, so
;; it must be a good rationalization, so I'll use it again..."
;; That's a groove. The membrane prevents it.

;; --- Coincidence-mark retrieval ---
;; The episode-index maps each index to the set of episodes stored
;; under it.

(def episode-index
  {:tony_trusts_can_lineage #{:ep-8 :ep-12}
   :can_is_inheritance #{:ep-8 :ep-16}
   :creation_is_regulation #{:ep-8}
   :intensity_can_be_style #{:ep-8}
   :light_jolt_floods_attention #{:ep-12 :ep-20}
   :noise_fragments_precision #{:ep-12 :ep-20}
   :siren_pulse_hits_body #{:ep-20}})

;; Given active indices, count overlaps per episode:
(defn retrieve [episode-index active-indices]
  (let [;; For each active index, get the episodes stored there
        hits (mapcat #(get episode-index % #{}) active-indices)
        ;; Count how many indices each episode matched
        marks (frequencies hits)]
    (->> marks
         (sort-by (comp - val))
         (map (fn [[eid count]] {:episode-id eid :marks count})))))

;; Mural situation has these active indices:
(def mural-indices [:light_jolt_floods_attention
                    :noise_fragments_precision
                    :tony_trusts_can_lineage
                    :can_is_inheritance])

(retrieve episode-index mural-indices)
;; => ({:episode-id :ep-12, :marks 3}    <- 3 overlapping indices!
;;     {:episode-id :ep-8, :marks 2}     <- 2 overlapping
;;     {:episode-id :ep-20, :marks 2}
;;     {:episode-id :ep-16, :marks 1})   <- only 1, below threshold

;; If threshold is 2, episodes :ep-12, :ep-8, :ep-20 are retrievable.
;; :ep-16 is not (only 1 mark).

;; This is the ENTIRE retrieval mechanism. Named discrete indices,
;; overlap counting, threshold. No embeddings, no cosine similarity.

;; --- The three cue zones ---
;; Not all indices are equal:
;;
;; CONTENT INDICES — drive retrieval (counted toward threshold)
;;   :tony_trusts_can_lineage, :can_is_inheritance, etc.
;;
;; PROVENANCE INDICES — tie-break only (not counted)
;;   :goal-family/rationalization-plan-dispatch
;;   (which rule produced this)
;;
;; SUPPORT INDICES — metadata only (never retrieved)
;;   :realism/plausible, :keep/keep-exemplar
;;
;; This separation prevents metadata from driving retrieval.
;; You don't want "all plausible episodes" clumping together
;; just because they share :realism/plausible.

;; --- Promotion: how episodes earn trust ---

;; An episode promotes from :provisional to :durable when:
;; 1. It has :promotion-eligible? true (has reframe-facts or similar)
;; 2. It has >= 2 cross-family use successes in its :promotion-evidence
;; 3. It has no active anti-residue flags

;; Cross-family use means: a DIFFERENT family retrieved and used
;; this episode successfully. If a rationalization episode gets
;; used by reversal, that's cross-family use.

;; Let's simulate the promotion chain:

(def episode-with-evidence
  (-> a-rationalization-episode
      ;; First cross-family use: rehearsal used this episode
      (update :use-history conj
              {:use-id :use-1
               :source-family :rationalization
               :target-family :rehearsal
               :outcome :succeeded
               :status :resolved})
      ;; That success generates promotion evidence
      (update :promotion-evidence conj
              {:type :cross-family-use-success
               :use-id :use-1
               :source-family :rationalization
               :target-family :rehearsal})
      ;; Second cross-family use: another rehearsal cycle
      (update :use-history conj
              {:use-id :use-2
               :source-family :rationalization
               :target-family :rehearsal
               :outcome :succeeded
               :status :resolved})
      (update :promotion-evidence conj
              {:type :cross-family-use-success
               :use-id :use-2
               :source-family :rationalization
               :target-family :rehearsal})))

;; Now check: is it eligible for promotion?
(let [ep episode-with-evidence
      eligible? (and (:promotion-eligible? ep)
                     (>= (count (filter #(= :cross-family-use-success (:type %))
                                        (:promotion-evidence ep)))
                         2)  ;; threshold is 2
                     (empty? (:anti-residue-flags ep)))]
  (println "Promotion eligible?" eligible?)
  (println "Evidence count:" (count (:promotion-evidence ep)))
  (println "Flags:" (:anti-residue-flags ep)))
;; => Promotion eligible? true

;; Promote it:
(def promoted-episode
  (assoc episode-with-evidence :admission-status :durable))

(:admission-status promoted-episode)
;; => :durable — now fully trusted for future planning

;; --- Anti-residue flags ---
;; These PREVENT promotion or suppress retrieval:
;;
;; :same-family-loop — this episode keeps getting reused by its
;;   own family. Probably a groove.
;;
;; :stale — used multiple times, never succeeded. Dead weight.
;;
;; :backfired — using this episode made things worse.
;;
;; :contradicted — later evidence directly conflicts with this.

;; Example: flag an episode as same-family-loop
(def flagged-episode
  (update a-rationalization-episode :anti-residue-flags conj :same-family-loop))

;; Now it can't promote even with evidence:
(let [ep (-> flagged-episode
             (assoc :promotion-evidence
                    [{:type :cross-family-use-success}
                     {:type :cross-family-use-success}]))
      flags (set (:anti-residue-flags ep))
      blocked? (seq (clojure.set/intersection
                     #{:backfired :contradicted :stale}
                     flags))]
  (println "Has evidence:" (count (:promotion-evidence ep)))
  (println "Has blocking flags:" (boolean blocked?))
  (println "Has loop flag:" (contains? flags :same-family-loop)))
;; The loop flag doesn't directly block promotion, but it does
;; suppress same-family reentry and signal a groove.

;; =============================================================
;; Exercise: trace the full lifecycle of an episode
;; =============================================================
;; Starting from creation through storage, retrieval, use,
;; outcome, and promotion. This is the chain the kernel runs.

(defn lifecycle-trace []
  (println "1. CREATED: rationalization produces episode")
  (println "   admission: :provisional")
  (println "   indices:" (:content-indices a-rationalization-episode))
  (println)

  (println "2. STORED: under content indices in episode-index")
  (println "   retrievable by: coincidence-mark overlap >= threshold")
  (println)

  (println "3. RETRIEVED: mural situation has overlapping indices")
  (let [hits (retrieve episode-index mural-indices)]
    (println "   hits:" hits))
  (println)

  (println "4. USED: rehearsal selects this as a source")
  (println "   note-episode-use records the use")
  (println "   source-family: :rationalization, target-family: :rehearsal")
  (println "   → cross-family! this counts.")
  (println)

  (println "5. OUTCOME: the branch that used it succeeded")
  (println "   resolve-episode-use-outcome records :succeeded")
  (println "   record-promotion-evidence adds :cross-family-use-success")
  (println)

  (println "6. PROMOTION: after 2 cross-family successes")
  (println "   reconcile-episode-admission promotes to :durable")
  (println "   → this episode now fully influences future thinking")
  (println)

  (println "7. FRONTIER: promotion can open frontier rules")
  (println "   if the episode's rule-path touches a :frontier rule,")
  (println "   that rule moves to :accessible")
  (println "   → the system can now PLAN things it couldn't before"))

(lifecycle-trace)

;; That's the full chain. Every counter in the miniworld trace
;; corresponds to one step in this lifecycle.
