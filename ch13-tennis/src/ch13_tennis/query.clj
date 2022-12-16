(ns ch13-tennis.query
  (:require [clojure.java.jdbc :as jdbc]))

(defn all-tennis-matches [db]
  (jdbc/query db ["select * from tennis_match order by tournament_year, tournament_order, round_order desc, match_order"]))

(defn all-players [db]
  (jdbc/query db ["select * from player"]))

(defn player [db id]
  (jdbc/query db [(str "select * from player where id = '" id "'")]))

(defn tennis-matches-by-player [db id]
  (jdbc/query db [(str "select * from tennis_match where winner_id = '" id "' or loser_id = '" id "'")]))

(defn player-elo [db id]
  (jdbc/query db [(str "select e.rating, e.id from elo e, player p where e.player_id = p.id and p.id = '" id "' and e.id in (select max(e2.id) from elo e2 where e2.player_id = '" id "')")]))

(defn tennis-match [db id]
  (jdbc/query db [(str "select * from tennis_match where id = '" id "'")]))
