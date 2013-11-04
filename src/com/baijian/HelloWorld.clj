(ns com.baijian.HelloWorld
  (:use [clojure.tools.logging :only (info error)])
  (:gen-class))

(defn -main [& args]
  (info "Enter main method")
  (println "This is my first clojure coding: " args)
  (info "After print args"))
