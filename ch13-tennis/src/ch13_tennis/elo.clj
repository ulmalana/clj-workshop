(ns ch13-tennis.elo
  (:require [clojure.java.jdbc :as jdbc]
            [ch13-tennis.query :as query]))

(def k-factor 32)

(defn match-probability
  [p1-rating p2-rating]
  (/ 1
     (+ 1 (Math/pow 10 (/ (- p2-rating p1-rating) 1000)))))

(defn recalculate-rating
  [prev-rating expected-outcome real-outcome]
  (+ prev-rating (* k-factor (- real-outcome expected-outcome))))

(defn- expected-outcomes
  [winner-rating loser-rating]
  (let [winner-expected-outcome (match-probability winner-rating loser-rating)]
    [winner-expected-outcome (- 1 winner-expected-outcome)]))

(defn calculate-new-ratings
  [current-player-ratings {:keys [winner_id loser_id]}]
  (let [winner-rating (get current-player-ratings winner_id 1000)
        loser-rating (get current-player-ratings loser_id 1000)
        [winner-expected-outcome loser-expected-outcome] (expected-outcomes winner-rating loser-rating)]
    [{:player_id winner_id
      :rating (recalculate-rating winner-rating winner-expected-outcome 1)}
     {:player_id loser_id
      :rating (recalculate-rating loser-rating loser-expected-outcome 0)}]))

(defn calculate-all [db]
  (->> (query/all-tennis-matches db)
       (reduce
        (fn [{:keys [current-player-ratings] :as acc} match]
          (let [[{winner-id :player_id :as new-winner-rating} {loser-id :player_id :as new-loser-rating}] (calculate-new-ratings current-player-ratings match)]
            (-> acc
                (update :elo-ratings into [new-winner-rating
                                           new-loser-rating])
                (assoc-in [:current-player-ratings winner-id] (:rating new-winner-rating))
                (assoc-in [:current-player-ratings loser-id] (:rating new-loser-rating)))))
        {:elo-ratings []
         :current-player-ratings {}})
       :elo-ratings))

(defn persist-all [db]
  (let [elo-ratings (calculate-all db)]
    (jdbc/insert-multi! db :elo elo-ratings)))

(defn select-max-elo [db]
  (jdbc/query db ["select p.full_name, e.rating from player p, elo e where p.id = e.player_id"]
              {:result-set-fn (fn [rs]
                                (reduce
                                 (fn [{:keys [max-rating] :as acc} {:keys [full_name rating]}]
                                   (cond-> acc
                                     (< max-rating rating) (assoc :max-rating rating :player-name full_name)))
                                 {:max-rating Integer/MIN_VALUE
                                  :player-name nil}
                                 rs))}))

(defn persist [db elo-ratings]
  (jdbc/insert-multi! db :elo elo-ratings))
