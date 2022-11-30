(ns ch09-host-interop.core
  (:import [java.math BigDecimal BigInteger]
           [java.time LocalTime ZonedDateTime]
           [java.util Locale Scanner])
  (:require [ch09-host-interop.utils :as utils]
            [ch09-host-interop.book-utils :as book-utils])
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

;; exercise 9.04 and 9.05
(def ^:const price-menu {:latte 0.5 :mocha 0.4})
(def ^:const orders-file "orders.edn")
(def input (Scanner. System/in))

(defn- buy-coffee [type]
  (println "How many coffees do you want to buy?")
  (let [choice (.nextInt input)
        price (utils/calculate-coffee-price price-menu type choice)]
    (utils/save-coffee-order orders-file type choice price)
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
  (doseq [order (utils/load-orders orders-file)]
    (println (utils/display-order order))))

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

;; exercise 9.06
(def capitals ["Berlin" "Oslo" "Warszawa" "Belgrad"])

(class capitals)
;; => clojure.lang.PersistentVector

;; create java arraylist from clojure vector
(def destinations (java.util.ArrayList. capitals))

(class destinations)
;; => java.util.ArrayList

;; convert from java arraylist to clojure vector
(class (vec destinations))
;; => clojure.lang.PersistentVector

;; convert clojure map to java hashmap
(def fluss {"Germany" "Rhein" "Poland" "Vistula"})
(class fluss)
;; => clojure.lang.PersistentArrayMap

(def rivers (java.util.HashMap. fluss))
(class rivers)
;; => java.util.HashMap

;; from java hashmap to clojure map
(class (into {} rivers))
;; => clojure.lang.PersistentArrayMap

;;;; activity 9.01
(def ^:const books {:2019 {:clojure {:title "Hands-on reactive programming with Clojure" :price 20}
                           :go {:title "Go Cookbook" :price 18}}
                    :2018 {:clojure {:title "Clojure Microservices" :price 15}
                           :go {:title "Advanced Go programming" :price 25}}})

(def ^:const book-orders "book-orders.edn")

(defn- show-orders-by-year [year]
  (println "\n")
  (doseq [order (filter #(= year (:year %)) (book-utils/load-orders book-orders))]
    (println (book-utils/display-order order books))))


(defn book-show-order []
  (println "| Books by published year |")
  (println "|1. 2019  2. 2018 |")
  (let [choice (.nextInt input)]
    (case choice
      1 (show-orders-by-year :2019)
      2 (show-orders-by-year :2018))))

(defn- buy-book [year prog-lang]
  (println "How many books do you want to buy?")
  (let [choice (.nextInt input)
        price (book-utils/calculate-book-price (get books year) prog-lang choice)]
    (book-utils/save-book-order book-orders year prog-lang choice price)
    (book-utils/display-bought-book-message (:title (get (get books year) prog-lang)) choice price)))

(defn- show-year-menu [year]
  (let [year-books (get books year)]
    (println "| Books in" (name year) "|")
    (println "| 1. " (:title (:clojure year-books)) " 2. "
             (:title (:go year-books)) "|")
    (let [choice (.nextInt input)]
      (case choice
        1 (buy-book year :clojure)
        2 (buy-book year :go)))))

(defn- book-show-menu []
  (println "| Available books by year |")
  (println "| 1. 2019   2. 2018 |")
  (let [choice (.nextInt input)]
    (case choice
      1 (show-year-menu :2019)
      2 (show-year-menu :2018))))

(defn- book-start-app []
  "Displaying main menu and processing user choices"
  (let [run-app (ref true)]
    (while (deref run-app)
      (println "\n|\t Books app \t|")
      (println "| 1-Menu 2-Orders 3-Exit|\n")
      (let [choice (.nextInt input)]
        (case choice
          1 (book-show-menu)
          2 (book-show-order)
          3 (dosync (alter run-app (fn [_] false))))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (book-start-app))
