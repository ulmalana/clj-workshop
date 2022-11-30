(ns ch09-host-interop.core
  (:import [java.math BigDecimal BigInteger]
           [java.time LocalTime ZonedDateTime]
           [java.util Locale Scanner])
  (:require [ch09-host-interop.utils :as utils])
  (:gen-class))

(def big-num (new BigDecimal "100000"))
(def big-num' (BigDecimal. "999999999"))

(Locale. "id")
;; => #object[java.util.Locale 0x38ef28b4 "in"]

;; static method of class LocalTime
(LocalTime/now)
;; => #object[java.time.LocalTime 0x9b6b0bb "09:27:27.434413"]

;; static field of class LocalTime
(LocalTime/MIDNIGHT)
;; => #object[java.time.LocalTime 0x32b7792a "00:00"]

;; instance method of big-num. use dot notation
(.negate big-num)
;; => -100000M
(.pow big-num 2)
;; => 10000000000M

;; exercise 9.03
(ZonedDateTime/now)
;; => #object[java.time.ZonedDateTime 0x2aaf58d8 "2022-11-30T09:34:19.013164+07:00[Asia/Jakarta]"]

(. (ZonedDateTime/now) getOffset)
;; => #object[java.time.ZoneOffset 0x34021232 "+07:00"]

;; or

(.getOffset (ZonedDateTime/now))
;; => #object[java.time.ZoneOffset 0x34021232 "+07:00"]

;;; get time difference to UTC in seconds
(. (. (ZonedDateTime/now) getOffset) getTotalSeconds)
;; => 25200
;; or
(.. (ZonedDateTime/now) getOffset getTotalSeconds)
;; => 25200
;; or
(.getTotalSeconds (.getOffset (ZonedDateTime/now)))
;; => 25200

(let [string (StringBuffer. "quick")]
  (.append string " brown")
  (.append string " fox")
  (.append string " jumped")
  (.append string " over")
  (.append string " the")
  (.append string " lazy")
  (.append string " dog")
  (.toString string)
  (println string))
;; => #object[java.lang.StringBuffer 0x3cbf03ca quick brown fox jumped over the lazy dog]

;; using doto macro
(let [string (StringBuffer. "quick")]
  (doto string
    (.append " brown")
    (.append " fox")
    (.append " jumped")
    (.append " over")
    (.append " the")
    (.append " lazy")
    (.append " dog"))
  (.toString string)
  (println string))
;; => #object[java.lang.StringBuffer 0x43abfd44 quick brown fox jumped over the lazy dog]

;; exercise 9.04
(def ^:const price-menu {:latte 0.5 :mocha 0.4})
(def input (Scanner. System/in))

(defn- buy-coffee [type]
  (println "How many coffees do you want to buy?")
  (let [choice (.nextInt input)
        price (utils/calculate-coffee-price price-menu type choice)]
    (utils/display-bought-coffee-message type choice price)))

(defn- show-menu []
  (println "| Available coffees |")
  (println "| 1.Latte   2.Mocha |")
  (let [choice (.nextInt input)]
    (case choice
      1 (buy-coffee :latte)
      2 (buy-coffee :mocha))))

(defn- show-orders []
  (println "\n")
  (println "Display orders here"))

(defn- start-app []
  "Displaying main menu and processing user choices."
  (let [run-application (ref true)]
    (while (deref run-application)
      (println "\n|\t Coffe app \t|")
      (println "| 1-Menu 2-Orders 3-Exit |\n")
      (let [choice (.nextInt input)]
        (case choice
          1 (show-menu)
          2 (show-orders)
          3 (dosync (ref-set run-application false)))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
