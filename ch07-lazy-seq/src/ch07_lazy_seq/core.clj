(ns ch07-lazy-seq.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

;; simple lazy seq
(defn iterate-range []
  (iterate inc 0))

(take 5 (iterate-range))
;; => (0 1 2 3 4)

(defn our-range [n]
  (lazy-seq
   (cons n (our-range (inc n)))))

(take 5 (our-range 0))
;; => (0 1 2 3 4)

;; exercise 7.01
(def sample-data
  [[24.2 420031]
   [25.8 492657]
   [25.9 589014]
   [23.8 691995]
   [24.7 734902]
   [23.2 794243]
   [23.1 836204]
   [23.5 884120]])

(defn local-max? [[a b c]]
  (and (< (first a) (first b)) (< (first c) (first b))))

(defn local-min? [[a b c]]
  (and (> (first a) (first b)) (> (first c) (first b))))

(defn local-max?' [[[a _] [b _] [c _]]]
  (and (< a b) (< c b)))

(defn local-min?' [[[a _] [b _] [c _]]]
  (and (> a b) (> c b)))

(local-max? (take 3 sample-data))
;; => false
(local-min? (take 3 sample-data))
;; => false
(local-min? (take 3 (drop 2 sample-data)))
;; => true

(defn inflection-points [data]
  (lazy-seq
   (let [current-series (take 3 data)]
     (cond
       (< (count current-series) 3) '()
       (local-max? current-series)
       (cons
        (conj (second current-series) :peak)
        (inflection-points (rest data)))
       (local-min? current-series)
       (cons
        (conj (second current-series) :valley)
        (inflection-points (rest data)))
       :otherwise (inflection-points (rest data))))))

(inflection-points sample-data)
;; => ([25.9 589014 :peak] [23.8 691995 :valley] [24.7 734902 :peak] [23.1 836204 :valley])

(take 15 (inflection-points (cycle sample-data)))
;; ([25.9 589014 :peak]
;;  [23.8 691995 :valley]
;;  [24.7 734902 :peak]
;;  [23.1 836204 :valley]
;;  [25.9 589014 :peak]
;;  [23.8 691995 :valley]
;;  [24.7 734902 :peak]
;;  [23.1 836204 :valley]
;;  [25.9 589014 :peak]
;;  [23.8 691995 :valley]
;;  [24.7 734902 :peak]
;;  [23.1 836204 :valley]
;;  [25.9 589014 :peak]
;;  [23.8 691995 :valley]
;;  [24.7 734902 :peak])
