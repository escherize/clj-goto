(ns hooks.clj-goto.core
  (:require [clj-kondo.hooks-api  :as api]))

(defn goto-block [{:keys [node]}]
  (let [new-node (api/list-node
                   (list*
                     (api/token-node 'let)
                     (api/vector-node
                       [(api/token-node 'goto)
                        (api/list-node
                          [(api/token-node 'fn)
                           (api/vector-node [(api/token-node '&)
                                             (api/token-node '_)])])])
                     (:children node)))]
    #_(prn (api/sexpr new-node))
    {:node new-node}))
