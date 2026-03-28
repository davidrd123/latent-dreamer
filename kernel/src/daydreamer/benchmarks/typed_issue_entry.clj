(ns daydreamer.benchmarks.typed-issue-entry
  "First bounded collaborator-side issue-entry experiment.

  This stays deliberately small:
  - bounded architecture packet from repo notes
  - proposer packet
  - kernel validation/admission/activation
  - bridge-index retrieval over issues"
  (:require [daydreamer.issue-entry :as issue-entry]
            [daydreamer.issue-proposer :as issue-proposer]))

(defn empty-world
  []
  {:issues {}
   :issue-proposal-log []
   :issue-lifecycle-log []
   :issue-uptake-log []
   :issue-retrieval-log []
   :issue-counter 0
   :issue-event-counter 0
   :cycle 0})

(defn architecture-decision-source-packet
  []
  (issue-entry/create-source-packet
   {:source-packet-id :src-architecture-a
    :surface :architecture-review
    :standing-note? true
    :spans [{:doc "right-now.md"
             :span-id "s1"
             :quote "If that read stabilizes without exposing another clear reusable Graffito seam, the next branch may be one bounded collaborator-side proposal experiment (for example typed concern/issue initiation), not a broad pivot and not another miniworld growth spurt."}
            {:doc "steering-balance-2026-03-24.md"
             :span-id "s2"
             :quote "if the current attractor / lifecycle read stabilizes and no new clearly reusable Graffito seam appears, let this branch graduate sooner into one bounded proposal experiment rather than extending the miniworld indefinitely"}
            {:doc "26-typed-concern-entry-and-epistemic-retrieval.md"
             :span-id "s3"
             :quote "the model may nominate provisional issue objects from bounded source packets; it may not activate them directly, durable-promote them, lower retrieval thresholds, or open rule access."}]}))

(defn run-mock-issue-entry
  []
  (let [source-packet (architecture-decision-source-packet)
        proposer (issue-proposer/build-issue-proposer {:mode :mock})
        proposal-packet (proposer source-packet)
        [world summary] (issue-entry/process-proposal-packet
                         (empty-world)
                         source-packet
                         proposal-packet)
        admitted-issue-id (first (:accepted-issue-ids summary))
        admitted-issue (get-in world [:issues admitted-issue-id])
        [world retrieval-hits]
        (issue-entry/retrieve-issues
         world
         {:topics ["typed proposal entry"]
          :tensions [["proposal" "authority"]]
          :stakes ["kernel integrity"]}
         {:related-object-id admitted-issue-id})]
    {:source-packet source-packet
     :proposal-packet proposal-packet
     :summary (assoc summary
                     :retrieved-issue-ids (mapv :issue-id retrieval-hits)
                     :issue-counters (issue-entry/issue-counters world))
     :retrieval-hits retrieval-hits
     :world world
     :admitted-issue admitted-issue}))
