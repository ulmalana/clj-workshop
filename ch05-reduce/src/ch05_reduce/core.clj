(ns ch05-reduce.core
  (:require [ch05-reduce.serena :as serena]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(def weather-days
  [{:max 31
    :min 27
    :description :sunny
    :date "2019-09-24"}
   {:max 28
    :min 25
    :description :cloudy
    :date "2019-09-25"}
   {:max 22
    :min 18
    :description :rainy
    :date "2019-09-26"}
   {:max 23
    :min 16
    :description :stormy
    :date "2019-09-27"}
   {:max 35
    :min 19
    :description :sunny
    :date "2019-09-28"}])

;; get max weather
(apply max (map :max weather-days))
;; => 35

(reduce (fn [max-so-far this-day]
          (if (> (:max this-day) (:max max-so-far))
            this-day
            max-so-far))
        weather-days)
;; => {:max 35, :min 19, :description :sunny, :date "2019-09-28"}

;; get day with lowest temp
(reduce (fn [min-so-far this-day]
          (if (< (:max this-day) (:max min-so-far))
            this-day
            min-so-far))
        weather-days)
;; => {:max 22, :min 18, :description :rainy, :date "2019-09-26"}

;; get min and max from seqs simultaneously
(reduce (fn [{:keys [minimum maximum]} new-number]
          {:minimum (if (and minimum (> new-number minimum))
                      minimum
                      new-number)
           :maximum (if (and maximum (< new-number maximum))
                      maximum
                      new-number)})
        {}
        [5 23 5004 845 22])
;; => {:minimum 5, :maximum 5004}

;;; partition with reduce
(partition 3 [1 2 3 4 5 6 7 8 9 10])
;; => ((1 2 3) (4 5 6) (7 8 9))

(partition-all 3 [1 2 3 4 5 6 7 8 9 10])
;; => ((1 2 3) (4 5 6) (7 8 9) (10))

(partition-by #(> % 10) [5 33 18 0 23 2 9 4 3 99])
;; => ((5) (33 18) (0) (23) (2 9 4 3) (99))

;; partition seq to a group with sum <= 20
(reduce (fn [{:keys [segments current] :as accum} n]
          (let [current-with-n (conj current n)
                total-with-n (apply + current-with-n)]
            (if (> total-with-n 20)
              (assoc accum
                     :segments (conj segments current)
                     :current [n])
              (assoc accum :current current-with-n))))
        {:segments [] :current []}
        [4 19 4 9 5 12 5 3 4 1 1 9 5 18])
;; => {:segments [[4] [19] [4 9 5] [12 5 3] [4 1 1 9 5]], :current [18]}

(defn segment-by-sum [limit ns]
  (let [result (reduce (fn [{:keys [segments current] :as accum} n]
                         (let [current-with-n (conj current n)
                               total-with-n (apply + current-with-n)]
                           (if (> total-with-n limit)
                             (assoc accum
                                    :segments (conj segments current)
                                    :current [n])
                             (assoc accum :current current-with-n))))
                       {:segments [] :current []}
                       ns)]
    (conj (:segments result) (:current result))))

(segment-by-sum 20 [4 19 4 9 5 12 5 3 4 1 1 9 5 18])
;; => [[4] [19] [4 9 5] [12 5 3] [4 1 1 9 5] [18]]

(def numbers [4 9 2 3 7 9 5 2 6 1 4 6 2 3 3 6 1])

(defn parity-totals [ns]
  (:ret
   (reduce (fn [{:keys [current] :as acc} n]
             (if (and (seq current)
                      (or (and (odd? (last current)) (odd? n))
                          (and (even? (last current)) (even? n))))
               (-> acc
                   (update :ret conj [n (apply + current)])
                   (update :current conj n))
               (-> acc
                   (update :ret conj [n 0])
                   (assoc :current [n]))))
           {:current [] :ret []}
           ns)))

(parity-totals numbers)
;; => [[4 0] [9 0] [2 0] [3 0] [7 3] [9 10] [5 19] [2 0] [6 2] [1 0] [4 0] [6 4] [2 10] [3 0] [3 3] [6 0] [1 0]]

;; exercise 5.02
(def distance-elevation
  [[0 400]
   [12.5 457]
   [19 622]
   [21.5 592]
   [29 615]
   [35.5 892]
   [39 1083]
   [43 1477]
   [48.5 1151]
   [52.5 999]
   [57.5 800]
   [62.5 730]
   [65 1045]
   [68.5 1390]
   [70.5 1433]
   [75 1211]
   [78.5 917]
   [82.5 744]
   [84 667]
   [88.5 860]
   [96 671]
   [99 584]
   [108 402]
   [115.5 473]])

(defn distances-elevation-to-next-peak-or-valley
  [data]
  (-> (reduce
       (fn [{:keys [current] :as acc} [distance elevation :as this-position]]
         (cond (empty? current)
               {:current [this-position]
                :calculated [{:race-position distance
                              :elevation elevation
                              :distance-to-next 0
                              :elevation-to-next 0}]}
               (same-slope-as-current? current elevation)
               (-> acc
                   (update :current conj this-position)
                   (update :calculated conj {:race-position distance
                                             :elevation elevation
                                             :distance-to-next (- (first (first current)) distance)
                                             :elevation-to-next (- (second (first current)) elevation)}))
               :otherwise-slope-change
               (let [[prev-distance prev-elevation :as peak-or-valley] (last current)]
                 (-> acc
                     (assoc :current [peak-or-valley this-position])
                     (update :calculated conj {:race-position distance
                                               :elevation elevation
                                               :distance-to-next (- prev-distance distance)
                                               :elevation-to-next (- prev-elevation elevation)})))))
       {:current [] :calculated []}
       (reverse data))
      :calculated
      reverse))

(defn same-slope-as-current? [current elevation]
  (or (= 1 (count current))
      (let [[[_ next-to-last] [_ the-last]] (take-last 2 current)]
        (or (>= next-to-last the-last elevation)
            (<= next-to-last the-last elevation)))))

(same-slope-as-current? [[1 5] [2 10]] 15)
;; => true
(same-slope-as-current? [[1 5] [2 10]] 5)
;; => false
(same-slope-as-current? [[1 5] [2 10]] 10)
;; => true
(same-slope-as-current? [[1 5]] 10)
;; => true
(same-slope-as-current? [[1 5] [2 10] [3 15]] 20)
;; => true

(distances-elevation-to-next-peak-or-valley distance-elevation)
;; => ({:race-position 0, :elevation 400, :distance-to-next 19, :elevation-to-next 222} {:race-position 12.5, :elevation 457, :distance-to-next 6.5, :elevation-to-next 165} {:race-position 19, :elevation 622, :distance-to-next 2.5, :elevation-to-next -30} {:race-position 21.5, :elevation 592, :distance-to-next 21.5, :elevation-to-next 885} {:race-position 29, :elevation 615, :distance-to-next 14, :elevation-to-next 862} {:race-position 35.5, :elevation 892, :distance-to-next 7.5, :elevation-to-next 585} {:race-position 39, :elevation 1083, :distance-to-next 4, :elevation-to-next 394} {:race-position 43, :elevation 1477, :distance-to-next 19.5, :elevation-to-next -747} {:race-position 48.5, :elevation 1151, :distance-to-next 14.0, :elevation-to-next -421} {:race-position 52.5, :elevation 999, :distance-to-next 10.0, :elevation-to-next -269} {:race-position 57.5, :elevation 800, :distance-to-next 5.0, :elevation-to-next -70} {:race-position 62.5, :elevation 730, :distance-to-next 8.0, :elevation-to-next 703} {:race-position 65, :elevation 1045, :distance-to-next 5.5, :elevation-to-next 388} {:race-position 68.5, :elevation 1390, :distance-to-next 2.0, :elevation-to-next 43} {:race-position 70.5, :elevation 1433, :distance-to-next 13.5, :elevation-to-next -766} {:race-position 75, :elevation 1211, :distance-to-next 9, :elevation-to-next -544} {:race-position 78.5, :elevation 917, :distance-to-next 5.5, :elevation-to-next -250} {:race-position 82.5, :elevation 744, :distance-to-next 1.5, :elevation-to-next -77} {:race-position 84, :elevation 667, :distance-to-next 4.5, :elevation-to-next 193} {:race-position 88.5, :elevation 860, :distance-to-next 19.5, :elevation-to-next -458} {:race-position 96, :elevation 671, :distance-to-next 12, :elevation-to-next -269} {:race-position 99, :elevation 584, :distance-to-next 9, :elevation-to-next -182} {:race-position 108, :elevation 402, :distance-to-next 7.5, :elevation-to-next 71} {:race-position 115.5, :elevation 473, :distance-to-next 0, :elevation-to-next 0})

;; exercise 5.03
(defn streak-string [current-wins current-losses]
  (cond (pos? current-wins) (str "Won " current-wins)
        (pos? current-losses) (str "Lost " current-losses)
        :otherwise "First match of the year"))

(defn serena-williams-win-loss-streaks [matches]
  (:matches
   (reduce (fn [{:keys [current-wins current-losses] :as acc} match]
             (let [this-match (assoc match :current-streak
                                     (streak-string current-wins current-losses))
                   serena-victory? (= (:winner-name match) "Williams S.")]
               (-> acc
                   (update :matches #(conj % this-match))
                   (assoc :current-wins (if serena-victory?
                                          (inc current-wins)
                                          0))
                   (assoc :current-losses (if serena-victory?
                                            0
                                            (inc current-losses))))))
           {:matches []
            :current-wins 0
            :current-losses 0}
           matches)))

(serena-williams-win-loss-streaks serena/serena-williams-2015)
;; => [{:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Van Uytvanck A.", :tournament "Australian Open", :location "Melbourne", :date "2015-01-20", :current-streak "First match of the year"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Zvonareva V.", :tournament "Australian Open", :location "Melbourne", :date "2015-01-22", :current-streak "Won 1"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Svitolina E.", :tournament "Australian Open", :location "Melbourne", :date "2015-01-24", :current-streak "Won 2"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Muguruza G.", :tournament "Australian Open", :location "Melbourne", :date "2015-01-26", :current-streak "Won 3"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Cibulkova D.", :tournament "Australian Open", :location "Melbourne", :date "2015-01-28", :current-streak "Won 4"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Keys M.", :tournament "Australian Open", :location "Melbourne", :date "2015-01-29", :current-streak "Won 5"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Sharapova M.", :tournament "Australian Open", :location "Melbourne", :date "2015-01-31", :current-streak "Won 6"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Niculescu M.", :tournament "BNP Paribas Open", :location "Indian Wells", :date "2015-03-14", :current-streak "Won 7"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Diyas Z.", :tournament "BNP Paribas Open", :location "Indian Wells", :date "2015-03-15", :current-streak "Won 8"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Stephens S.", :tournament "BNP Paribas Open", :location "Indian Wells", :date "2015-03-17", :current-streak "Won 9"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Bacsinszky T.", :tournament "BNP Paribas Open", :location "Indian Wells", :date "2015-03-19", :current-streak "Won 10"} {:loser-sets-won nil, :winner-sets-won nil, :winner-name "Halep S.", :loser-name "Williams S.", :tournament "BNP Paribas Open", :location "Indian Wells", :date "2015-03-21", :current-streak "Won 11"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Niculescu M.", :tournament "Sony Ericsson Open", :location "Miami", :date "2015-03-28", :current-streak "Lost 1"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Bellis C.", :tournament "Sony Ericsson Open", :location "Miami", :date "2015-03-29", :current-streak "Won 1"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Kuznetsova S.", :tournament "Sony Ericsson Open", :location "Miami", :date "2015-03-30", :current-streak "Won 2"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Lisicki S.", :tournament "Sony Ericsson Open", :location "Miami", :date "2015-04-01", :current-streak "Won 3"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Halep S.", :tournament "Sony Ericsson Open", :location "Miami", :date "2015-04-03", :current-streak "Won 4"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Suarez Navarro C.", :tournament "Sony Ericsson Open", :location "Miami", :date "2015-04-04", :current-streak "Won 5"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Brengle M.", :tournament "Mutua Madrid Open", :location "Madrid", :date "2015-05-03", :current-streak "Won 6"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Stephens S.", :tournament "Mutua Madrid Open", :location "Madrid", :date "2015-05-04", :current-streak "Won 7"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Azarenka V.", :tournament "Mutua Madrid Open", :location "Madrid", :date "2015-05-06", :current-streak "Won 8"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Suarez Navarro C.", :tournament "Mutua Madrid Open", :location "Madrid", :date "2015-05-07", :current-streak "Won 9"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Kvitova P.", :loser-name "Williams S.", :tournament "Mutua Madrid Open", :location "Madrid", :date "2015-05-08", :current-streak "Won 10"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Pavlyuchenkova A.", :tournament "Internazionali BNL d'Italia", :location "Rome", :date "2015-05-12", :current-streak "Lost 1"} {:loser-sets-won nil, :winner-sets-won nil, :winner-name "Mchale C.", :loser-name "Williams S.", :tournament "Internazionali BNL d'Italia", :location "Rome", :date "2015-05-14", :current-streak "Won 1"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Hlavackova A.", :tournament "French Open", :location "Paris", :date "2015-05-26", :current-streak "Lost 1"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Friedsam A.L.", :tournament "French Open", :location "Paris", :date "2015-05-28", :current-streak "Won 1"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Azarenka V.", :tournament "French Open", :location "Paris", :date "2015-05-30", :current-streak "Won 2"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Stephens S.", :tournament "French Open", :location "Paris", :date "2015-06-01", :current-streak "Won 3"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Errani S.", :tournament "French Open", :location "Paris", :date "2015-06-03", :current-streak "Won 4"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Bacsinszky T.", :tournament "French Open", :location "Paris", :date "2015-06-04", :current-streak "Won 5"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Safarova L.", :tournament "French Open", :location "Paris", :date "2015-06-06", :current-streak "Won 6"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Gasparyan M.", :tournament "Wimbledon", :location "London", :date "2015-06-29", :current-streak "Won 7"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Babos T.", :tournament "Wimbledon", :location "London", :date "2015-07-01", :current-streak "Won 8"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Watson H.", :tournament "Wimbledon", :location "London", :date "2015-07-03", :current-streak "Won 9"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Williams V.", :tournament "Wimbledon", :location "London", :date "2015-07-06", :current-streak "Won 10"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Azarenka V.", :tournament "Wimbledon", :location "London", :date "2015-07-07", :current-streak "Won 11"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Sharapova M.", :tournament "Wimbledon", :location "London", :date "2015-07-09", :current-streak "Won 12"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Muguruza G.", :tournament "Wimbledon", :location "London", :date "2015-07-11", :current-streak "Won 13"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Bonaventure Y.", :tournament "Collector Swedish Open", :location "Bastad", :date "2015-07-15", :current-streak "Won 14"} {:loser-sets-won nil, :winner-sets-won nil, :winner-name "Koukalova K.", :loser-name "Williams S.", :tournament "Collector Swedish Open", :location "Bastad", :date "2015-07-16", :current-streak "Won 15"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Pennetta F.", :tournament "Rogers Cup", :location "Toronto", :date "2015-08-11", :current-streak "Lost 1"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Petkovic A.", :tournament "Rogers Cup", :location "Toronto", :date "2015-08-14", :current-streak "Won 1"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Vinci R.", :tournament "Rogers Cup", :location "Toronto", :date "2015-08-15", :current-streak "Won 2"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Bencic B.", :loser-name "Williams S.", :tournament "Rogers Cup", :location "Toronto", :date "2015-08-15", :current-streak "Won 3"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Pironkova T.", :tournament "Western & Southern Financial Group Women's Open", :location "Cincinnati", :date "2015-08-19", :current-streak "Lost 1"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Knapp K.", :tournament "Western & Southern Financial Group Women's Open", :location "Cincinnati", :date "2015-08-20", :current-streak "Won 1"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Ivanovic A.", :tournament "Western & Southern Financial Group Women's Open", :location "Cincinnati", :date "2015-08-21", :current-streak "Won 2"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Svitolina E.", :tournament "Western & Southern Financial Group Women's Open", :location "Cincinnati", :date "2015-08-23", :current-streak "Won 3"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Halep S.", :tournament "Western & Southern Financial Group Women's Open", :location "Cincinnati", :date "2015-08-23", :current-streak "Won 4"} {:loser-sets-won 0, :winner-sets-won 1, :winner-name "Williams S.", :loser-name "Diatchenko V.", :tournament "US Open", :location "New York", :date "2015-09-01", :current-streak "Won 5"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Bertens K.", :tournament "US Open", :location "New York", :date "2015-09-02", :current-streak "Won 6"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Mattek-Sands B.", :tournament "US Open", :location "New York", :date "2015-09-05", :current-streak "Won 7"} {:loser-sets-won 0, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Keys M.", :tournament "US Open", :location "New York", :date "2015-09-06", :current-streak "Won 8"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Williams S.", :loser-name "Williams V.", :tournament "US Open", :location "New York", :date "2015-09-09", :current-streak "Won 9"} {:loser-sets-won 1, :winner-sets-won 2, :winner-name "Vinci R.", :loser-name "Williams S.", :tournament "US Open", :location "New York", :date "2015-09-11", :current-streak "Won 10"}]
