(ns ch14-http.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [GET defroutes DELETE PUT]]
            [compojure.route :as route]
            [muuntaja.middleware :as middleware]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [clojure.edn :as edn])
  (:gen-class))

(def db (atom {}))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn handler [request]
  {:status 200
   :body "Halo Riz"})

(def route
  (GET "/" request "Halo Riz"))

(defroutes routes
  (GET "/" request "Halo Riz!")
  (GET "/route-1" request "Halo from route-1")
  (GET "/route-2" request "Halo from route-2")
  (GET "/string" request "a simple string response")
  ;; (GET "/data-structure" request
  ;;      {:body {:a 1
  ;;              :b #{2 3 4}
  ;;              :c "nested data structure"}})
  (GET "/data-structure" request
       (when-let [data-structure (@db :data)]
         {:body data-structure}))
  (PUT "/data-structure" request
       (swap! db assoc :data (:body-params request))
       {:status 201})
  (DELETE "/data-structure" request
          (swap! db dissoc :data))
  (route/files "/files/" {:root "./resources/"})
  (route/not-found "Page not found"))

;; (def app
;;   (run-jetty handler {:port 8080
;;                       :join? false}))

(defn run []
  (run-jetty
   (middleware/wrap-format routes) {:port 8080
                                    :join? false}))

(def app (run))

;; exercise 14.04 work with request body
;;; write data
(-> (http/put "http://localhost:8080/data-structure"
              {:content-type :application/json
               :body (json/write-str {:a 1
                                      :b #{2 3 4}})})
    :status)
;; => 201

;; retrieve data
(-> (http/get "http://localhost:8080/data-structure"
              {:accept :application/edn})
    :body
    edn/read-string)
;; => {:b [4 3 2], :a 1}

;; edn persistence
(-> (http/put "http://localhost:8080/data-structure"
              {:content-type :application/edn
               :body (pr-str {:a 1
                              :b #{2 3 4 5}})})
    :status)
;; => 201

(-> (http/get "http://localhost:8080/data-structure"
              {:accept :application/edn})
    :body
    edn/read-string)
;; => {:a 1, :b #{4 3 2 5}}

;; delete data
(-> (http/delete "http://localhost:8080/data-structure")
    :status)
;; => 200

;; (-> (http/get "http://localhost:8080/data-structure"
;;              {:accept :application/edn}))
;; Execution error (ExceptionInfo) at slingshot.support/stack-trace (support.clj:201).
;; clj-http: status 404
