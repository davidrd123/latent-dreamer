(ns daydreamer.export-trace
  "Impure trace export edge for benchmark logs."
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.string :as str]
            [daydreamer.benchmarks.puppet-knows :as puppet]))

(def ^:private default-output-path "out/puppet_knows_benchmark.json")

(defn- cwd-file
  []
  (.getCanonicalFile (io/file (System/getProperty "user.dir"))))

(defn- latent-root
  []
  (let [cwd (cwd-file)]
    (if (= "kernel" (.getName cwd))
      (.getCanonicalFile (.getParentFile cwd))
      cwd)))

(defn- default-scope-root
  []
  (let [repo-root (latent-root)
        lora-root (.getParentFile repo-root)]
    (.getCanonicalPath (io/file lora-root "scope-drd"))))

(defn- parse-args
  [args]
  (loop [args args
         options {}]
    (if-let [[flag value & more] (seq args)]
      (case flag
        "--out" (recur more (assoc options :out-path value))
        "--scope-root" (recur more (assoc options :scope-root value))
        (throw (ex-info "Unknown CLI flag"
                        {:flag flag
                         :args args})))
      options)))

(defn- fixture-paths
  [scope-root]
  (let [{:keys [world-path graph-path feedback-path reporter-path]}
        puppet/fixture-relative-paths
        scope-root (io/file scope-root)]
    {:world-path (.getCanonicalPath (io/file scope-root world-path))
     :graph-path (.getCanonicalPath (io/file scope-root graph-path))
     :feedback-path (.getCanonicalPath (io/file scope-root feedback-path))
     :reporter-path (.getCanonicalPath (io/file scope-root reporter-path))}))

(defn- palette-path
  [world-path]
  (some->> (slurp world-path)
           (re-find #"(?m)^source_palette:\s*\"([^\"]+)\"")
           second))

(defn- graph-counts
  [graph-path]
  (let [payload (json/read-str (slurp graph-path) :key-fn keyword)]
    {:graph_nodes (count (:nodes payload))
     :graph_edges (count (:edges payload))}))

(defn- repo-commit
  [repo-root]
  (let [{:keys [exit out]} (sh/sh "git"
                                  "-C"
                                  (.getPath (io/file repo-root))
                                  "rev-parse"
                                  "--short"
                                  "HEAD")]
    (when (zero? exit)
      (str/trim out))))

(defn build-puppet-knows-log
  "Return the reporter-shaped Puppet Knows benchmark log with resolved fixture
  metadata."
  ([] (build-puppet-knows-log {}))
  ([{:keys [scope-root git-commit]}]
   (let [scope-root (or scope-root (default-scope-root))
         {:keys [world-path graph-path feedback-path reporter-path]}
         (fixture-paths scope-root)]
     (:log (puppet/run-benchmark
            (merge {:world_path world-path
                    :graph_path graph-path
                    :feedback_path feedback-path
                    :palette_path (palette-path world-path)
                    :git_commit (or git-commit
                                    (repo-commit (latent-root)))
                    :reporter_path reporter-path}
                   (graph-counts graph-path)))))))

(defn write-puppet-knows-log!
  "Write the reporter-shaped Puppet Knows benchmark log to disk and return the
  absolute output path."
  ([] (write-puppet-knows-log! {}))
  ([{:keys [out-path scope-root git-commit]}]
   (let [out-path (or out-path default-output-path)
         output-file (io/file (latent-root) out-path)
         payload (build-puppet-knows-log {:scope-root scope-root
                                          :git-commit git-commit})]
     (.mkdirs (.getParentFile output-file))
     (spit output-file (json/write-str payload :escape-unicode false))
     (.getCanonicalPath output-file))))

(defn -main
  [& args]
  (let [{:keys [out-path scope-root]} (parse-args args)
        written-path (write-puppet-knows-log! {:out-path out-path
                                               :scope-root scope-root})]
    (println written-path)))
