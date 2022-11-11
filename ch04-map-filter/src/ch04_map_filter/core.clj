(ns ch04-map-filter.core
  (:require [clojure.string :as string]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [semantic-csv.core :as sc]))


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
       (apply max 0)))

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

;;; importing dataset from csv
;; exercise 4.10
(with-open [r (io/reader "resources/match_scores_1991-2016_unindexed_csv.csv")]
  (first (csv/read-csv r)))
;; => ["tourney_year_id" "tourney_order" "tourney_slug" "tourney_url_suffix" "tourney_round_name" "round_order" "match_order" "winner_name" "winner_player_id" "winner_slug" "loser_name" "loser_player_id" "loser_slug" "winner_seed" "loser_seed" "match_score_tiebreaks" "winner_sets_won" "loser_sets_won" "winner_games_won" "loser_games_won" "winner_tiebreaks_won" "loser_tiebreaks_won" "match_id" "match_stats_url_suffix"]

(with-open [r (io/reader "resources/match_scores_1991-2016_unindexed_csv.csv")]
  (count (csv/read-csv r)))
;; => 95360


;; forcing evaluation with doall
(with-open [r (io/reader "resources/match_scores_1991-2016_unindexed_csv.csv")]
  (->> (csv/read-csv r)
       (map #(nth % 7))
       (take 6)
       doall))
;; ("winner_name"
;;  "Nicklas Kulti"
;;  "Michael Stich"
;;  "Nicklas Kulti"
;;  "Jim Courier"
;;  "Michael Stich")

;;; exercise 4.12
(defn first-match [csv]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         sc/mappify
         first)))

(first-match "resources/match_scores_1991-2016_unindexed_csv.csv")
;; => {:tourney_slug "adelaide", :loser_slug "michael-stich", :winner_sets_won "2", :match_score_tiebreaks "63 16 62", :loser_sets_won "1", :loser_games_won "11", :tourney_year_id "1991-7308", :tourney_order "1", :winner_seed "", :loser_seed "6", :winner_slug "nicklas-kulti", :match_order "1", :loser_name "Michael Stich", :winner_player_id "k181", :match_stats_url_suffix "/en/scores/1991/7308/MS001/match-stats", :tourney_url_suffix "/en/scores/archive/adelaide/7308/1991/results", :loser_player_id "s351", :loser_tiebreaks_won "0", :round_order "1", :tourney_round_name "Finals", :match_id "1991-7308-k181-s351", :winner_name "Nicklas Kulti", :winner_games_won "13", :winner_tiebreaks_won "0"}

;; get only specific fields
(defn five-matches [csv]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         sc/mappify
         (map #(select-keys % [:tourney_year_id
                               :winner_name
                               :loser_name
                               :winner_sets_won
                               :loser_sets_won]))
         (take 5)
         doall)))
;; => #'ch04-map-filter.core/five-matches

(five-matches "resources/match_scores_1991-2016_unindexed_csv.csv")
;; => ({:tourney_year_id "1991-7308", :winner_name "Nicklas Kulti", :loser_name "Michael Stich", :winner_sets_won "2", :loser_sets_won "1"} {:tourney_year_id "1991-7308", :winner_name "Michael Stich", :loser_name "Jim Courier", :winner_sets_won "2", :loser_sets_won "0"} {:tourney_year_id "1991-7308", :winner_name "Nicklas Kulti", :loser_name "Magnus Larsson", :winner_sets_won "2", :loser_sets_won "0"} {:tourney_year_id "1991-7308", :winner_name "Jim Courier", :loser_name "Martin Sinner", :winner_sets_won "2", :loser_sets_won "0"} {:tourney_year_id "1991-7308", :winner_name "Michael Stich", :loser_name "Jimmy Arias", :winner_sets_won "2", :loser_sets_won "1"})

;; cast some value from string to integer
(defn five-matches-int-sets [csv]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         sc/mappify
         (map #(select-keys % [:tourney_year_id
                               :winner_name
                               :loser_name
                               :winner_sets_won
                               :loser_sets_won]))
         (sc/cast-with {:winner_sets_won sc/->int
                        :loser_sets_won sc/->int})
         (take 5)
         doall)))
;; => #'ch04-map-filter.core/five-matches-int-sets

(five-matches-int-sets "resources/match_scores_1991-2016_unindexed_csv.csv")
;; => ({:tourney_year_id "1991-7308", :winner_name "Nicklas Kulti", :loser_name "Michael Stich", :winner_sets_won 2, :loser_sets_won 1} {:tourney_year_id "1991-7308", :winner_name "Michael Stich", :loser_name "Jim Courier", :winner_sets_won 2, :loser_sets_won 0} {:tourney_year_id "1991-7308", :winner_name "Nicklas Kulti", :loser_name "Magnus Larsson", :winner_sets_won 2, :loser_sets_won 0} {:tourney_year_id "1991-7308", :winner_name "Jim Courier", :loser_name "Martin Sinner", :winner_sets_won 2, :loser_sets_won 0} {:tourney_year_id "1991-7308", :winner_name "Michael Stich", :loser_name "Jimmy Arias", :winner_sets_won 2, :loser_sets_won 1})


;; exercise 4.13
(defn federer-wins [csv]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         sc/mappify
         (filter #(= "Roger Federer" (:winner_name %)))
         (map #(select-keys % [:winner_name
                               :loser_name
                               :winner_sets_won
                               :loser_sets_won
                               :winner_games_won
                               :loser_games_won
                               :tourney_year_id
                               :tourney_slug]))
         doall)))

(take 3 (federer-wins "resources/match_scores_1991-2016_unindexed_csv.csv"))
;; => ({:winner_name "Roger Federer", :loser_name "Richard Fromberg", :winner_sets_won "2", :loser_sets_won "0", :winner_games_won "13", :loser_games_won "7", :tourney_year_id "1998-327", :tourney_slug "toulouse"} {:winner_name "Roger Federer", :loser_name "Guillaume Raoux", :winner_sets_won "2", :loser_sets_won "0", :winner_games_won "12", :loser_games_won "4", :tourney_year_id "1998-327", :tourney_slug "toulouse"} {:winner_name "Roger Federer", :loser_name "Jerome Golmard", :winner_sets_won "2", :loser_sets_won "1", :winner_games_won "20", :loser_games_won "19", :tourney_year_id "1999-496", :tourney_slug "marseille"})
;; => 

;; exercise 4.14
(defn match-query [csv pred]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         sc/mappify
         (filter pred)
         (map #(select-keys % [:winner_name
                               :loser_name
                               :winner_sets_won
                               :loser_sets_won
                               :winner_games_won
                               :loser_games_won
                               :tourney_year_id
                               :tourney_slug]))
         doall)))

(count (match-query "resources/match_scores_1991-2016_unindexed_csv.csv"
                    #((hash-set (:winner_name %) (:loser_name %)) "Roger Federer")))
;; => 1290

(count (match-query "resources/match_scores_1991-2016_unindexed_csv.csv"
                    #(= (:winner_name %) "Roger Federer")))
;; => 1050

;; exercise 4.15
(take 3 (match-query "resources/match_scores_1991-2016_unindexed_csv.csv"
                     #(= (hash-set (:winner_name %) (:loser_name %))
                         #{"Roger Federer" "Rafael Nadal"})))
;; => ({:winner_name "Rafael Nadal", :loser_name "Roger Federer", :winner_sets_won "2", :loser_sets_won "0", :winner_games_won "12", :loser_games_won "6", :tourney_year_id "2004-403", :tourney_slug "miami"} {:winner_name "Roger Federer", :loser_name "Rafael Nadal", :winner_sets_won "3", :loser_sets_won "2", :winner_games_won "27", :loser_games_won "23", :tourney_year_id "2005-403", :tourney_slug "miami"} {:winner_name "Rafael Nadal", :loser_name "Roger Federer", :winner_sets_won "3", :loser_sets_won "1", :winner_games_won "22", :loser_games_won "16", :tourney_year_id "2005-520", :tourney_slug "roland-garros"})

(defn close-match-query [csv pred]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         sc/mappify
         (sc/cast-with {:winner_sets_won sc/->int
                        :loser_sets_won sc/->int
                        :winner_games_won sc/->int
                        :loser_games_won sc/->int})
         (filter pred)
         (map #(select-keys % [:winner_name
                               :loser_name
                               :winner_sets_won
                               :loser_sets_won
                               :winner_games_won
                               :loser_games_won
                               :tourney_year_id
                               :tourney_slug]))
         doall)))
;; => #'ch04-map-filter.core/close-match-query

(take 3 (close-match-query "resources/match_scores_1991-2016_unindexed_csv.csv"
                           #(and (= (hash-set (:winner_name %) (:loser_name %))
                                    #{"Roger Federer" "Rafael Nadal"})
                                 (= 1 (- (:winner_sets_won %) (:loser_sets_won %))))))
;; => ({:winner_name "Roger Federer", :loser_name "Rafael Nadal", :winner_sets_won 3, :loser_sets_won 2, :winner_games_won 27, :loser_games_won 23, :tourney_year_id "2005-403", :tourney_slug "miami"} {:winner_name "Rafael Nadal", :loser_name "Roger Federer", :winner_sets_won 2, :loser_sets_won 1, :winner_games_won 14, :loser_games_won 14, :tourney_year_id "2006-495", :tourney_slug "dubai"} {:winner_name "Rafael Nadal", :loser_name "Roger Federer", :winner_sets_won 3, :loser_sets_won 2, :winner_games_won 28, :loser_games_won 29, :tourney_year_id "2006-416", :tourney_slug "rome"})

;;; activity
(defn rivalry-data [csv p1 p2]
  (with-open [r (io/reader csv)]
    (let [rivalry-seq (->>(csv/read-csv r)
                          sc/mappify
                          (sc/cast-with {:winner_sets_won sc/->int
                                         :loser_sets_won sc/->int
                                         :winner_games_won sc/->int
                                         :loser_games_won sc/->int})
                          (filter #(= (hash-set (:winner_name %) (:loser_name %))
                                      #{p1 p2}))
                          (map #(select-keys % [:winner_name
                                                :loser_name
                                                :winner_sets_won
                                                :loser_sets_won
                                                :winner_games_won
                                                :loser_games_won
                                                :tourney_year_id
                                                :tourney_slug])))
          p1-wins (filter #(= (:winner_name %) p1) rivalry-seq)
          p2-wins (filter #(= (:winner_name %) p2) rivalry-seq)]
      {:first-victory-player-1 (first p1-wins)
       :first-victory-player-2 (first p2-wins)
       :total-matches (count rivalry-seq)
       :total-victory-player-1 (count p1-wins)
       :total-victory-player-2 (count p2-wins)
       :most-competitive-matches (->> rivalry-seq
                                      (filter #(= 1 (- (:winner_sets_won %) (:loser_sets_won %)))))})))


(rivalry-data "resources/match_scores_1968-1990_unindexed_csv.csv"
              "Boris Becker"
              "Jimmy Connors")
;; => {:first-victory-player-1 {:winner_name "Boris Becker", :loser_name "Jimmy Connors", :winner_sets_won 2, :loser_sets_won 1, :winner_games_won 17, :loser_games_won 16, :tourney_year_id "1986-411", :tourney_slug "chicago"}, :first-victory-player-2 nil, :total-matches 5, :total-victory-player-1 5, :total-victory-player-2 0, :most-competitive-matches ({:winner_name "Boris Becker", :loser_name "Jimmy Connors", :winner_sets_won 2, :loser_sets_won 1, :winner_games_won 17, :loser_games_won 16, :tourney_year_id "1986-411", :tourney_slug "chicago"} {:winner_name "Boris Becker", :loser_name "Jimmy Connors", :winner_sets_won 2, :loser_sets_won 1, :winner_games_won 15, :loser_games_won 15, :tourney_year_id "1986-428", :tourney_slug "bolton"} {:winner_name "Boris Becker", :loser_name "Jimmy Connors", :winner_sets_won 2, :loser_sets_won 1, :winner_games_won 18, :loser_games_won 14, :tourney_year_id "1987-311", :tourney_slug "london"} {:winner_name "Boris Becker", :loser_name "Jimmy Connors", :winner_sets_won 2, :loser_sets_won 1, :winner_games_won 15, :loser_games_won 14, :tourney_year_id "1987-605", :tourney_slug "nitto-atp-finals"})}
;; => 
