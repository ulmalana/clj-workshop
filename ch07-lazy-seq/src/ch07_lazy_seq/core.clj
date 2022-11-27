(ns ch07-lazy-seq.core
  (:require [clojure.math.numeric-tower :as math]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [semantic-csv.core :as sc]))

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

;; exercise 7.02
(def endless-potatoes (repeatedly (fn [] (+ 10 (rand-int 390)))))

(take 5 endless-potatoes)
;; => (302 278 11 341 180)
(take 10 endless-potatoes)
;; => (302 278 11 341 180 348 188 375 22 297)

(defn average-potatoes [prev arrivals]
  (lazy-seq
   (if-not arrivals
     '()
     (let [[_ n total] prev
           current [(first arrivals)
                    (inc (or n 0))
                    (+ (first arrivals) (or total 0))]]
       (cons
        current
        (average-potatoes current (next arrivals)))))))

(take 3 (average-potatoes '() endless-potatoes))
;; => ([302 1 302] [278 2 580] [11 3 591])
(last (take 500000 (average-potatoes '() endless-potatoes)))
;; => [353 500000 102208764]

;; lazy trees
;; exercise 7.03
(defn match-probability [p1-rating p2-rating]
  (/ 1
     (+ 1
        (math/expt 10 (/ (- p2-rating p1-rating) 400)))))

(def k-factor 32)

(defn recalculate-rating [prev-rating expected-result real-result]
  (+ prev-rating (* k-factor (- real-result expected-result))))

(defn elo-db [csv k]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         sc/mappify
         (sc/cast-with {:winner_sets_won sc/->int
                        :loser_sets_won sc/->int
                        :winner_games_won sc/->int
                        :loser_games_won sc/->int})
         (reduce (fn [{:keys [players] :as acc} {:keys [:winner_name :winner_slug :loser_name :loser_slug] :as match}]
                   (let [winner-rating (get players winner_slug 400)
                         loser-rating (get players loser_slug 400)
                         winner-prob (match-probability winner-rating loser-rating)
                         loser-prob (- 1 winner-prob)]
                     (-> acc
                         (assoc-in [:players winner_slug] (recalculate-rating winner-rating winner-prob 1))
                         (assoc-in [:players loser_slug] (recalculate-rating loser-rating loser-prob 0))
                         (update :matches (fn [ms]
                                            (conj ms (assoc match
                                                            :winner_rating winner-rating
                                                            :loser_rating loser-rating)))))))
                 {:players {}
                  :matches []})
         :matches
         reverse)))

(def ratings (elo-db "resources/match_scores_1991-2016_unindexed_csv.csv" 35))


(map #(select-keys % [:winner_rating :loser_rating]) (take 5 ratings))
;; ({:winner_rating 971.7454364144253, :loser_rating 914.5227794278815}
;;  {:winner_rating 1255.010476602315, :loser_rating 864.1082333776739}
;;  {:winner_rating 999.9809456512259, :loser_rating 956.9004001481379}
;;  {:winner_rating 1287.9493288642025, :loser_rating 981.5015812416054}
;;  {:winner_rating 844.0291961864665, :loser_rating 934.6018166190888})

(defn player-in-match? [{:keys [winner_slug loser_slug]} player-slug]
  ((hash-set winner_slug loser_slug) player-slug))

(player-in-match? (first ratings) "gael-monfils")
;; => "gael-monfils"
(player-in-match? (first ratings) "boris-becker")
;; => nil

(defn match-tree-by-player [m player-slug]
  (lazy-seq
   (cond
     (empty? m) '()
     (player-in-match? (first m) player-slug)
     (cons (first m)
           (cons
            [(match-tree-by-player (rest m) (:winner_slug (first m)))
             (match-tree-by-player (rest m) (:loser_slug (first m)))]
            '()))
     ::otherwise (match-tree-by-player (rest m) player-slug))))

(match-tree-by-player ratings "non-tennis-player")
;; => ()

(def federer (match-tree-by-player ratings "roger-federer"))
(type federer)
;; => clojure.lang.LazySeq

(:winner_rating (first federer))
;; => 1118.0209677983953

;; exercise 7.04
(defn take-matches [limit tree]
  (cond
    (zero? limit) '()
    (= 1 limit) (first tree)
    :otherwise-continue
    (cons
     (first tree)
     (cons
      [(take-matches (dec limit) (first (second tree)))
       (take-matches (dec limit) (second (second tree)))]
      '()))))

(select-keys (take-matches 1 federer) [:winner_slug :loser_slug])
;; => {:winner_slug "roger-federer", :loser_slug "guido-pella"}

(take-matches 2 federer)
;; => ({:tourney_slug "wimbledon", :loser_slug "guido-pella", :winner_sets_won 3, :match_score_tiebreaks "76(5) 76(3) 63", :loser_sets_won 0, :loser_games_won 15, :tourney_year_id "2016-540", :tourney_order "39", :winner_seed "3", :loser_seed "", :winner_slug "roger-federer", :match_order "3", :loser_name "Guido Pella", :winner_player_id "f324", :match_stats_url_suffix "/en/scores/2016/540/MS080/match-stats", :tourney_url_suffix "/en/scores/archive/wimbledon/540/2016/results", :loser_player_id "pc11", :loser_tiebreaks_won "0", :round_order "7", :winner_rating 1118.0209677983953, :tourney_round_name "Round of 128", :match_id "2016-540-f324-pc11", :winner_name "Roger Federer", :winner_games_won 20, :loser_rating 610.5000625008922, :winner_tiebreaks_won "2"} [{:tourney_slug "wimbledon", :loser_slug "marcus-willis", :winner_sets_won 3, :match_score_tiebreaks "60 63 64", :loser_sets_won 0, :loser_games_won 7, :tourney_year_id "2016-540", :tourney_order "39", :winner_seed "3", :loser_seed "Q", :winner_slug "roger-federer", :match_order "3", :loser_name "Marcus Willis", :winner_player_id "f324", :match_stats_url_suffix "/en/scores/2016/540/MS040/match-stats", :tourney_url_suffix "/en/scores/archive/wimbledon/540/2016/results", :loser_player_id "w521", :loser_tiebreaks_won "0", :round_order "6", :winner_rating 1117.5598912164435, :tourney_round_name "Round of 64", :match_id "2016-540-f324-w521", :winner_name "Roger Federer", :winner_games_won 18, :loser_rating 383.5303713763432, :winner_tiebreaks_won "0"} {:tourney_slug "eastbourne", :loser_slug "guido-pella", :winner_sets_won 2, :match_score_tiebreaks "36 61 63", :loser_sets_won 1, :loser_games_won 10, :tourney_year_id "2016-741", :tourney_order "38", :winner_seed "", :loser_seed "13", :winner_slug "benjamin-becker", :match_order "13", :loser_name "Guido Pella", :winner_player_id "b896", :match_stats_url_suffix "/en/scores/2016/741/MS021/match-stats", :tourney_url_suffix "/en/scores/archive/eastbourne/741/2016/results", :loser_player_id "pc11", :loser_tiebreaks_won "0", :round_order "5", :winner_rating 626.8198541196332, :tourney_round_name "Round of 32", :match_id "2016-741-b896-pc11", :winner_name "Benjamin Becker", :winner_games_won 15, :loser_rating 626.4846246165524, :winner_tiebreaks_won "0"}])

;;; exercise 7.05
(defn take-matches [limit tree f]
  (cond
    (zero? limit) '()
    (= 1 limit) (f (first tree))
    :otherwise-continue
    (cons (f (first tree))
          (cons
           [(take-matches (dec limit) (first (second tree)) f)
            (take-matches (dec limit) (second (second tree)) f)]
           '()))))

(take-matches 3 federer #(select-keys % [:winner_slug :loser_slug]))
;; => ({:winner_slug "roger-federer", :loser_slug "guido-pella"} [({:winner_slug "roger-federer", :loser_slug "marcus-willis"} [{:winner_slug "roger-federer", :loser_slug "daniel-evans"} {:winner_slug "pierre-hugues-herbert", :loser_slug "marcus-willis"}]) ({:winner_slug "benjamin-becker", :loser_slug "guido-pella"} [{:winner_slug "dudi-sela", :loser_slug "benjamin-becker"} {:winner_slug "guido-pella", :loser_slug "diego-schwartzman"}])])

(defn matches-with-ratings [limit tree]
  (take-matches limit
                tree
                (fn [match]
                  (-> match
                      (update :winner_rating int)
                      (update :loser_rating int)
                      (select-keys [:winner_name :loser_name
                                    :winner_rating :loser_rating])
                      (assoc :winner_probability_percentage
                             (->> (match-probability (:winner_rating match)
                                                     (:loser_rating match))
                                  (* 100)
                                  int))))))

(matches-with-ratings 3 federer)
;; ({:winner_name "Roger Federer",
;;   :loser_name "Guido Pella",
;;   :winner_rating 1118,
;;   :loser_rating 610,
;;   :winner_probability_percentage 94}
;;  [({:winner_name "Roger Federer",
;;     :loser_name "Marcus Willis",
;;     :winner_rating 1117,
;;     :loser_rating 383,
;;     :winner_probability_percentage 98}
;;    [{:winner_name "Roger Federer",
;;      :loser_name "Daniel Evans",
;;      :winner_rating 1116,
;;      :loser_rating 591,
;;      :winner_probability_percentage 95}
;;     {:winner_name "Pierre-Hugues Herbert",
;;      :loser_name "Marcus Willis",
;;      :winner_rating 575,
;;      :loser_rating 391,
;;      :winner_probability_percentage 74}])
;;   ({:winner_name "Benjamin Becker",
;;     :loser_name "Guido Pella",
;;     :winner_rating 626,
;;     :loser_rating 626,
;;     :winner_probability_percentage 50}
;;    [{:winner_name "Dudi Sela",
;;      :loser_name "Benjamin Becker",
;;      :winner_rating 552,
;;      :loser_rating 647,
;;      :winner_probability_percentage 36}
;;     {:winner_name "Guido Pella",
;;      :loser_name "Diego Schwartzman",
;;      :winner_rating 608,
;;      :loser_rating 645,
;;      :winner_probability_percentage 44}])])

;; activity 7.0
(defn focus-history [tree player-slug focus-depth opponent-depth f]
  (cond
    (zero? focus-depth) '()
    (= 1 focus-depth) (f (first tree))
    :otherwise
    (cons (f (first tree))
          (cons
           [(if (player-in-match? (ffirst (second tree)) player-slug)
              (focus-history (first (second tree)) player-slug (dec focus-depth) opponent-depth f)
              (take-matches opponent-depth (first (second tree)) f))
            (if (player-in-match? (first (second (second tree))) player-slug)
              (focus-history (second (second tree)) player-slug (dec focus-depth) opponent-depth f)
              (take-matches opponent-depth (second (second tree)) f))]
           '()))))

(focus-history federer "roger-federer" 4 2 #(select-keys % [:winner_name :loser_name :winner_rating :loser_rating]))
;; ({:winner_name "Roger Federer",
;;   :loser_name "Guido Pella",
;;   :winner_rating 1118.0209677983953,
;;   :loser_rating 610.5000625008922}
;;  [({:winner_name "Roger Federer",
;;     :loser_name "Marcus Willis",
;;     :winner_rating 1117.5598912164435,
;;     :loser_rating 383.5303713763432}
;;    [({:winner_name "Roger Federer",
;;       :loser_name "Daniel Evans",
;;       :winner_rating 1116.0670318316115,
;;       :loser_rating 591.9139913612546}
;;      [{:winner_name "Roger Federer",
;;        :loser_name "Steve Johnson",
;;        :winner_rating 1112.225690610438,
;;        :loser_rating 766.1740592228002}
;;       ({:winner_name "Daniel Evans",
;;         :loser_name "Liam Broady",
;;         :winner_rating 584.9092331046751,
;;         :loser_rating 363.92355725864604}
;;        [{:winner_name "Daniel Evans",
;;          :loser_name "Ricardas Berankis",
;;          :winner_rating 569.5605752488634,
;;          :loser_rating 555.4090394677708}
;;         {:winner_name "Inigo Cervantes",
;;          :loser_name "Liam Broady",
;;          :winner_rating 464.4729215208981,
;;          :loser_rating 375.9321377471188}])])
;;     ({:winner_name "Pierre-Hugues Herbert",
;;       :loser_name "Marcus Willis",
;;       :winner_rating 575.2346889957473,
;;       :loser_rating 391.78876077846934}
;;      [{:winner_name "Pierre-Hugues Herbert",
;;        :loser_name "Daniil Medvedev",
;;        :winner_rating 566.791272201825,
;;        :loser_rating 388.55364422602725}
;;       {:winner_name "Daniel Kosakowski",
;;        :loser_name "Marcus Willis",
;;        :winner_rating 465.7770030621933,
;;        :loser_rating 405.01892580000225}])])
;;   ({:winner_name "Benjamin Becker",
;;     :loser_name "Guido Pella",
;;     :winner_rating 626.8198541196332,
;;     :loser_rating 626.4846246165524}
;;    [{:winner_name "Dudi Sela",
;;      :loser_name "Benjamin Becker",
;;      :winner_rating 552.9885976684109,
;;      :loser_rating 647.0486677104107}
;;     {:winner_name "Guido Pella",
;;      :loser_name "Diego Schwartzman",
;;      :winner_rating 608.8025240246648,
;;      :loser_rating 645.4643440212398}])])
