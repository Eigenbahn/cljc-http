# cljc-http

A naive attempt at making a cljc wrapper around the well-known [clj-http](https://github.com/dakrone/clj-http) and [cljs-http](https://github.com/r0man/cljs-http) libs.

For a more production-ready libraries, see [httpurr](https://github.com/funcool/httpurr) ([doc](https://funcool.github.io/httpurr/latest/)) and [cljs-ajax](https://github.com/JulianBirch/cljs-ajax).

The main trick is to make clj-http calls be always async and returning a core.async channel, as per cljs-http conventions.

In case of errors, a response object is returned, as per cljs-http conventions. That means that for clj, we convert the [slingshot stone](https://github.com/scgilardi/slingshot) (basically an Exception) back to a response object.

Cancelling requests is possible with `cljc-http.core/abort!`, modeled around `cljs-http.core/abort!`. Note that standard clj-http also supports [cancelling async requests](https://github.com/dakrone/clj-http#cancelling-requests) by cancelling the future (promise object) holding its job.



## Usage

```Clojure
(ns test.ns
  #?(:cljs
     (:require-macros
      [cljs.core.async.macros :as asyncm :refer (go go-loop)]))
  (:require [clojure.core.async :as async]
            [cljc-http.client :as http-client]
            [cljc-http.core :as http-core]))


;; blocking sync request (clj-only as `async/<!!` not avialable in cljs)
(let [resp (async/<!! (http-client/get "http://http://example.com/resource" {:accept :json}))
      http-status (:status resp)]
  (when (http-client/unexceptional-status? http-status)
    (throw (ex-info "Unexpected HTTP response code" {:ex-type ::unexpected-http-status,
                                                     :input http-status})))
  ;; ... do stuff w/ (:body resp)
  )

;; async request
(go
  (let [resp (async/<! (http-client/get "http://http://example.com/resource" {:accept :json}))
        http-status (:status resp)]
    (when (http-client/unexceptional-status? http-status)
      (throw (ex-info "Unexpected HTTP response code" {:ex-type ::unexpected-http-status,
                                                       :input http-status})))
    ;; ... do stuff w/ (:body resp)
    ))

;; canceling sync request (clj-only as `async/alts!!` not avialable in cljs)
(let [request-channel (http-client/get "http://http://example.com/resource" {:accept :json})
      [resp alts-chan] (async/alts!! [request-channel (async/timeout 1000)])]
  (when (nil? resp)
    (http-core/abort! request-channel)))

;; canceling async request
(go
  (let [request-channel (http-client/get "http://http://example.com/resource" {:accept :json})
        [resp alts-chan] (async/alts! [request-channel (async/timeout 1000)])]
    (when (nil? resp)
      (http-core/abort! request-channel))

    ;; ... do stuff w/ resp
    ))
```


## License

Released under the MIT License: http://www.opensource.org/licenses/mit-license.php
