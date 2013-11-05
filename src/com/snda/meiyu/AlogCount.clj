(ns com.snda.meiyu.AlogCount
  (:import 
    '(backtype.storm StormSubmitter LocalCluster)
    '(backtype.storm.topology IRichSpout)
    '(backtype.storm.topology IRichBolt))
  (:use '[backtype.storm clojure config])
  (:use '[clojure.tools.logging :only (info debug error)])
  (:gen-class))

(defspout alogSpout)

(defbolt filterUriBolt)

(defbolt countBolt)

(defn mk-topology []
  (topology
    {"1" (spout-spec alogSpout)
     "2" (bolt-spec {}
                    filterUriBolt
                    :p 5)
     "3" (bolt-spec {}
                    countBolt
                    :p 6)}))

(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "AlogCount" {TOPOLOGY-DEBUG true} (mk-topology))
    (Thread/sleep 10000)
    (.shutdown cluster)
    ))

(defn submit-topology! [name]
  (StormSubmitter/submitTopology
    name
    {TOPOLOGY-DEBUG true
     TOPOLOGY-WORKERS 3}
    (mk-topology)))

(defn -main
  ([]
    (run-local!))
  ([name]
    (submit-topology! name)))
