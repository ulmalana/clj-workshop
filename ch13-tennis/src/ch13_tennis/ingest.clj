(ns ch13-tennis.ingest
  (:require [ch13-tennis.parse :as parse]
            [clojure.java.jdbc :as jdbc]))

(defn historic [db file-path]
  (let [{:keys [players matches]} (parse/historic file-path)]
    (jdbc/insert-multi! db :player players)
    (jdbc/insert-multi! db :tennis_match matches)))

