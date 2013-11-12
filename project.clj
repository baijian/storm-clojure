(defproject com.snda.meiyu/AlogCount "0.1.0-SNAPSHOT"
  :description "Write some topologies using clojure language"
  :url "http://baijian.github.io/storm-clojure"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/java.jdbc "0.3.0-beta1"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [clj-time "0.6.0"]
                 [org.clojure/tools.logging "0.2.6"]]
  :main com.snda.meiyu.AlogCount
  :aot [com.snda.meiyu.AlogCount]
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[storm "0.8.2"]]}})
