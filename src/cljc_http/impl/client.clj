(ns cljc-http.impl.client
  (:refer-clojure :exclude [get update])
  (:require
   [clojure.core.async :as async :refer (<! <!! >! >!! put! close! chan go go-loop)]
   [clj-http.client :as http-client]
   [cljc-http.impl.core :as http-core])
  (:use [slingshot.slingshot :only [try+]]))


(declare core-asyncify-http-fn core-asyncified-http-fn
         slingshot-stone->dummy-resp)




;; IMPL

(defn core-asyncify-http-fn [http-fn url & [req]]
  (let [req (-> (or req {})
                (conj {:async? true}))  ; ensure asynchronous
        c (chan)
        success-fn #(do (put! c %)
                        (swap! http-core/pending-requests dissoc c)
                        (close! c))
        exception-fn #(do
                        (put! c (slingshot-stone->dummy-resp %))
                        (swap! http-core/pending-requests dissoc c)
                        (close! c))]
    (->> (http-fn url req
                  success-fn exception-fn) ; call
         (swap! http-core/pending-requests assoc c))
    c))

(defn core-asyncified-http-fn [http-fn]
  (fn [url & [req]]
    (core-asyncify-http-fn http-fn url req)))

(defn slingshot-stone->dummy-resp [e]
  (let [e-map (Throwable->map e)
        e-data (:data e-map)
        http-errno (:status e-data)
        ]
    {:status http-errno
     :success false
     :body (:body e-data)
     :headers (:headers e-data)
     :error-code :http-error
     :error-text (str (:reason-phrase e-data) " [" http-errno "]")}))



;; API

;; NB: `copy` & `update` not implemented in cljs and `jsonp` not implemented in clj

(def get (core-asyncified-http-fn http-client/get))
(def head (core-asyncified-http-fn http-client/head))
(def post (core-asyncified-http-fn http-client/post))
(def put (core-asyncified-http-fn http-client/put))
(def delete (core-asyncified-http-fn http-client/delete))
(def move (core-asyncified-http-fn http-client/move))
(def patch (core-asyncified-http-fn http-client/patch))
