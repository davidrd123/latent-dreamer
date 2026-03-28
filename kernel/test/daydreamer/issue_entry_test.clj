(ns daydreamer.issue-entry-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.typed-issue-entry :as typed-issue-entry]
            [daydreamer.issue-entry :as issue-entry]
            [daydreamer.issue-proposer :as issue-proposer]))

(deftest mock-issue-entry-admits-grounded-issue-and-rejects-jargon-proposal
  (let [{:keys [summary retrieval-hits world admitted-issue]}
        (typed-issue-entry/run-mock-issue-entry)]
    (testing "one grounded issue is admitted and activated"
      (is (= 1 (count (:accepted-issue-ids summary))))
      (is (= :provisional (:issue/status admitted-issue)))
      (is (= :active (:issue/activation admitted-issue)))
      (is (= :issue/move-now-to-a-bounded-proposal-seam-or-harden-the-kernel-first
             (:issue/normalized-label admitted-issue))))
    (testing "the second proposal is rejected for insufficient grounding before it can leak authority"
      (is (= 1 (count (:rejected-proposal-ids summary))))
      (is (= [:insufficient-grounding-spans]
             (:validator/reasons (last (:issue-proposal-log world))))))
    (testing "bridge-index retrieval can resurface the admitted issue"
      (is (= [(:issue/id admitted-issue)]
             (mapv :issue-id retrieval-hits)))
      (is (= 1 (get-in summary [:issue-counters :issue-retrieval-count]))))))

(deftest duplicate-issue-packets-merge-into-existing-open-issue
  (let [source-packet (typed-issue-entry/architecture-decision-source-packet)
        proposer (issue-proposer/build-issue-proposer {:mode :mock})
        proposal-packet (proposer source-packet)
        [world first-summary] (issue-entry/process-proposal-packet
                               (typed-issue-entry/empty-world)
                               source-packet
                               proposal-packet)
        [world second-summary] (issue-entry/process-proposal-packet
                                world
                                source-packet
                                proposal-packet)
        issue-id (first (:accepted-issue-ids first-summary))
        issue (get-in world [:issues issue-id])]
    (is (= [issue-id] (:merged-issue-ids second-summary)))
    (is (= 2 (:issue/corroboration-count issue)))
    (is (= 1 (count (:issues world))))
    (is (= 1 (get-in (issue-entry/issue-counters world) [:duplicate-merge-count])))))

(deftest settled-labels-reject-already-settled-issue
  (let [source-packet (assoc (typed-issue-entry/architecture-decision-source-packet)
                             :settled-labels #{:issue/move-now-to-a-bounded-proposal-seam-or-harden-the-kernel-first})
        proposer (issue-proposer/build-issue-proposer {:mode :mock})
        proposal-packet (proposer source-packet)
        [world summary] (issue-entry/process-proposal-packet
                         (typed-issue-entry/empty-world)
                         source-packet
                         proposal-packet)]
    (is (empty? (:accepted-issue-ids summary)))
    (is (empty? (:issues world)))
    (is (= 1 (get-in (issue-entry/issue-counters world) [:settled-reject-count])))))
