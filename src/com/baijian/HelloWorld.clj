(ns com.baijian.HelloWorld
  (:use clojure.tools.trace)
  (:gen-class))

(deftrace fib [n] (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2)))))

(defn -main [& args]
  ;(println "Hello World")
  (trace [fib] (fib 3)))
