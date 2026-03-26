(ns mini.ex03-emotion-decay-and-competition)

;; =============================================================
;; Exercise 3: Emotion Decay and Concern Competition
;; =============================================================
;;
;; Curriculum section: "Learn the core control substrate"
;; Key file: control.clj
;;
;; Mueller's central insight: you daydream about what BOTHERS you
;; most. Emotions have strength that decays every cycle. The
;; strongest emotion wins. This is the scheduler.

;; --- Emotion decay ---
;; Every cycle, non-motivating emotions get multiplied by 0.95.
;; When they drop below 0.15, they're garbage collected.

(def decay-factor 0.95)
(def gc-threshold 0.15)

;; Start with an emotion at strength 0.8
(def initial-strength 0.8)

;; After N cycles of decay, what's the strength?
(defn strength-after [initial n]
  (* initial (Math/pow decay-factor n)))

;; Try it:
(strength-after 0.8 1)   ;; => 0.76
(strength-after 0.8 5)   ;; => 0.619...
(strength-after 0.8 10)  ;; => 0.479...
(strength-after 0.8 20)  ;; => 0.287...
(strength-after 0.8 30)  ;; => 0.173...
(strength-after 0.8 35)  ;; => 0.136... below threshold! GC'd!

;; How many cycles until an emotion at 0.8 gets garbage collected?
(->> (range 100)
     (map (fn [n] [n (strength-after 0.8 n)]))
     (filter (fn [[_ s]] (< s gc-threshold)))
     first)
;; => [33 0.14...] — after 33 cycles, it's gone.

;; An emotion at 0.3 dies much faster:
(->> (range 100)
     (map (fn [n] [n (strength-after 0.3 n)]))
     (filter (fn [[_ s]] (< s gc-threshold)))
     first)
;; => [13 0.14...] — only 13 cycles.

;; --- What this means for the system ---
;; Strong negative emotions PERSIST. They keep coming back as the
;; strongest concern cycle after cycle. Weak emotions fade quickly.
;;
;; If nothing refreshes the emotion (no new event, no retrieval
;; that re-triggers it), it eventually dies. The system moves on.
;; But if the concern keeps getting reinforced (new failures, new
;; reminders), the emotion stays strong and the system keeps
;; returning to it.
;;
;; That's why rumination works: the concern generates episodes that
;; get retrieved later, which re-triggers the emotion, which keeps
;; the concern alive. The membrane's job is to prevent FAKE
;; reinforcement (self-generated grooves).

;; --- Concern competition ---
;; Multiple emotions compete. The strongest wins each cycle.
;; Let's simulate.

(def emotions
  {:e-shame   {:strength 0.79 :valence :negative :affect :shame}
   :e-dread   {:strength 0.87 :valence :negative :affect :dread}
   :e-hope    {:strength 0.42 :valence :positive :affect :hope}})

;; Who wins?
(->> emotions
     (sort-by (comp - :strength val))
     first)
;; => [:e-dread {:strength 0.87, ...}]
;; Dread wins. The system will daydream about whatever dread is
;; linked to.

;; After 5 cycles of decay (assuming nothing refreshes them):
(def emotions-after-5
  (into {}
        (map (fn [[eid e]]
               [eid (update e :strength * (Math/pow decay-factor 5))]))
        emotions))

emotions-after-5
;; e-shame: 0.607, e-dread: 0.669, e-hope: 0.323
;; Dread still wins, but the gap has narrowed.

;; After 15 cycles:
(def emotions-after-15
  (into {}
        (map (fn [[eid e]]
               [eid (update e :strength * (Math/pow decay-factor 15))]))
        emotions))

emotions-after-15
;; e-shame: 0.367, e-dread: 0.404, e-hope: 0.195
;; Still dread. But hope is getting close to the GC threshold.

;; --- Motivating emotions don't decay ---
;; In the real kernel, emotions that are actively motivating a goal
;; are protected from decay. This is Mueller's mechanism for
;; "this concern is still being worked on, don't let it fade."
;; The kernel checks goals/motivating-emotion? before applying decay.

;; --- How families get selected ---
;; Each family has different triggers based on emotional state:
;;
;; RATIONALIZATION: negative emotion linked to a failed goal
;;                  → "reframe this failure"
;;
;; REVERSAL:        negative emotion linked to a failed goal
;;                  → "what if it had gone differently?"
;;
;; REHEARSAL:       regulation need + affordance available
;;                  → "practice handling this"
;;
;; ROVING:          strong negative emotion + drift pressure
;;                  → "escape to something pleasant"
;;
;; REPERCUSSIONS:   (not yet implemented)
;;                  → "what would happen if...?"

;; The family that fires depends on which trigger conditions are met
;; AND which family's strength is highest in the current state.

;; In the Graffito miniworld, the strengths come from a formula:
;; family-strength = emotion-pressure + appraisal-bias + bridge-bonus
;;                   - fatigue-penalty

;; The appraisal-bias is where Tony's regulation state enters:
;; when Tony is overloaded, reversal gets a boost.
;; When Tony is entraining/flowing, rationalization/rehearsal get boosts.

;; =============================================================
;; Exercise: simulate 20 cycles of three competing emotions
;; =============================================================

(defn simulate-competition
  "Run n cycles of decay. Each cycle, print which emotion is strongest."
  [emotions n]
  (loop [step 0
         ems emotions]
    (when (< step n)
      (let [;; Find strongest
            [winner-id winner] (->> ems
                                    (sort-by (comp - :strength val))
                                    first)
            ;; Decay all
            decayed (into {}
                          (comp
                           (map (fn [[eid e]]
                                  [eid (update e :strength * decay-factor)]))
                           (remove (fn [[_ e]]
                                     (< (:strength e) gc-threshold))))
                          ems)]
        (println (format "Cycle %2d | winner: %-10s (%.3f) | alive: %d"
                         step
                         (name winner-id)
                         (:strength winner)
                         (count decayed)))
        (recur (inc step) decayed)))))

(simulate-competition emotions 40)
;; Watch: dread wins for many cycles, then emotions start dying,
;; then eventually nobody is left. That's what happens without
;; reinforcement.

;; Now try with a refresh at cycle 15 (something re-triggers shame):
(defn simulate-with-refresh
  [emotions n refresh-cycle refresh-id refresh-strength]
  (loop [step 0
         ems emotions]
    (when (< step n)
      (let [ems (if (= step refresh-cycle)
                  (do (println (format "  >>> %s refreshed to %.2f"
                                       (name refresh-id) refresh-strength))
                      (assoc-in ems [refresh-id :strength] refresh-strength))
                  ems)
            [winner-id winner] (->> ems
                                    (sort-by (comp - :strength val))
                                    first)
            decayed (into {}
                          (comp
                           (map (fn [[eid e]]
                                  [eid (update e :strength * decay-factor)]))
                           (remove (fn [[_ e]]
                                     (< (:strength e) gc-threshold))))
                          ems)]
        (println (format "Cycle %2d | winner: %-10s (%.3f) | alive: %d"
                         step
                         (name winner-id)
                         (:strength winner)
                         (count decayed)))
        (recur (inc step) decayed)))))

(simulate-with-refresh emotions 40 15 :e-shame 0.9)
;; Watch: at cycle 15, shame gets refreshed and takes over from
;; the fading dread. That's what happens when retrieval re-triggers
;; a concern — it gets a fresh burst of emotional strength and
;; recaptures the scheduler.
