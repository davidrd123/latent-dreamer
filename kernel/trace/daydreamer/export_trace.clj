(ns daydreamer.export-trace
  "Impure trace export edge for benchmark logs."
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.string :as str]
            [daydreamer.benchmarks.puppet-knows :as puppet]))

(def ^:private benchmark-specs
  {:scripted {:run-benchmark puppet/run-benchmark
              :default-output-path "out/puppet_knows_benchmark.json"
              :default-html-path "out/puppet_knows_benchmark.html"
              :default-title "Puppet Knows Benchmark: Scripted"
              :label "scripted"}
   :semi-unscripted {:run-benchmark puppet/run-semi-unscripted-benchmark
                     :default-output-path "out/puppet_knows_semi_unscripted.json"
                     :default-html-path "out/puppet_knows_semi_unscripted.html"
                     :default-title "Puppet Knows Benchmark: Semi-Unscripted"
                     :label "semi-unscripted"}})

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
    (if-let [flag (first args)]
      (case flag
        "--render-html" (recur (rest args) (assoc options :render-html? true))
        "--out" (let [[_ value & more] args]
                  (recur more (assoc options :out-path value)))
        "--scope-root" (let [[_ value & more] args]
                         (recur more (assoc options :scope-root value)))
        "--benchmark" (let [[_ value & more] args]
                        (recur more (assoc options :benchmark value)))
        "--html-out" (let [[_ value & more] args]
                       (recur more (assoc options :html-out value)))
        "--title" (let [[_ value & more] args]
                    (recur more (assoc options :title value)))
        (throw (ex-info "Unknown CLI flag"
                        {:flag flag
                         :args args})))
      options)))

(defn- parse-benchmark
  [value]
  (let [benchmark (keyword (or value "scripted"))]
    (case benchmark
      :scripted :scripted
      :semi :semi-unscripted
      :semi-unscripted :semi-unscripted
      (throw (ex-info "Unknown benchmark variant"
                      {:benchmark value
                       :supported (sort (map name (keys benchmark-specs)))})))))

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

(defn- benchmark-spec
  [benchmark]
  (or (get benchmark-specs benchmark)
      (throw (ex-info "Missing benchmark spec"
                      {:benchmark benchmark}))))

(defn- default-output-path
  [benchmark]
  (:default-output-path (benchmark-spec benchmark)))

(defn- default-html-path
  [benchmark]
  (:default-html-path (benchmark-spec benchmark)))

(defn- default-title
  [benchmark]
  (:default-title (benchmark-spec benchmark)))

(defn build-puppet-knows-log
  "Return the reporter-shaped Puppet Knows benchmark log with resolved fixture
  metadata."
  ([] (build-puppet-knows-log {}))
  ([{:keys [benchmark scope-root git-commit]}]
   (let [benchmark (parse-benchmark benchmark)
         scope-root (or scope-root (default-scope-root))
         {:keys [run-benchmark]} (benchmark-spec benchmark)
         {:keys [world-path graph-path feedback-path reporter-path]}
         (fixture-paths scope-root)]
     (:log (run-benchmark
            (merge {:world_path world-path
                    :graph_path graph-path
                    :feedback_path feedback-path
                    :palette_path (palette-path world-path)
                    :git_commit (or git-commit
                                    (repo-commit (latent-root)))
                    :reporter_path reporter-path}
                   (graph-counts graph-path)))))))

(defn- render-report!
  [{:keys [json-path html-path scope-root title label]}]
  (let [scope-root (or scope-root (default-scope-root))
        {:keys [reporter-path]} (fixture-paths scope-root)
        html-file (io/file (latent-root) html-path)
        json-file (io/file json-path)
        command (if label
                  ["python3"
                   reporter-path
                   (.getCanonicalPath json-file)
                   "--label"
                   label
                   "--title"
                   title
                   "--out"
                   (.getCanonicalPath html-file)]
                  ["python3"
                   reporter-path
                   (.getCanonicalPath json-file)
                   "--title"
                   title
                   "--out"
                   (.getCanonicalPath html-file)])
        {:keys [exit err]} (apply sh/sh command)]
    (when-not (zero? exit)
      (throw (ex-info "Reporter HTML render failed"
                      {:exit exit
                       :stderr err
                       :json-path (.getCanonicalPath json-file)
                       :html-path (.getCanonicalPath html-file)})))
    (.getCanonicalPath html-file)))

(defn write-puppet-knows-log!
  "Write the reporter-shaped Puppet Knows benchmark log to disk and return the
  absolute output path."
  ([] (write-puppet-knows-log! {}))
  ([{:keys [benchmark out-path scope-root git-commit]}]
   (let [benchmark (parse-benchmark benchmark)
         out-path (or out-path (default-output-path benchmark))
         output-file (io/file (latent-root) out-path)
         payload (build-puppet-knows-log {:benchmark benchmark
                                          :scope-root scope-root
                                          :git-commit git-commit})]
     (.mkdirs (.getParentFile output-file))
     (spit output-file (json/write-str payload :escape-unicode false))
     (.getCanonicalPath output-file))))

(defn write-puppet-knows-report!
  "Write reporter JSON, and optionally HTML, for a Puppet Knows benchmark
  variant. Returns the written paths."
  ([] (write-puppet-knows-report! {}))
  ([{:keys [benchmark out-path html-out scope-root git-commit render-html? title]}]
   (let [benchmark (parse-benchmark benchmark)
         json-path (write-puppet-knows-log! {:benchmark benchmark
                                             :out-path out-path
                                             :scope-root scope-root
                                             :git-commit git-commit})
         html-path (when render-html?
                     (render-report! {:json-path json-path
                                      :html-path (or html-out
                                                     (default-html-path benchmark))
                                      :scope-root scope-root
                                      :title (or title
                                                 (default-title benchmark))
                                      :label (:label (benchmark-spec benchmark))}))]
     {:json-path json-path
      :html-path html-path})))

(defn -main
  [& args]
  (try
    (let [{:keys [benchmark out-path html-out render-html? scope-root title]}
          (parse-args args)
          {:keys [json-path html-path]}
          (write-puppet-knows-report! {:benchmark benchmark
                                       :out-path out-path
                                       :html-out html-out
                                       :render-html? render-html?
                                       :scope-root scope-root
                                       :title title})]
      (println json-path)
      (when html-path
        (println html-path)))
    (finally
      (shutdown-agents))))
