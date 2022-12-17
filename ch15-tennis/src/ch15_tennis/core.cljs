(ns ch15-tennis.core
    (:require 
              [reagent.core :as r]
              [reagent.dom :as rd]))

(enable-console-print!)

(println "This text is printed from src/ch15-tennis/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (r/atom {:players []
                            :current-player nil}))

(defn fetch-player [id full_name]
  (-> (js/fetch (str "http://localhost:8080/players/" id "/elo"))
      (. then (fn [response] (.json response)))
      (. then (fn [json] (swap! app-state assoc-in [:current-player]
                                (assoc (js->clj json :keywordize-keys true) :full_name full_name))))))

(defn player-alone [{:keys [rating full_name]}]
  [:div
   (str full_name " has a rating of: " rating)])

(defn player-list-button []
  [:button.btn
   {:on-click #(swap! app-state assoc-in [:current-player] nil)}
   "Display all players"])

(defn player [{:keys [id full_name]}]
  [:div
   [:span
    [:a {:href "#"
         :on-click (partial fetch-player id full_name)}
     full_name]]])

(defn player-list [players]
  (if (empty? players)
    [:div "click the button to fetch players"]
    (into [:div] (map player players))))

(defn fetch-players []
  (-> (js/fetch "http://localhost:8080/players/")
      (. then (fn [response] (.json response)))
      (. then (fn [json] (swap! app-state assoc-in [:players]
                                (js->clj json :keywordize-keys true))))))

(defn clear-players []
  (swap! app-state assoc-in [:players] []))

(defn fetch-or-clear-button []
  (let [handler (if (empty? (:players @app-state))
                  fetch-players
                  clear-players)
        text (if (empty? (:players @app-state))
               "Fetch players"
               "Clear players")]
    [:button.btn
     {:on-click handler}
     text]))


(defn app []
  (if (:current-player @app-state)
    [:div
     [player-list-button]
     [player-alone (:current-player @app-state)]]
    [:div
     [fetch-or-clear-button]
     [player-list (:players @app-state)]]))

(rd/render [app]
           (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
