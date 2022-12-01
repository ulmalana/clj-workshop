(ns ^:figwheel-hooks ch09-figwheel.core
  (:require
   [goog.dom :as gdom]
   [rum.core :as rum]
   [jayq.core :as jayq :refer [$]]
   [sablono.util]))

(println "This text is printed from src/ch09_figwheel/core.cljs. Go ahead and edit it and see reloading in action.")

(defn multiply [a b] (* a b))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!" :counter 0}))


(defn get-app-element []
  (gdom/getElement "app"))

(defn increment-likes []
  (swap! app-state update-in [:counter] inc))

(rum/defc band [name likes]
  [:div {:class "band"
         :on-click #(increment-likes)}
   (str name " is liked " likes " times")])

(rum/defc hello-world < rum/reactive []
  [:div {}
   (band "Metallica" (:counter (rum/react app-state)))])


;;; exercise 9.09
(defonce is-element-dropped? (atom false))

(defn attrs [a]
  (clj->js (sablono.util/html-to-dom-attrs a)))

(defn make-draggable []
  (.draggable ($ ".card") (attrs {:revert true :cursor "move"})))

(defn handle-drop [event ui]
  (let [draggable-id (jayq/data (.-draggable ui) "number")]
    (println "Dropping element with id" draggable-id)
    (reset! is-element-dropped? true)
    (.draggable (.-draggable ui) "disable")
    (.droppable ($ (str "#" (.-id (.-target event)))) "disable")
    (.position (.-draggable ui)
               (attrs {:of ($ (str "#" (.-id (.-target event)))) :my "left top" :at "left top"}))))

(defn start-dragging [event ui]
  (reset! is-element-dropped? false))

(defn make-droppable []
  (.droppable ($ (str ".tile"))
              (attrs {:hoverClass "hovered-tile" :drop handle-drop
                      :activate start-dragging})))

(rum/defc tile [text number]
  [:div {:class "tile" :id number} text])

(rum/defc tiles []
  [:.tiles {}
   (tile "first" 1)
   (tile "second" 2)])

(rum/defc dropped-message < rum/reactive []
  [:div {}
   (str "was element dropped? " (rum/react is-element-dropped?))])

(rum/defc card [number]
  [:.card {:data-number number :id number}])

(rum/defc cards []
  [:.cards {}
   (card 1)
   (card 2)])

(rum/defc content []
  [:div {}
   (tiles)
   (cards)
   (dropped-message)])

(defn mount [el]
  (rum/mount (content) el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)
(make-draggable)
(make-droppable)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  (make-draggable)
  (make-droppable)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
