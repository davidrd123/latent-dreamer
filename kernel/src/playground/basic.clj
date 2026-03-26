(require '[clojure.walk :refer [postwalk]])
(let [counter (atom -1)
      line-counter (atom 0)
      print-touch (fn [x]
                    (print (swap! line-counter inc) ":" (pr-str x) "→ "))
      change (fn [x]
               (let [new-x (swap! counter inc)]
                 (prn new-x)
                 [new-x x]))]
  (postwalk (fn [x]
              (print-touch x)
              (change x))
            {:a 1 :b 2}))