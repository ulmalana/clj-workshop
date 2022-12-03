(ns ch10-testing.core
  (:require [ch10-testing.utils :as utils])
  (:import [java.util Scanner])
  (:gen-class))

(def input (Scanner. System/in))
(def ^:const price-menu {:latte 0.5 :mocha 0.4})

(defn- buy-coffee [type]
  (println "How many coffees do you want to buy?")
  (let [choice (.nextInt input)
        price (utils/calculate-coffee-price price-menu type choice)]
    (utils/display-bought-coffee-message type choice price)))

(defn- show-menu []
  (println "| Available coffees |")
  (println "|1. Latte  2. Mocha |")
  (let [choice (.nextInt input)]
    (case choice
      1 (buy-coffee :latte)
      2 (buy-coffee :mocha))))

(defn- show-orders []
  (println "\n")
  (println "Display orders here"))

(defn- start-app []
  (let [run-app (ref true)]
    (while (deref run-app)
      (println "\n|\t Coffee app \t|")
      (println "| 1-Menu 2-Order 3-Exit |\n")
      (let [choice (.nextInt input)]
        (case choice
          1 (show-menu)
          2 (show-orders)
          3 (dosync (ref-set run-app false)))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (start-app))
