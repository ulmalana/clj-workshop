(ns fitness.core
  (:require [fitness.ingest :as ingest]
            [fitness.query :as query]
            [fitness.schema :as schema]
            [semantic-csv.core :as sc]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(def users
  [{:first_name "Andre"
    :surname "Agassi"
    :height 180
    :weight 80}
   {:first_name "Pete"
    :surname "Sampras"
    :height 185
    :weight 77}
   {:first_name "Steffi"
    :surname "Graff"
    :height 176
    :weight 64}])

;; (doseq [user users]
;;   (ingest/user schema/db user))

(def accessors
  {:activity_type :type
   :distance :distance_metres
   :duration :duration_seconds
   :user_id :userid
   :activity_date (fn [{:keys [day month year]}]
                    (str year "-" month "-" day))})

(defn apply-accessors [row accessors]
  (reduce-kv
   (fn [acc target-key accessor]
     (assoc acc target-key (accessor row)))
   {}
   accessors))

(def activities
  (->> (csv/read-csv (io/reader "resources/sample-activities.csv"))
       sc/mappify
       (map #(apply-accessors % accessors))))

(first activities)
;; => {:activity_type "swim", :distance "5100.00", :duration "9180", :user_id "1", :activity_date "2019-01-22"}

(doseq [activity core/activities] (ingest/activity schema/db activity))
;; nil

(count (query/all-users schema/db))
;; 3
(count (query/all-activities schema/db))
;; 60
(query/user schema/db 1)
;; ({:id 1, :first_name "Andre", :surname "Agassi", :height 180, :weight 80})
(query/activity schema/db 1)
;; ({:id 1, :activity_type "swim", :distance 5100.00M, :duration 9180, :activity_date #inst "2019-01-21T17:00:00.000-00:00", :user_id 1})
