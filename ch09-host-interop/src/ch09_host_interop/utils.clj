(ns ch09-host-interop.utils)

(defn calculate-coffee-price
  [coffees coffee-type number]
  (-> (get coffees coffee-type)
      (* number)
      float))

(defn display-bought-coffee-message
  [type number total]
  (println "Buying" number (name type) "coffees for total:$" total))
