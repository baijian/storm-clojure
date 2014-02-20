(defproject storm-clojure "0.0.1-SNAPSHOT"
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
                 [org.clojure/tools.logging "0.2.6"]
                 [org.zeromq/jzmq "2.2.2-SNAPSHOT"]
                 [org.clojars.mikejs/clojure-zmq "2.0.7-SNAPSHOT"]]
  :native-path "/usr/local/lib"
  :main com.baijian.HelloWorld
  :aot [com.baijian.HelloWorld]
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[storm "0.9.0.1"]]}})
