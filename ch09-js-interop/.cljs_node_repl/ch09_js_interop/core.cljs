(ns ch09-js-interop.core
  (:require [clojure.browser.repl :as repl]))

;; (defonce conn
;;   (repl/connect "http://localhost:9000/repl"))

(defn test-fun []
  (println "ini percobaan fungsi"))

(def languages {:Clojure "CLJ"
                :ClojureScript "CLJS"
                :JavaScript "JS"})

(defn language-abbreviator [language]
  (if-let [lang (get languages language)]
           lang
           (throw (js/Error. "Language not supported"))))

(defn get-language-of-the-week [languages]
  (let [lang-of-the-week (rand-nth languages)]
    (try
      (str "Language of the week is: " (language-abbreviator lang-of-the-week))
      (catch js/Error e
        (str lang-of-the-week " is not supported"))
      (finally (println lang-of-the-week "was chosen as the language of the week")))))

(enable-console-print!)

(println "Hello world!")
