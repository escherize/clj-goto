(ns clj-goto.core
  (:require
   [clojure.walk :as w]))

(defn label? [x]
  (and (vector? x) (= :label (first x))))

(defn first-label [body]
  (second (first (filter label? body))))

(defn group-labels [body]
  (->> (partition-by label? body)
       (partition-all 2)
       (map (fn [[label body]]
              (let [label-name (second (first label))]
                `[~label-name (fn ~(symbol label-name) [] ~@body)])))
       (into {})))

(defn- deep-replace [x from to]
  (w/postwalk (fn [v] (if (= from v) to v)) x))

(defmacro goto-block [& body]
  (let [goto-sym (gensym "goto-sym")]
    `(let [~goto-sym (fn [label#]
                      (throw (ex-info "" {:goto/label label#})))
           blocks# ~(deep-replace (group-labels body) 'goto goto-sym)
           engine# (fn engine# [~goto-sym]
                     (try
                       ((get blocks# ~goto-sym))
                       (catch Exception e#
                         (engine# (:goto/label (ex-data e#))))))]
       (engine# ~(first-label body)))))

(let [n (atom 0)]
  (goto-block
    [:label :start]
    (goto :add)

    [:label :add]
    (swap! n inc)
    (if (>= @n 5) (goto :end) (goto :add))

    [:label :end])
  @n)
;; => 5
