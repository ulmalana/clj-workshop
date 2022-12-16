(ns ch13-tennis.core
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.java.io :as io]
            [ch13-tennis.database :as database]
            [ch13-tennis.ingest :as ingest]
            [ch13-tennis.query :as query]
            [ch13-tennis.elo :as elo])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;;;;;; activity 13.01
;;(ingest/historic database/db
;;                 (io/file "resources/match_scores_1991-2016_unindexed_csv.csv"))

(jdbc/query database/db ["select count(*) from player"])
;; => ({:1 3483})
(jdbc/query database/db ["select count(*) from tennis_match"])
;; => ({:1 95359})

(elo/select-max-elo database/db)
;; => {:max-rating 2974.61M, :player-name "Novak Djokovic"}
;; => 
