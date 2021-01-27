(ns cljc-http.core
  (:require
   #?(:clj [cljc-http.impl.core :as http-core])
   #?(:cljs [cljs-http.core :as http-core])))



;; EXTRA API

(def abort! http-core/abort!)
