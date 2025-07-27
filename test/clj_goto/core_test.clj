(ns clj-goto.core-test
  (:require [clj-goto.core :as goto]
            [clojure.test :refer [deftest is]]))

(deftest a-test
  (is (let [n (atom 0)]
        (goto/block
          [:label :start]
          (goto :add)

          [:label :add]
          (swap! n inc)
          (goto
            (if (>= @n 5) :end :add))

          [:label :end])
        @n)
      5))

(deftest no-blowing-stack-test
  (is (let [n (atom 0)]
        (goto/block
          [:label :start]
          (goto :add)

          [:label :add]
          (swap! n inc)
          (goto
            (if (>= @n 50000) :end :add))

          [:label :end])
        @n)
      50000))
