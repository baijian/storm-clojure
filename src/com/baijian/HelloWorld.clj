(ns com.baijian.HelloWorld
  (:use [clojure.tools.logging :only (info error)])
  (:require [clojure.contrib.sql :as sql])
  (:gen-class))

(defn -main [& args]
  (info "Enter main method")
  (println "This is my first clojure coding: " args)
  (info "After print args"))

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

(defn insert-user [name age]
  (sql/with-connection db
    (sql/insert-values :users [:name :age] [name age])))

(defn update-user [id attribute-map]
  (sql/with-connection db
    (sql/update-values :users ["id=?" id] attribute-map)))

(defn delete-user [id]
  (sql/with-connection db
    (sql/delete-rows :users ["id=?" id])))

(defn prepare-insert-user [name age]
  (let [sql "insert into storm.users (name, age) values(?, ?)"]
    (sql/with-connection db
      (sql/do-prepared sql ["baijian" 12] ))))

(defn query-user [id]
  (sql/with-connection db
    (sql/with-query-results rs ["select * from users whre id=?" id]
      (dorun (map #(println %) rs)))))
