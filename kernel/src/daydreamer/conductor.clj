(ns daydreamer.conductor
  "Shared autonomous session loop for benchmark adapters.

  The conductor owns the outer cycle loop and the optional thought/director
  feedback hooks. Scenario adapters provide world initialization, one-cycle
  advancement, packet builders, and any scenario-specific director feedback
  application."
  (:require [daydreamer.runtime-thought :as runtime-thought]
            [daydreamer.trace :as trace]))

(defn maybe-apply-runtime-thought
  [world adapter thought-fn]
  (if-not thought-fn
    world
    (let [packet ((:thought-packet adapter) world)
          raw-feedback (thought-fn packet)
          [world feedback-applied]
          (runtime-thought/apply-feedback world packet raw-feedback)]
      (trace/merge-latest-cycle world {:runtime-thought feedback-applied}))))

(defn maybe-apply-director
  [world adapter director-fn]
  (if-not director-fn
    world
    (let [director-input ((:director-input adapter) world)
          raw-feedback (director-fn director-input)
          [world feedback-applied]
          ((:apply-director-feedback adapter) world raw-feedback)]
      (trace/merge-latest-cycle world {:feedback-applied feedback-applied}))))

(defn run-session
  [adapter {:keys [cycles thought-fn director-fn]
            :or {cycles 12}
            :as config}]
  (let [{:keys [world]} ((:init-world adapter) config)
        summary-fn (or (:summary adapter)
                       (fn [_world cycle-result]
                         cycle-result))]
    (loop [world world
           remaining cycles
           summaries []]
      (if (zero? remaining)
        {:world world
         :log (trace/reporter-log world
                                  ((:metadata adapter) world config))
         :summaries summaries}
        (let [[world cycle-result] ((:run-cycle adapter) world)
              world (maybe-apply-runtime-thought world adapter thought-fn)
              world (maybe-apply-director world adapter director-fn)]
          (recur world
                 (dec remaining)
                 (conj summaries (summary-fn world cycle-result))))))))
