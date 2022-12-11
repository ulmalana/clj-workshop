(ns ch12-concurrency.core
  (:require [clojure.tools.cli :as cli]
            [ch12-concurrency.crowdspell.fetch :as fetch])
  (:gen-class))


;; exercise 12.01
(def random-ints
  (doall (take 1000000
               (repeatedly (partial rand-int 1000)))))


(defn int-count [i xs]
  (count (filter #(= % i) xs)))

;; count the number of occurrences of some ints
(map #(int-count % random-ints) [0 1 2 45 788 500 999])
;; => (1007 995 977 978 958 1007 997)

(time (doall (map #(int-count % random-ints) [0 1 2 45 788 500 999])))
;; => (1007 995 977 978 958 1007 997)
;; "Elapsed time: 2101.94957 msecs"

;; compared to pmap
(time (doall (pmap #(int-count % random-ints) [0 1 2 45 788 500 999])))
;; => (1007 995 977 978 958 1007 997)
;; "Elapsed time: 805.682815 msecs"

;; futures
(defn expensive-calc-1 [i]
  (Thread/sleep (+ 500 (rand 1000)))
  (println "calc 1")
  (+ i 5))

(defn expensive-calc-2 [i]
  (Thread/sleep (+ 500 (rand 1000)))
  (println "calc 2")
  (+ i 10))

(+ (expensive-calc-1 10) (expensive-calc-2 25))
;; calc 1
;; calc 2
;; => 50

(let [c1 (future (expensive-calc-1 10))
      c2 (future (expensive-calc-2 20))]
  (+ (deref c1) (deref c2)))
;; calc 2
;; calc 1
;; 45

(let [c1 (future (expensive-calc-1 10))
      c2 (future (expensive-calc-2 20))]
  (+ @c1 @c2))
;; calc 1
;; calc 2
;; => 45

;; exercise 12.02
(cli/parse-opts ["--language" "fr" "Cloj" "Clojure"] [["-l" "--language LANG" "Language code for search"]])
;; => {:options {:language "fr"}, :arguments ["Cloj" "Clojure"], :summary "  -l, --language LANG  Language code for search", :errors nil}

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;;(println (fetch/get-best-word "en" args))
  (let [parsed (cli/parse-opts
                args
                [["-l" "--language LANG" "Two letter language code for search" :default "en"]])]
    (fetch/get-best-word (get-in parsed [:options :language])
                         (:arguments parsed))))

(-main "-l" "en" "Klojure" "Cljure" "Clojure")
;; => "Clojure"

;;; atoms
(def integer-atom (atom 5))

@integer-atom;; => 5

(swap! integer-atom inc)
;; => 6

(deref integer-atom)
;; => 6

(do
  (future (do (Thread/sleep 500) (swap! integer-atom + 500)))
  (future (swap! integer-atom * 1000))
  @integer-atom)
;; => 6000
@integer-atom;; => 6500

(do
  (def integer-atom (atom 6))
  (future (swap! integer-atom + 500))
  (future (do (Thread/sleep 500) (swap! integer-atom * 1000)))
  (deref integer-atom))
;; => 506
@integer-atom
;; => 506000

(do
  (def integer-atom (atom 5))
  (future (swap! integer-atom (fn [a] (Thread/sleep 2000) (* a 100))))
  (future (swap! integer-atom (fn [a] (Thread/sleep 500) (+ a 200))))
  @integer-atom)
;; => 5

@integer-atom
;; => 205

@integer-atom
;; => 20500

;;; refs
;; exercise 12.03
(def client-account (ref 2100))
(def broker-account (ref 10000))
(def acme-corp-share-price (ref 22))

(def client-stocks (ref {:acme-corp 0}))
(def broker-stocks (ref {:acme-corp 50}))

(defn buy-acme-corp-shares [n]
  (dosync
   (let [purchase-price (* n @acme-corp-share-price)]
     (alter client-account #(- % purchase-price))
     (alter broker-account #(+ % purchase-price))
     (alter client-stocks update :acme-corp #(+ % n))
     (alter broker-stocks update :acme-corp #(- % n)))))

(buy-acme-corp-shares 1)
;; => {:acme-corp 49}

@client-account
;; 2078
@client-stocks
;; {:acme-corp 1}
@broker-account
;; 10022
@broker-stocks
;; {:acme-corp 49}

(defn buy-acme-corp-shares' [n]
  (dosync
   (let [purchase-price (* n @acme-corp-share-price)]
     (println "Lets buy" n "stocks at" purchase-price "per stock")
     (Thread/sleep 1000)
     (alter client-account #(- % purchase-price))
     (alter broker-account #(+ % purchase-price))
     (alter client-stocks update :acme-corp #(+ % n))
     (alter broker-stocks update :acme-corp #(- % n)))))

(defn reset-accounts []
  (dosync
   (ref-set acme-corp-share-price 22)
   (ref-set client-account 2100)
   (ref-set broker-account 10000)
   (ref-set client-stocks {:acme-corp 0})
   (ref-set broker-stocks {:acme-corp 50})))

(reset-accounts)

(do
  (reset-accounts)
  (future (buy-acme-corp-shares' 1))
  (future (dosync
           (Thread/sleep 300)
           (alter client-account + 500))))
;; => Lets buy 1 stocks at 22 per stock
;; => #future[{:status :pending, :val nil} 0x423ccf48] Lets buy 1 stocks at 22 per stock

@client-account
;; => 2578

(do
  (reset-accounts)
  (future (buy-acme-corp-shares' 1))
  (future (dosync
           (Thread/sleep 300)
           (alter client-account + 500)))
  (future (dosync
           (Thread/sleep 350)
           (alter broker-account - 200)))
  (future (dosync
           (Thread/sleep 600)
           (alter broker-account + 1200))))
;; => Lets buy 1 stocks at 22 per stock
;; => #future[{:status :pending, :val nil} 0x2e341de9] Lets buy 1 stocks at 22 per stock
;; => Lets buy 1 stocks at 22 per stock

@broker-account
;; => 11022

@client-account
;; => 2578

;; change the price inside dosync
(do
  (reset-accounts)
  (future (buy-acme-corp-shares' 1))
  (future (dosync
           (Thread/sleep 300)
           (alter acme-corp-share-price + 10))))
;; => Lets buy 1 stocks at 22 per stock
;; => #future[{:status :pending, :val nil} 0x6fbf6cfb]

@client-account
;; => 2078

;; exercise 12.04
;; coherence using ensure
(defn buy-acme-corp-shares'' [n]
  (dosync
   (let [price (ensure acme-corp-share-price)]
     (println "Lets buy" n "stocks at" price "per stock")
     (Thread/sleep 1000)
     (alter client-account #(- % price))
     (alter broker-account #(+ % price))
     (alter client-stocks update :acme-corp #(+ % n))
     (alter broker-stocks update :acme-corp #(- % n)))))

(do
  (reset-accounts)
  (future (buy-acme-corp-shares'' 1))
  (future (dosync
           (Thread/sleep 300)
           (println "raising share price to " (+ @acme-corp-share-price 10))
           (alter acme-corp-share-price + 10))))
;; Lets buy 1 stocks at 22 per stock
;; => #future[{:status :pending, :val nil} 0x2f6cbf54] raising share price to  32
;; raising share price to  32
;; raising share price to  32

(defn buy-acme-corp-shares''' [n]
  (dosync
   (let [price @acme-corp-share-price]
     (println "Lets buy" n "stocks at" price "per stock")
     (Thread/sleep 1000)
     (alter acme-corp-share-price identity)
     (alter client-account #(- % price))
     (alter broker-account #(+ % price))
     (alter client-stocks update :acme-corp #(+ % n))
     (alter broker-stocks update :acme-corp #(- % n)))))

(do
  (reset-accounts)
  (future (buy-acme-corp-shares''' 1))
  (future (dosync
           (Thread/sleep 300)
           (println "raising share price to " (+ @acme-corp-share-price 10))
           (alter acme-corp-share-price + 10))))
;; Lets buy 1 stocks at 22 per stock
;; => #future[{:status :pending, :val nil} 0x358876b] raising share price to  32
;; Lets buy 1 stocks at 32 per stock
@client-account
;; => 2068
