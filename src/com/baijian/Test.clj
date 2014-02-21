(ns com.baijian.Test
  (:use [clojure.tools.logging :only (info error)])
  (:require [clojure.java.jdbc :as sql])
  (:gen-class))

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
  (sql/insert! db :users 
               {:name name :age age}))

(defn update-age [name age]
  (sql/update! db :users {:age age}
               ["name = ?" name]))

(defn query-user [name]
  (sql/query db
             ["select * from users where name = ?" name]
             :row-fn :age))

(defn delete-user [name]
  (sql/delete! db :users
               ["name = ?" name]))

(defn query-all []
  (sql/query db ["select * from users"]
    :as-arrays? true))

(defn -main [& args]
  ;(info "Insert baijian 24>>>>>>>>")
  ;(insert-user "baijian" 24)
  ;(info "Query baijian>>>>>>>>>>>>")
  ;(println (query-user "baijian"))
  ;(info "Update baijian to 25>>>>>")
  ;(update-age "baijian" 25)
  ;(info "Query baijian>>>>>>>>>>>>")
  ;(println (query-user "baijian"))
  (info "Query all <<<<<<<<<<<<<<<")
  (doseq [row (subvec (query-all) 1)] (println (nth row 2)))
  ;(info "Delete baijian>>>>>>>>>>>")
  ;(delete-user "baijian")
  (info "End>>>>>>>>>>>>>>>>>>>>>>"))
