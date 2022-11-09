(ns ch04-map-filter.core
  (:require [clojure.string :as string]))


(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def students [{:name "Eliza" :year 1994}
               {:name "Salma" :year 1995}
               {:name "Jodie" :year 1997}
               {:name "Catlyn" :year 2000}
               {:name "Alice" :year 2001}
               {:name "Pippa" :year 2002}
               {:name "Fleur" :year 2002}])

(take-while #(< (:year %) 2000) students)
;; => ({:name "Eliza", :year 1994} {:name "Salma", :year 1995} {:name "Jodie", :year 1997})

(drop-while #(< (:year %) 2000) students)
;; => ({:name "Catlyn", :year 2000} {:name "Alice", :year 2001} {:name "Pippa", :year 2002} {:name "Fleur", :year 2002})

(map (fn [n] (* 10 n))
     (filter odd? [1 2 3 4 5]));; => (10 30 50)

(def filtered (filter odd? [1 2 3 4 5]))
(map (fn [n] (* 10 n)) filtered)
;; => (10 30 50)

(->> [1 2 3 4 5]
     (filter odd?)
     (map (fn [n] (* 10 n))))
;; => (10 30 50)

;;; lazy seq
(def our-seq (range 100))

(first our-seq) ;; only calculate the first
;; => 0

(last our-seq) ; after this, all the elements are realized
;; => 99

;;; map, filter, remove are lazy. they dont force calculation of the entire seq. only when needed.

;; exercise 4.04
(defn our-range [limit]
  (take-while #(< % limit) (iterate inc 0)))

(our-range 5);; => (0 1 2 3 4)

(map #(* 10 %) (our-range 5))
;; => (0 10 20 30 40)

;; with side effects
(map (fn [i] (print ".") (* i 10)) (our-range 5))
;; => .(0. 10. 20. 30. 40)

(def by-ten (map (fn [i] (print ".") (* i 10)) (our-range 5)))

;; range computes from 0 to infinity
;; but because it is lazy, we can compute one by one
;; and take only first 5
(->> (range)
     (map #(* 10 %))
     (take 5))
;; => (0 10 20 30 40)

;; exercise 4.05
;; create a seq of random integers
(def our-randoms (repeatedly (partial rand-int 100)))

(take 20 our-randoms)
;; => (57 98 83 95 93 50 33 16 6 84 83 44 52 46 38 84 61 94 49 46)

(defn some-random-integers [size]
  (take size (repeatedly (fn [] (rand-int 100)))))

(some-random-integers 12)
;; => (21 29 40 37 80 43 93 71 92 52 3 52)

(def apart (partial * 4))

(apart 8)
;; => 32

;; exercise 4.06
(def game-users
  [{:id 9342
    :username "speedy"
    :current-points 45
    :remaining-lives 2
    :experience-level 5
    :status :active}
   {:id 9854
    :username "stealthy"
    :current-points 1201
    :remaining-lives 1
    :experience-level 8
    :status :speed-boost}
   {:id 3014
    :username "sneaky"
    :current-points 725
    :remaining-lives 7
    :experience-level 3
    :status :active}
   {:id 2051
    :username "forgetful"
    :current-points 89
    :remaining-lives 4
    :experience-level 5
    :status :imprisoned}
   {:id 1032
    :username "wandering"
    :current-points 2043
    :remaining-lives 12
    :experience-level 7
    :status :speed-boost}
   {:id 7213
    :username "slowish"
    :current-points 143
    :remaining-lives 0
    :experience-level 1
    :status :speed-boost}
   {:id 5633
    :username "smarter"
    :current-points 99
    :remaining-lives 4
    :experience-level 4
    :status :terminated}
   {:id 3954
    :username "crafty"
    :current-points 21
    :remaining-lives 2
    :experience-level 8
    :status :active}
   {:id 7213
    :username "smarty"
    :current-points 290
    :remaining-lives 5
    :experience-level 12
    :status :terminated}
   {:id 3002
    :username "clever"
    :current-points 681
    :remaining-lives 1
    :experience-level 8
    :status :active}])

;; get current points of all users
(map (fn [player] (:current-points player)) game-users)
;; => (45 1201 725 89 2043 143 99 21 290 681)

(map :current-points game-users)
;; => (45 1201 725 89 2043 143 99 21 290 681)

;; sets as predicate function
(def alpha-set (set [:a :b :c]))

(alpha-set :z)
;; => nil
(alpha-set :a)
;; => :a

(def animal-names ["turtle" "horse" "cat" "frog" "hawk" "worm"])

;; remove some animals in verbose
(remove (fn [animal-name]
          (or (= animal-name "horse")
              (= animal-name "cat")))
        animal-names)
;; => ("turtle" "frog" "hawk" "worm")

;; use sets as predicate
(remove #{"horse" "cat"} animal-names)
;; => ("turtle" "frog" "hawk" "worm")


;; filter with function composition
(defn normalize [s]
  (string/trim (string/lower-case s)))

(def normalizer (comp string/trim string/lower-case))

(normalize "   SomE InformAtion   ")
;; => "some information"
(normalizer "   SomE InformAtion ")
;; => "some information"

(def remove-words #{"and" "an" "a" "the" "of" "is"})

(remove (comp remove-words normalizer) ["FebruAry " " THe " "  4TH"])
;; => ("FebruAry " "  4TH")

;;; exercise 4.07
;; get score of user with specified status
(def keep-statuses #{:active :imprisoned :speed-boost})
(->> game-users
     (filter (comp keep-statuses :status))
     (map :current-points))
;; => (45 1201 725 89 2043 143 21 681)

;; return a list longer than the input with mapcat
(def alpha-lc ["a" "b" "c" "d" "e" "f" "g" "h" "i" "j"])

;; combine lowercase and uppercase
(mapcat (fn [letter] [letter (string/upper-case letter)]) alpha-lc)
;; => ("a" "A" "b" "B" "c" "C" "d" "D" "e" "E" "f" "F" "g" "G" "h" "H" "i" "I" "j" "J")

;; only with map
(map (fn [letter] [letter (string/upper-case letter)]) alpha-lc)
;; (["a" "A"]
;;  ["b" "B"]
;;  ["c" "C"]
;;  ["d" "D"]
;;  ["e" "E"]
;;  ["f" "F"]
;;  ["g" "G"]
;;  ["h" "H"]
;;  ["i" "I"]
;;  ["j" "J"])

;; map with multiple input sequences
(map (fn [a b] (+ a b)) [1 2 3 4 5] [6 7 8 9 0])
;; => (7 9 11 13 5)

(defn our-zipmap [xs ys]
  (->> (map (fn [x y] [x y]) xs ys)
       (into {})))

(our-zipmap [:a :b :c] [1 2 3])
;; => {:a 1, :b 2, :c 3}

(def meals ["breakfast" "lunch" "dinner" "midnight snack"])

(map (fn [idx meal] (str (inc idx) ". " meal)) (range) meals)
;; => ("1. breakfast" "2. lunch" "3. dinner" "4. midnight snack")

(map-indexed (fn [idx meal] (str (inc idx) ". " meal)) meals)
;; => ("1. breakfast" "2. lunch" "3. dinner" "4. midnight snack")

;; exercise 4.08
(def temperature-by-day
  [18 23 24 23 27 24 22 21 21 20 32 33 30 29 35 28 25 24 28 29 30])

;; compare todays' temperature with yesterday's
(map (fn [today yesterday]
       (cond
         (> today yesterday) :warmer
         (< today yesterday) :colder
         (= today yesterday) :same))
     (rest temperature-by-day) ; today
     temperature-by-day)
;; => (:warmer :warmer :colder :warmer :colder :colder :colder :same :colder :warmer :warmer :colder :colder :warmer :colder :colder :colder :warmer :warmer :warmer)

;; consuming extracted data with apply
(apply max [ 3 9 5])
;; => 9

(let [a 5
      b nil
      c 18]
  (apply + (filter integer? [a b c])))
;; => 23

;; exercise 4.09
;; compute average temperature
(let [total (apply + temperature-by-day)
      c (count temperature-by-day)]
  (/ total c))
;; => 26

;; activity 4.01
(defn max-value-by-status [field status users]
  (->> users
       (filter #(= status (:status %)))
       (map field)
       (apply max 0))

(defn min-value-by-status [field status users]
  (->> users
       (filter #(= status (:status %)))
       (map field)
       (apply min 0)))

(max-value-by-status :experience-level :imprisoned game-users)
;; => 5
(min-value-by-status :experience-level :imprisoned game-users)
;; => 0
(max-value-by-status :experience-level :terminated game-users)
;; => 12
(min-value-by-status :experience-level :terminated game-users)
;; => 0
(max-value-by-status :remaining-lives :active game-users)
;; => 7
(min-value-by-status :remaining-lives :active game-users)
;; => 0
(max-value-by-status :current-points :speed-boost game-users)
;; => 2043
