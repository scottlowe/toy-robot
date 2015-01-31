(defproject toy-robot "0.9.0-SNAPSHOT"
  :description "Toy Robot Code Challenge which is popular in Melbourne"
  :url "https://github.com/scottlowe/toy-robot"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :plugins [[lein-kibit "0.0.8"]]
  :profiles {:dev {:plugins [[lein-kibit "0.0.8"]]
                   :env {:dev true}}}
  :main toy-robot.core)
