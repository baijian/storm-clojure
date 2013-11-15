(ns com.snda.meiyu.AlogCount
  (:import 
    '(backtype.storm StormSubmitter LocalCluster))
  (:use '[backtype.storm clojure config])
  (:use '[clojure.tools.logging :only (info debug error)])
  (:use 'clj-time.core)
  (:use 'clj-time.coerce)
  (:use 'clj-time.local)
  (:use 'org.zeromq.clojure)
  (:require [clojure.contrib.sql :as sql])
  (:gen-class))

(def ctx (ZMQ/context))
(defn datain
  []
  (let [rcv (.socket ctx ZMQ/PULL)]
    (.connect rcv "tcp://127.0.0.1:6000")
    (while :true
      (let ))))

(defspout alogSpout ["log_string"]
  [conf context collector]
    (def ctx (ZMQ/context))
    (let [rcv (.socket ctx ZMQ/PULL)]
      (.connect rcv "tcp://127.0.0.1:6000")
      (spout
        (nextTuple []
          (while :true
            (let [log-msg (String. (.recv rcv))
                  interval 1]
              (Thread/sleep interval)
              (emit-spout! collector [log-msg])))))))

(defbolt filterUriBolt ["url_id", "alog_timestamp"] {:prepare true}
  [conf context collector]
  ; urls format : xxx.oo.com?/a/b/c
  (let [urls (atom {}) timestamp_record (atom 0)]
    (bolt
      (execute [tuple]
        ;init timestamp_record
        (if (= 0 timestamp_record)
          (do (reset! timestamp_record (to-long (local-now)))
              (reset! urls (sync-url))))
        ;every 3 miniutes to update uris have registered
        (if (> (- (to-long (local-now)) 180000000) timestamp_record)
          (do (reset! timestamp_record (to-long (local-now)))
              (reset! urls (sync-url))))
        (let [alog (.getString tuple 0)]
          ;regex pattern to emit
          (if (> (count urls) 0) (
            (let [timestamp (get (.split alog " ") 4) 
                  timestamp_format (str (get (.split timestamp ".") 0) (get (.split timestamp ".") 1))
                  status (get (.split alog " ") 5)
                  url (get (.split alog) 12)
                  request (get (.split alog) 13)]
              (let [method (get (.split request " ") 0)
                    uri (get (.split request " ") 1)
                    uril (str url "?" uri)]
                (if (contains? urls uril)
                  (do (emit-bolt! collector [(get urls uril) timestamp_format]:anchor tuple)
                      ;(ack! collector tuple)
                    )))))))))))

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
            ;(ack! collector tuple)
            ))))))

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

(defn sync-url
  "sync urls have registered"
  (let [urls (atom {})]
    (with-connection db
      with-query-results rs ['select id,url,uri from alog_url']
        (doseq [row rs] (swap! urls assoc {(str (row :url) "?" (row :uri)) (row :id)}))))

