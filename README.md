# clj-goto

It's `goto`, in Clojure :tada:

``` clojure
(def n (atom 0))

(goto/block
  [:label :start]
  (println "Starting up")
  (goto :add)

  [:label :add]
  (swap! n inc)
  (if (>= @n 5) (goto :end) (goto :add))

  [:label :end]
  (println "all done!"))

@n
;; => 5
```
