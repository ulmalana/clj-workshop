(ns ch05-reduce.core)

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
