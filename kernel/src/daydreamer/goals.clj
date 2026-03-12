(ns daydreamer.goals
  "Goal objects with status, strength, planning-type, and context pointers.

  Recovers goal representation from Mueller's dd_cntrl.cl.
  Goals carry: id, goal-type, planning-type (:real or :imaginary),
  status (:runable, :halted, :waiting), strength, main-motiv,
  activation-context, termination-context, next-context, backtrack-wall.

  Source: dd_cntrl.cl (activate-top-level-goal, terminate-top-level-goal,
          most-highly-motivated-goals, motivating-emotion?)"
  (:require [daydreamer.context :as cx]))

(defn create-goal
  "Create a goal map. This is a pure constructor for tests and fixtures."
  [goal-spec]
  (let [{:keys [id goal-type planning-type status strength main-motiv
                activation-cx termination-cx next-cx backtrack-wall
                top-level-goal]
         :or {id :g-1
              planning-type :real
              status :runable
              strength 0.0}}
        goal-spec]
    (merge goal-spec
           {:id id
            :goal-type goal-type
            :planning-type planning-type
            :status status
            :strength strength
            :main-motiv main-motiv
            :activation-cx activation-cx
            :termination-cx termination-cx
            :next-cx next-cx
            :backtrack-wall backtrack-wall
            :top-level-goal (or top-level-goal id)}
           (dissoc goal-spec
                   :id
                   :goal-type
                   :planning-type
                   :status
                   :strength
                   :main-motiv
                   :activation-cx
                   :termination-cx
                   :next-cx
                   :backtrack-wall
                   :top-level-goal))))

(defn- goal-or-throw
  [world goal-id]
  (or (get-in world [:goals goal-id])
      (throw (ex-info "Unknown goal"
                      {:goal-id goal-id}))))

(defn- next-goal-id
  [world]
  (let [next-id (inc (or (:id-counter world) 0))
        goal-id (keyword (str "g-" next-id))]
    [(assoc world :id-counter next-id) goal-id]))

(defn activate-top-level-goal
  "Insert a new top-level goal into the world. Imaginary goals sprout a fresh
  planning context; real goals begin in the current context."
  [world context-id goal-spec]
  (let [planning-type (:planning-type goal-spec :real)
        [world goal-id] (next-goal-id world)
        [world activation-cx backtrack-wall next-cx]
        (if (= planning-type :imaginary)
          (let [[world sprout-id] (cx/sprout world context-id)]
            [world sprout-id sprout-id sprout-id])
          [world context-id nil nil])
        goal (create-goal (assoc goal-spec
                                 :id goal-id
                                 :planning-type planning-type
                                 :activation-cx activation-cx
                                 :backtrack-wall backtrack-wall
                                 :next-cx next-cx
                                 :top-level-goal goal-id))]
    [(assoc-in world [:goals goal-id] goal) goal-id]))

(defn change-status
  "Set a goal's status."
  [world goal-id status]
  (assoc-in world [:goals goal-id :status] status))

(defn record-termination
  "Record the context where a goal terminated."
  [world goal-id context-id]
  (assoc-in world [:goals goal-id :termination-cx] context-id))

(defn set-next-context
  "Update the next context pointer for a goal."
  [world goal-id context-id]
  (assoc-in world [:goals goal-id :next-cx] context-id))

(defn get-next-context
  "Return the next context pointer for a goal."
  [world goal-id]
  (:next-cx (goal-or-throw world goal-id)))

(defn get-backtrack-wall
  "Return the backtrack wall for a goal."
  [world goal-id]
  (:backtrack-wall (goal-or-throw world goal-id)))

(defn motivating-emotion?
  "Return true when any active goal is motivated by the given emotion."
  [world emotion-id]
  (boolean
   (some #(= emotion-id (:main-motiv %))
         (vals (:goals world)))))

(defn most-highly-motivated-goals
  "Return the ids of the strongest runnable goals, filtered by mode. In
  `:performance` mode imaginary goals are skipped."
  [world]
  (let [eligible-goals (->> (vals (:goals world))
                            (filter (fn [{:keys [status planning-type]}]
                                      (and (= status :runable)
                                           (or (not= (:mode world) :performance)
                                               (not= planning-type :imaginary))))))
        highest-strength (reduce max 0.0 (map :strength eligible-goals))]
    (->> eligible-goals
         (filter #(= (:strength %) highest-strength))
         (map :id)
         (sort-by str)
         vec)))

(comment
  (create-goal {:goal-type :reversal
                :strength 0.8
                :main-motiv :e-1}))
