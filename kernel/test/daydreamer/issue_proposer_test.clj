(ns daydreamer.issue-proposer-test
  (:require [clojure.test :refer [deftest is]]
            [daydreamer.issue-proposer :as issue-proposer]))

(deftest normalize-issue-proposal-packet-coerces-json-shape
  (let [normalized (issue-proposer/normalize-issue-proposal-packet
                    {:packet_id "pkt-a"
                     :source_packet_id "src-a"
                     :proposals [{:proposal_id "prop-1"
                                  :proposal_type "issue"
                                  :issue_kind "decision"
                                  :issue_title "Choose the next branch"
                                  :source_phrasing ["next branch" "proposal experiment"]
                                  :why_unresolved "The packet still has a live choice."
                                  :why_now "The current loop is stable."
                                  :source_spans [{:doc "right-now.md"
                                                  :span_id "s1"
                                                  :quote "next branch may be one bounded collaborator-side proposal experiment"
                                                  :span_role "support"}
                                                 {:doc "reply-26.md"
                                                  :span_id "s2"
                                                  :quote "the model may nominate provisional issue objects"
                                                  :span_role "counterpressure"}]
                                  :anchor_phrases {:topics ["typed proposal entry"]
                                                   :tensions [["proposal" "authority"]]
                                                   :stakes ["kernel integrity"]
                                                   :actors ["kernel" "llm"]
                                                   :projects ["collaborative pivot"]
                                                   :tasks ["choose next branch"]
                                                   :terms ["proposer committer"]}
                                  :timing_hint "now"
                                  :opposing_span_ids ["s2"]}]})]
    (is (= :pkt-a (:packet/id normalized)))
    (is (= :src-a (:source/packet-id normalized)))
    (is (= :decision (get-in normalized [:proposals 0 :issue/kind])))
    (is (= :now (get-in normalized [:proposals 0 :timing/hint])))
    (is (= :support (get-in normalized [:proposals 0 :source/spans 0 :span/role])))))
