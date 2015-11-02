(defproject worker "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
      [org.clojure/clojure "1.6.0"]
      [amazonica "0.3.39"]
      [org.clojure/data.json "0.2.6"]
      [org.clojure/core.async "0.2.371"]
      [clj-aws-s3 "0.3.10"]
  ]
  :main ^:skip-aot worker.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
