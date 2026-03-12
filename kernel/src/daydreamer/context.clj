(ns daydreamer.context
  "Persistent context graph with sprouting, backtracking, and pseudo-sprouts.

  Recovers the context mechanism from Mueller's gate_cx.cl.
  Every context is an immutable map. Sprouting produces a new context
  that inherits its parent's facts. Pseudo-sprouts appear as children
  but do not inherit content (used by REVERSAL for alternative pasts).

  Source: gate_cx.cl (cx$sprout, cx$pseudo-sprout-of, cx$assert, cx$retract)"
  (:refer-clojure :exclude [ancestors]))

(declare visible-facts)

(defn create-context
  "Create a root context map. This is a pure constructor; callers that seed a
  world should store it under `:contexts` and set `:id-counter` accordingly."
  []
  {:id :cx-1
   :parent-id nil
   :children #{}
   :ancestors []
   :all-obs #{}
   :add-obs #{}
   :remove-obs #{}
   :pseudo-sprout? false
   :mutations-tried? false
   :timeout 10
   :ordering 0
   :touched-facts #{}})

(defn- context-or-throw
  [world cx-id]
  (or (get-in world [:contexts cx-id])
      (throw (ex-info "Unknown context"
                      {:context-id cx-id}))))

(defn- next-context-id
  [world]
  (let [next-id (inc (or (:id-counter world) 0))
        cx-id (keyword (str "cx-" next-id))]
    [(assoc world :id-counter next-id) cx-id]))

(defn- descendant-ids*
  [world cx-id]
  (let [{:keys [children]} (context-or-throw world cx-id)]
    (reduce
     (fn [acc child-id]
       (into acc (conj (descendant-ids* world child-id) child-id)))
     #{}
     children)))

(defn- rebase-subtree
  [world cx-id]
  (let [{:keys [parent-id children]} (context-or-throw world cx-id)
        parent (context-or-throw world parent-id)
        world (-> world
                  (assoc-in [:contexts cx-id :ancestors]
                            (vec (cons parent-id (:ancestors parent))))
                  (assoc-in [:contexts cx-id :pseudo-sprout?] true))]
    (reduce rebase-subtree world children)))

(defn sprout
  "Create a child context that inherits its parent's visible facts and sprout
  metadata."
  [world parent-id]
  (let [{:keys [ancestors all-obs mutations-tried? timeout pseudo-sprout?
                touched-facts]}
        (context-or-throw world parent-id)
        [world child-id] (next-context-id world)
        child {:id child-id
               :parent-id parent-id
               :children #{}
               :ancestors (vec (cons parent-id ancestors))
               :all-obs all-obs
               :add-obs #{}
               :remove-obs #{}
               :pseudo-sprout? pseudo-sprout?
               :mutations-tried? mutations-tried?
               :timeout (when (some? timeout) (dec timeout))
               :ordering 0
               :touched-facts touched-facts}
        world (-> world
                  (assoc-in [:contexts child-id] child)
                  (update-in [:contexts parent-id :children]
                             (fnil conj #{})
                             child-id))]
    [world child-id]))

(defn pseudo-sprout
  "Make an existing root context appear to be a child of another context
  without inheriting the parent's content."
  [world child-id parent-id]
  (let [{existing-parent-id :parent-id} (context-or-throw world child-id)]
    (when existing-parent-id
      (throw (ex-info "Cannot pseudo-sprout a context that already has a parent"
                      {:child-id child-id
                       :parent-id existing-parent-id})))
    (when (or (= child-id parent-id)
              (contains? (descendant-ids* world child-id) parent-id))
      (throw (ex-info "Pseudo-sprout would create a cycle"
                      {:child-id child-id
                       :parent-id parent-id})))
    (-> world
        (assoc-in [:contexts child-id :parent-id] parent-id)
        (update-in [:contexts parent-id :children] (fnil conj #{}) child-id)
        (rebase-subtree child-id))))

(defn assert-fact
  "Assert a fact into a context. Re-asserting a visible fact is a no-op."
  [world cx-id fact]
  (if (contains? (visible-facts world cx-id) fact)
    world
    (-> world
        (update-in [:contexts cx-id :add-obs] (fnil conj #{}) fact)
        (update-in [:contexts cx-id :all-obs] (fnil conj #{}) fact)
        (update-in [:contexts cx-id :touched-facts] (fnil conj #{}) fact))))

(defn retract-fact
  "Retract a fact from a context. Removing a locally added fact erases it from
  `:add-obs`; retracting an inherited fact records it in `:remove-obs`."
  [world cx-id fact]
  (let [{:keys [add-obs all-obs]} (context-or-throw world cx-id)]
    (cond
      (not (contains? all-obs fact))
      world

      (contains? add-obs fact)
      (-> world
          (update-in [:contexts cx-id :add-obs] disj fact)
          (update-in [:contexts cx-id :all-obs] disj fact)
          (update-in [:contexts cx-id :touched-facts] disj fact))

      :else
      (-> world
          (update-in [:contexts cx-id :remove-obs] (fnil conj #{}) fact)
          (update-in [:contexts cx-id :all-obs] disj fact)
          (update-in [:contexts cx-id :touched-facts] disj fact)))))

(defn visible-facts
  "Return the full visible fact set for a context."
  [world cx-id]
  (:all-obs (context-or-throw world cx-id)))

(defn fact-true?
  "Return true when a fact is visible in the given context."
  [world cx-id fact]
  (contains? (visible-facts world cx-id) fact))

(defn ancestors
  "Return ancestor ids in parent-first order."
  [world cx-id]
  (:ancestors (context-or-throw world cx-id)))

(defn children
  "Return the child context ids for a context."
  [world cx-id]
  (:children (context-or-throw world cx-id)))

(defn leaf-descendants
  "Return the ids of the leaf nodes under the given context. A leaf context is
  its own leaf descendant."
  [world cx-id]
  (let [{:keys [children]} (context-or-throw world cx-id)]
    (if (empty? children)
      #{cx-id}
      (reduce
       (fn [acc child-id]
         (into acc (leaf-descendants world child-id)))
       #{}
       children))))

(defn copy-context
  "Copy a context's visible contents into a fresh sprout of another parent."
  [world source-id target-parent-id]
  (let [[world new-cx-id] (sprout world target-parent-id)
        world (reduce
               (fn [current-world fact]
                 (assert-fact current-world new-cx-id fact))
               world
               (visible-facts world source-id))]
    [world new-cx-id]))

(comment
  (create-context))
