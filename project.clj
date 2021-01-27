(defproject cljc-http "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[clj-http "3.9.1"]
                 [cljs-http "0.1.46"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.0"]]}}
  :repl-options {:init-ns cljc-http.core})
