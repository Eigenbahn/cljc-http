(ns cljc-http.impl.core
  (:require
   [clojure.core.async :as async :refer (<! <!! >! >!! put! close! chan go go-loop)]
   [clj-http.core :as http-core]))



;; STATE

(def pending-requests
  "Cache of requests not yet completed.
  Map of core.async channels to future handlers.

  Keeping track of them allows cancelling them."
  (atom {}))



;; EXTRA API

(defn abort!
  "Attempt to close the given channel and abort the pending HTTP request
  with which it is associated."
  [channel]
  (when-let [req-future (@pending-requests channel)]
    (.cancel req-future true)
    (swap! pending-requests dissoc channel)
    (close! channel)
    nil))
