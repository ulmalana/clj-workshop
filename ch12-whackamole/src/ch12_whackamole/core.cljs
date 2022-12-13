(ns ch12-whackamole.core
    (:require [rum.core :as rum]))

(enable-console-print!)

(println "This text is printed from src/ch12-whackamole/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(def game-length-in-seconds 20)
(def millis-remaining (atom (* game-length-in-seconds 1000)))
(def points (atom 0))
(def game-state (atom :waiting))
(def clock-interval (atom nil))

(def moles (atom (into []
                       (repeat 5 {:status :waiting
                                  :remaining-millis 0}))))

(defn activate-mole [mole-idx]
  (swap! moles
         (fn [ms]
           (update ms mole-idx
                   #(if (= :waiting (:status %))
                      {:status :live :remaining-millis 3000}
                      %)))))

(defn deactivate-mole [mole-idx]
  (swap! moles
         (fn [ms]
           (assoc ms mole-idx
                  {:status :waiting :remaining-millis 0}))))

(defn mole-countdown []
  (swap! moles
         (fn [ms]
           (into []
                 (map (fn [m]
                        (if (= (:status m) :live)
                          (let [new-remaining (max (- (:remaining-millis m) 100) 0)]
                            (if (pos? new-remaining)
                              (assoc m :remaining-millis new-remaining)
                              {:status :waiting :remaining-millis 0}))
                          m))
                      ms)))))

(defn update-moles []
  (mole-countdown)
  (let [activate-mole-count (count (filter #(= (:status %) :live) @moles))]
    (if (< activate-mole-count 2)
      (activate-mole (rand-int 5)))))

(defn reset-moles []
  (swap! moles
         (fn [ms]
           (map #(assoc % :status :waiting :remaining-millis 0) ms))))

(defn whack [mole-idx]
  (let [{:keys [status waiting]} (nth @moles mole-idx)]
    (when (= status :live)
      (deactivate-mole mole-idx)
      (swap! points inc))))

(defn clock-tick []
  (if (= @millis-remaining 0)
    (do
      (reset! game-state :waiting)
      (reset-moles))
    (do
      (update-moles)
      (swap! millis-remaining #(- % 100)))))

(defn start-clock []
  (when @clock-interval
    (js/clearInterval @clock-interval))
  (swap! clock-interval
         (fn [] (js/setInterval clock-tick 100))))

(defn start-game []
  (start-clock)
  (reset! game-state :playing)
  (reset! points 0)
  (reset! millis-remaining (* game-length-in-seconds 1000)))

(rum/defc clock < rum/reactive []
  [:div.clock
   [:span "Remaining time: "]
   [:span.time
    (Math/floor (/ (rum/react millis-remaining) 1000))]])

(rum/defc score < rum/reactive []
  [:div.score
   [:span "Score: "]
   [:span (rum/react points)]])

(rum/defc start-game-button < rum/reactive []
  (if (= (rum/react game-state) :waiting)
    [:button
     {:onClick start-game}
     "Click to play!"]
    [:div "Game on"]))

(rum/defc single-mole-view [mole-idx {:keys [status remaining-millis]}]
  [:div {:class [(str "mole " (name status))]}
   [:a {:onClick (partial whack mole-idx)}
    (str "MOLE " (name status) "!")]])

(rum/defc moles-view < rum/reactive []
  (let [ms (rum/react moles)]
    [:div {:class "game moles"}
     (single-mole-view 0 (first ms))
     (single-mole-view 1 (second ms))
     (single-mole-view 2 (nth ms 2))
     (single-mole-view 3 (nth ms 3))
     (single-mole-view 4 (nth ms 4))]))

(rum/defc app []
  [:div#main
   [:div.header
    [:h1 "Welcome to Whack a mole"]
    [:p "when a mole goes 'live', click on it as fast as you can"]
    (start-game-button)
    (clock)
    (score)]
   (moles-view)])

(rum/defc hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this and watch it change!"]])


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (rum/mount (app)
             (. js/document (getElementById "app")))
)

(on-js-reload)
