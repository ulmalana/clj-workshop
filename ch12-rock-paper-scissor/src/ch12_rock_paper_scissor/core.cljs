(ns ch12-rock-paper-scissor.core
    (:require [rum.core :as rum]))

(enable-console-print!)

(println "This text is printed from src/ch12-rock-paper-scissor/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:computer-choice nil
                          :game-state :setup
                          :user-choice nil
                          :countdown 3}))

(set-validator! app-state #(and
                            (>= 3 (:countdown %) 0)
                            (#{:setup :waiting :complete} (:game-state %))))

(add-watch app-state :countdown-zero
           (fn [_k state old new]
             (when (and (= 1 (:countdown old)) (= 0 (:countdown new)))
               (js/clearInterval (:interval new)))))

(defn computer-choice []
  (nth [:rock :scissor :paper] (rand-int 3)))

(def object-sets->messages
  {#{:rock :scissor} "Rock crushes scissor"
   #{:scissor :paper} "Scissor cut paper"
   #{:paper :rock} "Paper wraps rock"})

(defn result-messages [a b]
  (get object-sets->messages (hash-set a b)))

(def resolutions
  {:rock {:paper :computer-wins
          :scissor :player-wins}
   :scissor {:rock :computer-wins
             :paper :player-wins}
   :paper {:scissor :computer-wins
           :rock :player-wins}})

(defn resolve-game [player computer]
  (if (= player computer)
    :tie
    (get-in resolutions [player computer])))

(defn start-countdown []
  (js/setInterval #(swap! app-state update :countdown dec) 1000))

(defn start-game []
  (let [interval (start-countdown)]
    (swap! app-state
           (fn [state]
             (assoc state
                    :computer-choice (computer-choice)
                    :game-state :waiting
                    :countdown 3
                    :interval interval)))))

(defn player-choice [choice]
  (fn []
    (swap! app-state
           (fn [state]
             (assoc state
                    :player-choice choice
                    :game-state :complete)))))

(rum/defc choice-link-view [kw label countdown]
  (if (zero? countdown)
    [:div [:a {:href (str "#" (name kw))
               :on-click (player-choice kw)}
           label]]
    [:div label]))

(rum/defc countdown-view < rum/reactive [countdown]
  [:div.countdown
   [:div.countdown-message
    (if (> countdown 0)
      "Get ready to make your choice..."
      "Go!")]
   [:h1 countdown]])

(rum/defc choices-view < rum/reactive []
  (let [countdown (:countdown (rum/react app-state))]
    [:div.player-choices-view
     (countdown-view countdown)
     [:div.choices
      [:h3 "Choose one"]
      (choice-link-view :rock "Rock" countdown)
      (choice-link-view :paper "Paper" countdown)
      (choice-link-view :scissor "Scissor" countdown)]]))

(rum/defc result-view < rum/reactive []
  (let [player (:player-choice (rum/react app-state))
        computer (:computer-choice (rum/react app-state))
        result (resolve-game player computer)]
    [:div
     [:div "You played " [:strong (name player)]]
     [:div "The computer played " [:strong (name computer)]]
     (if (= result :tie)
       [:div "It was a tie"]
       [:div
        [:div (result-messages player computer)]
        [:div (if (= result :player-wins)
                "You won"
                "Oops. The computer won")]])
     [:div [:a {:href "#start"
                :onClick start-game} "Play again?"]]]))

(rum/defc game-view < rum/reactive []
  (case (:game-state (rum/react app-state))
    :setup
    [:div "Ready to play?"
     [:div [:a {:href "#start"
                :onClick start-game} "Start"]]]
    :waiting
    (choices-view)
    :complete
    (result-view)))

(rum/defc rock-paper-scissors []
  [:div
   [:h1 "Rock, Paper, Scissor"]
   (game-view)])

;; (rum/mount (hello-world)
;;            (. js/document (getElementById "app"))
;;           )

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (rum/mount (rock-paper-scissors)
             (. js/document (getElementById "app"))))

(on-js-reload)
