(ns cljc-http.client
  (:refer-clojure :exclude [get update])
  (:require
   #?(:clj [cljc-http.impl.client :as http-client])
   #?(:cljs [cljs-http.client :as http-client])))




;; API

(def get http-client/get)
(def head http-client/head)
(def post http-client/post)
(def put http-client/put)
(def delete http-client/delete)
(def move http-client/move)
(def patch http-client/patch)

(def unexceptional-status? http-client/unexceptional-status?)
