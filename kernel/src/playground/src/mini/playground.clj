(ns mini.playground
  (:require [clojure.walk :as walk]))

; This project has custom configuration.
; See .vscode/settings.json

; If you are new to Calva, you may want to use the command:
; Calva: Create a “Getting Started” REPL project
; which creates a project with a an interactive Calva (and Clojure) guide.
(def thing {:page/tags [{:tag/category "lslsls"}]})

(walk/postwalk #(if (keyword? %) (keyword (name %)) %) thing)