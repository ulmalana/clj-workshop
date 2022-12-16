(ns ch13-tennis.query
  (:require [clojure.java.jdbc :as jdbc]))

(defn all-tennis-matches [db]
  (jdbc/query db ["select * from tennis_match order by tournament_year, tournament_order, round_order desc, match_order"]))

