(ns ch09-host-interop.utils
  (:require [clojure.java.io :as io])
  (:import [java.io PushbackReader]))

(defn calculate-coffee-price
  [coffees coffee-type number]
  (-> (get coffees coffee-type)
      (* number)
      float))

(defn display-bought-coffee-message
  [type number total]
  (println "Buying" number (name type) "coffees for total:$" total))

(defn save-to [location data]
  (spit location data :append true))

(defn file-exists? [location]
  (.exists (io/as-file location)))

(defn read-one-order [r]
  (try
    (read r)
    (catch java.lang.RuntimeException e
      (if (= "EOF while reading" (.getMessage e))
        ::EOF
        (throw e)))))

(defn load-orders
  "Reads a seq of orders stored in file"
  [file]
  (if (file-exists? file)
    (with-open [r (PushbackReader. (io/reader file))]
      (binding [*read-eval* false]
        (doall (take-while #(not= ::EOF %)
                           (repeatedly #(read-one-order r))))))
    []))

(defn save-coffee-order
  [orders-file type number price]
  (save-to orders-file {:type type :number number :price price}))

(defn display-order [order]
  (str "Bought " (:number order) " cups of " (name (:type order)) " for $" (:price order)))
