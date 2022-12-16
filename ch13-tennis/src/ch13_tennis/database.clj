(ns ch13-tennis.database
  (:require [hikari-cp.core :as hikari]
            [clojure.java.jdbc :as jdbc]))

(def db {:datasource (hikari/make-datasource
                      {:jdbc-url "jdbc:derby:tennis;create=true"})})

(def ^:private create-player-ddl
  "create table player (
id varchar(4) constraint player_id_pk primary key,
full_name varchar(128))")

(def ^:private create-tennis-match-ddl
  "create table tennis_match (
id varchar(32) constraint match_id_pk primary key,
tournament_year int,
tournament varchar(32),
tournament_order int,
round_order int,
match_order int,
winner_id varchar(4) references player(id) on delete cascade,
loser_id varchar(4) references player(id) on delete cascade)")

(def ^:private create-elo-ddl
  "create table elo (
id int generated always as identity constraint elo_id_pk primary key,
player_id varchar(4) references player(id) on delete cascade,
rating decimal(6,2))")

(defn load []
  (jdbc/db-do-commands db [create-player-ddl create-tennis-match-ddl create-elo-ddl]))
