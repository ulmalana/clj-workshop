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
