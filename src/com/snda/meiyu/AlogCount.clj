(ns com.snda.meiyu.AlogCount
  (:import [backtype.storm StormSubmitter LocalCluster])
  (:use [backtype.storm clojure config])
  (:use [clojure.tools.logging :only (info debug error)])
  (:gen-class))

(defspout alogSpout)

(defbolt filterUriBolt)

(defbolt countBolt)

(defn -main
  ([]
    (run-local!))
  ([name]
    (submit-topology! name)))
