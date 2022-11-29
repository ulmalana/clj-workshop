(ns ch08-ns-lib-lein.core
  (:require [clojure.string :as str]
            [java-time :as time]
            [cheshire.core :as json])
  (:gen-class))

;; activity 8.01

(use '[clojure.string :rename {replace str-replace, reverse str-reverse}])

(def users #{"mr_paul smith" "dr_john blake" "miss_katie hudson"})

(map #(str-replace % #"_" " ") users)
;; => ("mr paul smith" "miss katie hudson" "dr john blake")

(map #(capitalize %) users)
;; => ("Mr_paul smith" "Miss_katie hudson" "Dr_john blake")

(def updated-users
  (into #{}
        (map #(join " "
                    (map (fn [sub-str] (capitalize sub-str))
                         (split (str-replace % #"_" " ") #" ")))
             users)))

updated-users
;; => #{"Mr Paul Smith" "Dr John Blake" "Miss Katie Hudson"}

(use '[clojure.pprint :only (print-table)])

(print-table (map #(hash-map :user-name %) updated-users))
;; |        :user-name |
;; |-------------------|
;; |     Mr Paul Smith |
;; |     Dr John Blake |
;; | Miss Katie Hudson |

(use '[clojure.set :exclude (join)])

(def admins #{"Mr Paul Smith" "Miss Katie Hudson" "Dr Mike Rose" "Mrs Tracy Ford"})

(subset? users admins)
;; => false

(print-table (map #(hash-map :user-name %) updated-users))
;; => nil
;; |        :user-name |
;; |-------------------|
;; |     Mr Paul Smith |
;; |     Dr John Blake |
;; | Miss Katie Hudson |

;; (defn -main [& args]
;;   (-> (str/join " " args)
;;       (str/replace "melon" "banana")
;;       (str/replace "apple" "orange")
;;       println))

;; activity 8.02
(defn -main [& args]
  (let [nums (map read-string args)
        sum (apply + nums)]
    (println sum))
  (println (time/local-time)))

;; activity 8.03
(defn generate-json-from-hash [hash]
  (json/generate-string hash))

(defn generate-hash-from-json [json]
  (json/parse-string json))
