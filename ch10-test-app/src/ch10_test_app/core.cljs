(ns ^:figwheel-hooks ch10-test-app.core
  (:require
   [goog.dom :as gdom]
   [rum.core :as rum]))

(println "This text is printed from src/ch10_test_app/core.cljs. Go ahead and edit it and see reloading in action.")

(defn multiply [a b] (* a b))

;; define your app data so that it doesn't get over-written on reload
(defonce state (atom {:counter 0}))

(defn get-app-element []
  (gdom/getElement "app"))

(defn handle-click [state]
  (swap! state update-in [:counter] inc))

(rum/defc counter [number]
  [:div {:on-click #(handle-click state)}
   (str "Click times: " number)])

(rum/defc page-content < rum/reactive []
  [:div {}
   (counter (:counter (rum/react state)))])

(defn mount [el]
  (rum/mount (page-content) el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
