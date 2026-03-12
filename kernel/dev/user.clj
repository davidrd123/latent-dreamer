(ns user
  "REPL helpers for the daydreamer kernel."
  (:require [clojure.tools.namespace.repl :as tnr]))

(defn refresh
  []
  (tnr/refresh))

(defn refresh-all
  []
  (tnr/refresh-all))

(comment
  (refresh)
  (refresh-all))
