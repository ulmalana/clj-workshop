(ns ch15-frontend.core
    (:require 
              [reagent.core :as reagent :refer [atom]]
              [reagent.dom :as rd]))

(enable-console-print!)

(println "This text is printed from src/ch15-frontend/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"
                          :button-on? true}))
(def my-images
  ["https://picsum.photos/id/0/300/200"
   "https://picsum.photos/id/1/300/200"
   "https://picsum.photos/id/10/300/200"
   "https://picsum.photos/id/100/300/200"
   "https://picsum.photos/id/1000/300/200"
   "https://picsum.photos/id/1001/300/200"
   "https://picsum.photos/id/1002/300/200"
   "https://picsum.photos/id/1003/200/300"
   "https://picsum.photos/id/1004/300/200"
   "https://picsum.photos/id/1005/300/200"])

(defn image-with-width [url width]
  [:img {:src url
         :style {:width width
                 :border "solid gray 3px"
                 :border-radius "10px"}}])

(defn image-grid [images]
  (into [:div]
        (map (fn [image-data]
               [:div {:style {:float "left"
                              :margin-left "20px"}}
                [image-with-width image-data "500px"]])
             images)))

(defn button []
  (let [text (if (get-in @app-state [:button-on?]) "ON" "OFF")]
    [:button
     {:on-click #(swap! app-state update-in [:button-on?] not)}
     text]))

(defn image [url]
  [:img {:src url
         :style {:width "500px"
                 :border "solid gray 3px"
                 :border-radius "10px"}}])

(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [button]
   ;;[:div [image "https://picsum.photos/id/0/400/400"]]
   [image-grid my-images]
   [:h3 "Edit this and watch it change!"]])

(rd/render [hello-world]
           (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
