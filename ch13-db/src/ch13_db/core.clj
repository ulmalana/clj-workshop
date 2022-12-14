(ns ch13-db.core
  (:require [clojure.java.jdbc :as jdbc]
            [hikari-cp.core :as hikari])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


;; exercise 13.01 establish db connection
(def db {:dbtype "derby"
         :dbname "derby-local"
         :create true})

(jdbc/get-connection db)
;; => #object[org.apache.derby.impl.jdbc.EmbedConnection 0x5bb04ac7 "org.apache.derby.impl.jdbc.EmbedConnection@1538280135 (XID = 167), (SESSIONID = 3), (DATABASE = derby-local), (DRDAID = null) "]

;; exercise 13.02 create connection pool
(def db {:datasource (hikari/make-datasource
                      {:jdbc-url "jdbc:derby:derby-local;create=true"})})

(jdbc/get-connection db)
;; => #object[com.zaxxer.hikari.pool.HikariProxyConnection 0x28a98a8e "HikariProxyConnection@682199694 wrapping org.apache.derby.impl.jdbc.EmbedConnection@1519454532 (XID = 17), (SESSIONID = 5), (DATABASE = derby-local), (DRDAID = null) "]

;; exercise 13.03 define schema
(def create-app-user-ddl
  "CREATE TABLE app_user (
id INT GENERATED ALWAYS AS IDENTITY CONSTRAINT USER_ID_PK PRIMARY KEY,
first_name VARCHAR(32),
surname VARCHAR(32),
height SMALLINT,
weight SMALLINT)")

(def create-activity-ddl
  "CREATE TABLE activity (
id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
activity_type VARCHAR(32),
distance DECIMAL(5,2),
duration INT,
user_id INT REFERENCES app_user ON DELETE CASCADE)")

;; using jdbc
;; (def create-app-user-ddl-2
;;   (jdbc/create-table-ddl :app_user
;;                          [[:id :int "GENERATED ALWAYS AS IDENTITY CONSTRAINT USER_ID_PK PRIMARY KEY"]
;;                           [:first_name "varchar(32)"]
;;                           [:surname "varchar(32)"]
;;                           [:height :smallint]
;;                           [:weight :smallint]]
;;                          {:entities clojure.string/lower-case}))

(jdbc/db-do-commands db [create-app-user-ddl create-activity-ddl])
;; => (0 0)

;; exercise 13.04 data insertion
(jdbc/insert! db :app_user
              {:first_name "Andre"
               :surname "Agassi"
               :height 180
               :weight 80})
;; => ({:1 1M})

(jdbc/insert-multi! db :activity
                    [{:activity_type "run" :distance 8.67 :duration 2520 :user_id 1}
                     {:activity_type "cycle" :distance 17.68 :duration 2703 :user_id 1}])
;; => ({:1 1M} {:1 2M})


;; exercise 13.05 querying db
(jdbc/query db ["select * from app_user"])
;; => ({:id 1, :first_name "Andre", :surname "Agassi", :height 180, :weight 80})

(jdbc/query db ["select * from activity"])
;; => ({:id 1, :activity_type "run", :distance 8.67M, :duration 2520, :user_id 1} {:id 2, :activity_type "cycle", :distance 17.68M, :duration 2703, :user_id 1})

(jdbc/query db ["select * from app_user"] {:keywordize? false :identifiers clojure.string/upper-case})
;; => ({"ID" 1, "FIRST_NAME" "Andre", "SURNAME" "Agassi", "HEIGHT" 180, "WEIGHT" 80})

(jdbc/query db ["select * from app_user"] {:identifiers clojure.string/upper-case :qualifier "app_user"})
;; => (#:app_user{:ID 1, :FIRST_NAME "Andre", :SURNAME "Agassi", :HEIGHT 180, :WEIGHT 80})

(->
 (jdbc/query db ["select * from app_user"] {:identifiers clojure.string/upper-case :qualifier "app_user"})
 first
 keys)
;; => (:app_user/ID :app_user/FIRST_NAME :app_user/SURNAME :app_user/HEIGHT :app_user/WEIGHT)

(jdbc/query db ["select * from activity"] {:as-arrays? true})
;; => [[:id :activity_type :distance :duration :user_id]
;; => [1 "run" 8.67M 2520 1]
;; => [2 "cycle" 17.68M 2703 1]]
