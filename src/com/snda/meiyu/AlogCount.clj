(ns com.snda.meiyu.AlogCount
  (:import 
    '(backtype.storm StormSubmitter LocalCluster))
  (:use '[backtype.storm clojure config])
  (:use '[clojure.tools.logging :only (info debug error)])
  (:use 'clj-time.core)
  (:use 'clj-time.coerce)
  (:require [clojure.contrib.sql :as sql])
  (:gen-class))

(defspout alogSpout
  [conf context collector]
    (let [sentences ["hello"
                     "world"]]
      (spout
        (nextTuple []
          (Thread/sleep 100)
          (emit-spout! collector [(rand-nth sentences)])
          )
        (ack [id]
          ;; ack
          ))))

(defbolt filterUriBolt ["url_id", "alog_time"] {:prepare true}
  [conf context collector]
  (let [counts (atom {})]
    (bolt
      (execute [tuple]
        (let [word (.getString tuple 0)]
          (swap! counts (partial merge-with +){word 1})
          (emit-bolt! collector [word (@counts word)]:anchor tuple)
          (ack! collector tuple)
          )))))

(defbolt countBolt {:prepare true}
  [conf context collector]
  (let [timer (atom {}) counter (atom {}) ]
    (bolt
      (execute [tuple]
        ;timestamp / seconds + 000 to milliseconds
        (let [url_id (.getString tuple 0)
              current_timestamp (.getString 1)
              current_minute_timestamp (to-long (date-time (year (from-long current_timestamp))
                 (month (from-long current_timestamp)) (day (from-long current_timestamp))
                 (hour (from-long current_timestamp)) (minute (from-long current_timestamp)) ))]
          (if-not (contains? timer url_id) (swap! timer conj {url_id current_minute_timestamp}))
          (if-not (contains? counter url_id) (swap! counter conj {url_id 0}))
          (let [record_minute_timestamp (get timer url_id)
                interval (- current_minute_timestamp record_minute_timestamp)]
            (if (> interval 60000000)
              (do (insert-count [url_id url_time url_count])
                  (swap! timer assoc {url_id current_minute_timestamp})
                  (swap! counter assoc {url_id 0})))
            ;(emit-bolt! collector )
            (swap! counter (partial merge-with +) {url_id 1})
            (ack! collector tuple))
        )))))

(defn mk-topology []
  (topology
    {"1" (spout-spec alogSpout
                    :p 10)}
    {"2" (bolt-spec {"1" :shuffle}
                    filterUriBolt
                    :p 10)
     "3" (bolt-spec {"2" ["url_id"]}
                    countBolt
                    :p 20)}))

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
     TOPOLOGY-WORKERS 15}
    (mk-topology)))

(defn -main
  ([]
    (run-local!))
  ([name]
    (submit-topology! name)))


(let [db-host "localhost"
      db-port 3306
      db-name "storm"
      db-user "storm"
      db-pass "storm"]
  (def db {:classname "com.mysql.jdbc.Driver"
           :subprotocol "mysql"
           :subname (str "//" db-host ":" db-port "/" db-name)
           :user db-user
           :password db-pass}))

(defn insert-count [url_id url_time url_count]
  (with-connection db
    (sql/insert-values :alog_count [:url_id :time :count] [url_id, url_time, url_count])))

