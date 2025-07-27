(ns clj-goto.core
  (:require
   [clojure.walk :as w]))

(defn- label? [x]
  (and (vector? x) (= :label (first x))))

(defn- first-label [body]
  (second (first (filter label? body))))

(defn- group-labels [body]
  (->> (partition-by label? body)
       (partition-all 2)
       (map (fn [[label body]]
              (let [label-name (second (first label))]
                `[~label-name (fn ~(symbol label-name) [] ~@body)])))
       (into {})))

(defn- deep-replace [x from to]
  (w/postwalk (fn [v] (if (= from v) to v)) x))

(defmacro block [& body]
  (let [goto-sym (gensym "goto-sym")]
    `(let [~goto-sym (fn [label#]
                       (throw (ex-info "" {::label label#})))
           blocks# ~(deep-replace (group-labels body) 'goto goto-sym)
           engine# (fn engine# [intial-labe<l#]
                     (loop [label# intial-label#]
                       (let [out# (try ((get blocks# label#))
                                       (catch Exception e# e#))]
                         (if-let [label# (and
                                           (instance? clojure.lang.ExceptionInfo out#)
                                           (::label (ex-data out#)))]
                           (recur label#)
                           out#))))]
       (engine# ~(first-label body)))))
