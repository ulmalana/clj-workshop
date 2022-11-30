(ns ch09-host-interop.book-utils
  (:require [clojure.java.io :as io])
  (:import [java.io PushbackReader]))

(defn save-to [location data]
  (spit location data :append true))

(defn save-book-order
  [order-file year prog-lang number price]
  (save-to order-file {:year year :prog-lang prog-lang :number number :price price}))

(defn calculate-book-price
  [books title number]
  (-> (get books title)
      :price
      (* number)
      float))

(defn display-bought-book-message
  [title number total]
  (println "Buying" number title "for total: $" total))

(defn display-order [order books]
  (str "Bought " (:number order) ": " (:title (get (get books (:year order)) (:prog-lang order))) " published in " (name (:year order)) " for $" (:price order)))

(defn read-one-order [r]
  (try
    (read r)
    (catch java.lang.RuntimeException e
      (if (= "EOF while reading" (.getMessage e))
        ::EOF
        (throw e)))))

(defn file-exists [location]
  (.exists (io/as-file location)))

(defn load-orders [file]
  (if (file-exists file)
    (with-open [r (PushbackReader. (io/reader file))]
      (binding [*read-eval* false]
        (doall (take-while #(not= ::EOF %) (repeatedly #(read-one-order r))))))
    []))
