(ns daydreamer.issue-proposer
  "Impure proposer edge for bounded issue-initiation packets.

  The model may nominate provisional issue objects from a bounded source packet.
  The kernel remains the committer."
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [daydreamer.issue-entry :as issue-entry])
  (:import (java.net URI)
           (java.net.http HttpClient HttpRequest HttpRequest$BodyPublishers
                          HttpResponse$BodyHandlers)))

(def ^:private default-model
  "claude-haiku-4-5-20251001")

(def ^:private default-temperature
  0.1)

(def ^:private default-max-output-tokens
  900)

(def ^:private system-prompt
  (str
   "You read a bounded architecture or research packet and nominate at most three"
   " grounded unresolved issues.\n\n"
   "Rules:\n"
   "- zero proposals is allowed\n"
   "- every proposal needs at least two exact source spans\n"
   "- include one counterpressure span when available\n"
   "- do not use project jargon in the issue title unless it appears in the source\n"
   "- do not propose authority changes; only name unresolved objects\n\n"
   "Return JSON only with exactly these fields:\n"
   "- packet_id: string\n"
   "- source_packet_id: string\n"
   "- proposals: array of 0-3 proposal objects\n"
   "- abstain_reason: string or null\n\n"
   "Each proposal object must contain:\n"
   "- proposal_id\n"
   "- proposal_type (always \"issue\")\n"
   "- issue_kind (question|decision|contradiction|hypothesis|commitment|aesthetic-bet)\n"
   "- issue_title\n"
   "- source_phrasing: array of 1-4 exact source phrases\n"
   "- why_unresolved\n"
   "- why_now\n"
   "- source_spans: array of spans with doc, span_id, quote, span_role\n"
   "- anchor_phrases: object with topics, tensions, stakes, actors, projects, tasks, terms\n"
   "- timing_hint (now|soon|later|null)\n"
   "- opposing_span_ids: array"))

(def ^:private output-example
  {:packet_id "pkt-architecture-a"
   :source_packet_id "src-architecture-a"
   :proposals
   [{:proposal_id "prop-001"
     :proposal_type "issue"
     :issue_kind "decision"
     :issue_title "Move now to a bounded proposal seam or harden the kernel first"
     :source_phrasing ["bounded collaborator-side proposal experiment"
                       "one more kernel-side pass"]
     :why_unresolved "The packet contains both move-now and harden-first pressure."
     :why_now "The current attractor has stabilized and the next branch is live."
     :source_spans [{:doc "right-now.md"
                     :span_id "s1"
                     :quote "the next branch may be one bounded collaborator-side proposal experiment"
                     :span_role "support"}
                    {:doc "reply-26.md"
                     :span_id "s2"
                     :quote "the model may nominate provisional issue objects ... it may not activate them directly"
                     :span_role "counterpressure"}]
     :anchor_phrases {:topics ["typed proposal entry" "use outcome"]
                      :tensions [["move now" "harden first"]
                                 ["proposal" "authority"]]
                      :stakes ["kernel integrity" "next branch"]
                      :actors ["kernel" "llm"]
                      :projects ["collaborative pivot"]
                      :tasks ["choose next branch"]
                      :terms ["proposer committer"]}
     :timing_hint "now"
     :opposing_span_ids ["s2"]}]
   :abstain_reason nil})

(defn- normalize-text
  [value]
  (some-> value str str/trim))

(defn- json-string
  [value]
  (json/write-str value))

(defn- parse-json-text
  [text]
  (let [cleaned (-> text
                    str
                    str/trim
                    (str/replace #"(?s)^```json\s*" "")
                    (str/replace #"(?s)^```\s*" "")
                    (str/replace #"(?s)\s*```$" "")
                    str/trim)
        cleaned (if (and (str/includes? cleaned "{")
                         (str/includes? cleaned "}"))
                  (subs cleaned
                        (.indexOf cleaned "{")
                        (inc (.lastIndexOf cleaned "}")))
                  cleaned)]
    (json/read-str cleaned :key-fn keyword)))

(defn- parse-anthropic-body
  [body]
  (let [parsed (json/read-str body :key-fn keyword)
        text (->> (:content parsed)
                  (filter #(= "text" (:type %)))
                  first
                  :text
                  normalize-text)]
    (cond
      (map? (:parsed parsed))
      (:parsed parsed)

      (seq text)
      (parse-json-text text)

      :else
      (throw (ex-info "Issue proposer returned no JSON content"
                      {:response parsed})))))

(defn normalize-issue-proposal-packet
  [raw]
  (let [raw (or raw {})
        proposals (->> (or (:proposals raw) [])
                       (map-indexed
                        (fn [idx proposal]
                          {:proposal/id (or (:proposal-id proposal)
                                            (:proposal/id proposal)
                                            (keyword (str "prop-" (inc idx))))
                           :proposal/type :issue
                           :issue/kind (keyword (or (:issue-kind proposal)
                                                    (:issue_kind proposal)
                                                    "question"))
                           :issue/title (or (:issue-title proposal)
                                            (:issue_title proposal))
                           :source/phrasing (or (:source-phrasing proposal)
                                                (:source_phrasing proposal)
                                                [])
                           :why/unresolved (or (:why-unresolved proposal)
                                               (:why_unresolved proposal))
                           :why/now (or (:why-now proposal)
                                        (:why_now proposal))
                           :source/spans
                           (mapv (fn [span]
                                   {:doc (:doc span)
                                    :span-id (or (:span-id span)
                                                 (:span_id span))
                                    :quote (:quote span)
                                    :span/role (keyword
                                                (or (:span-role span)
                                                    (:span_role span)
                                                    "support"))})
                                 (or (:source-spans proposal)
                                     (:source_spans proposal)
                                     []))
                           :anchor/phrases {:topics (get-in proposal [:anchor-phrases :topics]
                                                            (get-in proposal [:anchor_phrases :topics] []))
                                            :tensions (get-in proposal [:anchor-phrases :tensions]
                                                              (get-in proposal [:anchor_phrases :tensions] []))
                                            :stakes (get-in proposal [:anchor-phrases :stakes]
                                                            (get-in proposal [:anchor_phrases :stakes] []))
                                            :actors (get-in proposal [:anchor-phrases :actors]
                                                            (get-in proposal [:anchor_phrases :actors] []))
                                            :projects (get-in proposal [:anchor-phrases :projects]
                                                              (get-in proposal [:anchor_phrases :projects] []))
                                            :tasks (get-in proposal [:anchor-phrases :tasks]
                                                           (get-in proposal [:anchor_phrases :tasks] []))
                                            :terms (get-in proposal [:anchor-phrases :terms]
                                                           (get-in proposal [:anchor_phrases :terms] []))}
                           :timing/hint (some-> (or (:timing-hint proposal)
                                                    (:timing_hint proposal))
                                                keyword)
                           :opposing/span-ids (vec (or (:opposing-span-ids proposal)
                                                       (:opposing_span_ids proposal)
                                                       []))}))
                       vec)]
    (issue-entry/normalize-proposal-packet
     {:schema/version 1
      :packet/id (or (:packet-id raw) (:packet_id raw) :issue-packet-1)
      :source/packet-id (or (:source-packet-id raw)
                            (:source_packet_id raw))
      :proposals proposals
      :abstain/reason (or (:abstain-reason raw)
                          (:abstain_reason raw))})))

(defn- build-user-message
  [source-packet]
  (str "Bounded source packet:\n\n"
       (json-string source-packet)
       "\n\nReturn JSON matching this example shape exactly:\n\n"
       (json-string output-example)))

(defn- call-issue-proposer
  [source-packet {:keys [api-key model temperature max-output-tokens]
                  :or {model default-model
                       temperature default-temperature
                       max-output-tokens default-max-output-tokens}}]
  (let [api-key (or api-key (System/getenv "ANTHROPIC_API_KEY"))
        model (or model default-model)]
    (when-not (seq api-key)
      (throw (ex-info "ANTHROPIC_API_KEY is required for live issue proposer mode"
                      {:mode :anthropic})))
    (let [payload {:model model
                   :temperature temperature
                   :max_output_tokens max-output-tokens
                   :system system-prompt
                   :messages [{:role "user"
                               :content (build-user-message source-packet)}]}
          request (-> (HttpRequest/newBuilder)
                      (.uri (URI/create "https://api.anthropic.com/v1/messages"))
                      (.header "content-type" "application/json")
                      (.header "x-api-key" api-key)
                      (.header "anthropic-version" "2023-06-01")
                      (.POST (HttpRequest$BodyPublishers/ofString
                              (json-string payload)))
                      (.build))
          response (.send (HttpClient/newHttpClient)
                          request
                          (HttpResponse$BodyHandlers/ofString))
          status (.statusCode response)
          body (.body response)]
      (when-not (<= 200 status 299)
        (throw (ex-info "Issue proposer request failed"
                        {:status status
                         :body body
                         :model model})))
      (normalize-issue-proposal-packet (parse-anthropic-body body)))))

(defn mock-issue-proposer
  "Deterministic proposer for tests and first-packet runs."
  [source-packet]
  (let [source-packet-id (:source-packet-id source-packet)
        s1 (first (:spans source-packet))
        s2 (second (:spans source-packet))
        s3 (nth (:spans source-packet) 2 nil)]
    (normalize-issue-proposal-packet
     {:packet-id (str "pkt-" (name source-packet-id))
      :source-packet-id source-packet-id
      :proposals
      [(cond-> {:proposal-id "prop-001"
                :proposal-type "issue"
                :issue-kind "decision"
                :issue-title "Move now to a bounded proposal seam or harden the kernel first"
                :source-phrasing ["bounded collaborator-side proposal experiment"
                                  "one more kernel-side pass"]
                :why-unresolved "The packet still contains live move-now and harden-first pressure."
                :why-now "The current attractor has stabilized and the next branch is live."
                :source-spans [{:doc (:doc s1)
                                :span-id (:span-id s1)
                                :quote (:quote s1)
                                :span-role "support"}
                               {:doc (:doc s2)
                                :span-id (:span-id s2)
                                :quote (:quote s2)
                                :span-role "support"}]
                :anchor-phrases {:topics ["typed proposal entry" "use outcome"]
                                 :tensions [["move now" "harden first"]
                                            ["proposal" "authority"]]
                                 :stakes ["kernel integrity" "next branch"]
                                 :actors ["kernel" "llm"]
                                 :projects ["collaborative pivot"]
                                 :tasks ["choose next branch"]
                                 :terms ["proposer committer"]}
                :timing-hint "now"
                :opposing-span-ids (cond-> []
                                     s3 (conj (:span-id s3)))}
         s3
         (update :source-spans conj {:doc (:doc s3)
                                     :span-id (:span-id s3)
                                     :quote (:quote s3)
                                     :span-role "counterpressure"}))
       {:proposal-id "prop-002"
        :proposal-type "issue"
        :issue-kind "hypothesis"
        :issue-title "Membrane concern initiation should directly open rule access"
        :source-phrasing ["bounded collaborator-side proposal experiment"]
        :why-unresolved "Possible next move."
        :why-now "Current branch is live."
        :source-spans [{:doc (:doc s1)
                        :span-id (:span-id s1)
                        :quote (:quote s1)
                        :span-role "support"}]
        :anchor-phrases {:topics ["typed proposal entry"]
                         :tensions [["proposal" "authority"]]
                         :stakes ["kernel integrity"]
                         :actors ["kernel"]
                         :projects ["collaborative pivot"]
                         :tasks ["choose next branch"]
                         :terms ["membrane"]}}]
      :abstain-reason nil})))

(defn build-issue-proposer
  [{:keys [mode model temperature max-output-tokens api-key]
    :or {mode :mock
         model default-model
         temperature default-temperature
         max-output-tokens default-max-output-tokens}}]
  (case mode
    :mock
    (fn [source-packet]
      (mock-issue-proposer source-packet))

    :anthropic
    (fn [source-packet]
      (call-issue-proposer source-packet
                           {:api-key api-key
                            :model model
                            :temperature temperature
                            :max-output-tokens max-output-tokens}))

    nil))
