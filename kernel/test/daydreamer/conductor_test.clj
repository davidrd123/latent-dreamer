(ns daydreamer.conductor-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.conductor :as conductor]
            [daydreamer.runtime-thought :as runtime-thought]
            [daydreamer.trace :as trace]))

(defn- test-adapter
  []
  {:init-world (fn [_config]
                 {:world {:cycle 0
                          :events []
                          :merged []}})
   :run-cycle (fn [world]
                (let [next-cycle (inc (:cycle world))]
                  [(-> world
                       (assoc :cycle next-cycle)
                       (update :events conj [:cycle next-cycle]))
                   {:cycle next-cycle}]))
   :thought-packet (fn [world]
                     {:cycle (:cycle world)})
   :director-input (fn [world]
                     {:cycle (:cycle world)})
   :apply-director-feedback (fn [world raw-feedback]
                              [(update world :events conj [:director raw-feedback])
                               {:director raw-feedback}])
   :metadata (fn [_world _config]
               {:label "test"})})

(deftest run-session-skips-optional-hooks-when-functions-are-nil
  (with-redefs [trace/reporter-log (fn [world metadata]
                                     {:events (:events world)
                                      :merged (:merged world)
                                      :metadata metadata})]
    (let [{:keys [world log summaries]}
          (conductor/run-session (test-adapter)
                                 {:cycles 2
                                  :thought-fn nil
                                  :director-fn nil})]
      (testing "the conductor still advances cycles and records summaries"
        (is (= [[:cycle 1] [:cycle 2]]
               (:events world)))
        (is (= [{:cycle 1} {:cycle 2}]
               summaries)))
      (testing "no optional hook payloads are merged into the trace"
        (is (= []
               (:merged world)))
        (is (= {:label "test"}
               (:metadata log)))))))

(deftest run-session-applies-runtime-thought-before-director
  (with-redefs [runtime-thought/apply-feedback
                (fn [world _packet raw-feedback]
                  [(update world :events conj [:thought raw-feedback])
                   {:thought raw-feedback}])
                trace/merge-latest-cycle
                (fn [world additions]
                  (update world :merged conj additions))
                trace/reporter-log
                (fn [world metadata]
                  {:events (:events world)
                   :merged (:merged world)
                   :metadata metadata})]
    (let [{:keys [world log summaries]}
          (conductor/run-session (test-adapter)
                                 {:cycles 1
                                  :thought-fn (fn [_packet]
                                                :raw-thought)
                                  :director-fn (fn [_director-input]
                                                 :raw-director)})]
      (testing "hook order is cycle, then thought, then director"
        (is (= [[:cycle 1]
                [:thought :raw-thought]
                [:director :raw-director]]
               (:events world)))
        (is (= [{:cycle 1}]
               summaries)))
      (testing "the merged trace payloads keep the same ordering"
        (is (= [{:runtime-thought {:thought :raw-thought}}
                {:feedback-applied {:director :raw-director}}]
               (:merged world)))
        (is (= {:label "test"}
               (:metadata log)))))))
