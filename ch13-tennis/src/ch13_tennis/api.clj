(ns ch13-tennis.api
  (:require [clojure.edn :as edn]
            [compojure.core :refer [context defroutes GET PUT]]
            [compojure.route :as route]
            [muuntaja.middleware :as middleware]
            [ch13-tennis.database :as database]
            [ch13-tennis.elo :as elo]
            [ch13-tennis.ingest :as ingest]
            [ch13-tennis.query :as query]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :as params]
            [jumblerg.middleware.cors :refer [wrap-cors]]))

(defroutes routes
  (context "/players" []
           (GET "/" []
                {:body (query/all-players database/db)})
           (GET "/:id" [id]
                (when-first [user (query/player database/db id)]
                  {:body user}))
           (GET "/:id/tennis-matches" [id]
                {:body (query/tennis-matches-by-player database/db id)})
           (GET "/:id/elo" [id]
                (when-first [elo (query/player-elo database/db id)]
                  {:body elo})))
  (context "/tennis-matches" []
           (GET "/" []
                {:body (query/all-tennis-matches database/db)})
           (GET "/:id" [id]
                (when-first [tennis-match (query/tennis-match database/db id)]
                  {:body tennis-match}))
           (PUT "/:id" req
                (let [id (-> req :params :id)
                      {:keys [winner_id loser_id] :as tennis-match} (assoc (edn/read-string (slurp (:body req))) :id id)
                      [{winner-elo :rating}] (query/player-elo database/db winner_id)
                      [{loser-elo :rating}] (query/player-elo database/db loser_id)
                      new-player-ratings (elo/calculate-new-ratings
                                          {winner_id winner-elo
                                           loser_id loser-elo}
                                          tennis-match)]
                  (ingest/tennis-match database/db tennis-match)
                  (elo/persist database/db new-player-ratings)
                  {:status 201
                   :headers {"Link" (str "/tennis-matches/" id)}})))
  (route/not-found "Page not found!!"))

(defn run []
  (run-jetty
   (-> routes
       middleware/wrap-format
       params/wrap-params
       (wrap-cors ".*")
       (wrap-cors identity))
   {:port 8080
    :join? false}))

