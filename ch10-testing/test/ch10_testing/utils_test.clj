(ns ch10-testing.utils-test
  (:require [clojure.test :refer :all]
            [ch10-testing.core :refer [price-menu]]
            [ch10-testing.utils :refer :all]
            [expectations :refer [expect in side-effects]]
            [midje.sweet :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]))

(deftest calculate-coffee-price-test-with-single-is
  (testing "Single test with is macro"
    (is (= (calculate-coffee-price price-menu :latte 1)
           0.5))))

(deftest calculate-coffee-price-test-with-multiple-is
  (testing "multiple tests with is macro"
    (is (= (calculate-coffee-price price-menu :latte 1) 0.5))
    (is (= (calculate-coffee-price price-menu :latte 2) 1.0))
    (is (= (calculate-coffee-price price-menu :latte 3) 1.5))))

(deftest calculate-coffee-price-test-with-are
  (testing "multiple tests with are macro"
    (are [coffee-hash coffee-type num-cups result]
        (= (calculate-coffee-price coffee-hash coffee-type num-cups) result)
      price-menu :latte 1 0.5
      price-menu :latte 2 1.0
      price-menu :latte 3 1.5)))

;; expectations
(expect 1.5 (calculate-coffee-price price-menu :latte 3))

(expect ClassCastException (calculate-coffee-price price-menu :latte "1"))

;; error test
;; (expect ClassCastException (calculate-coffee-price price-menu :latte 1))

;; check return type
(expect Number (calculate-coffee-price price-menu :latte 2))

;; check element in a collection
(expect {:latte 0.5} (in price-menu))

;; midje
(fact (calculate-coffee-price price-menu :latte 3) => 1.5)

(unfinished get-currency)

(def test-currency :euro)

(defn get-bought-coffee-message-with-currency
  [type number total currency]
  (format "Buying %d %s coffees for total: %s%s" number (name type) (get-currency test-currency) total))

(fact "Message about number of bought coffees should include currency symbol"
      (get-bought-coffee-message-with-currency :latte 3 1.5 :euro) =>
      "Buying 3 latte coffees for total: €1.5"
      (provided
       (get-currency test-currency) => "€"))

;; property-based testing
(gen/sample gen/small-integer)

;; custom generator
(gen/sample (gen/fmap inc gen/small-integer))

;; property
(defspec coffee-price-test-check 1000
  (prop/for-all [int gen/nat]
                (= (float (* int (:latte price-menu)))
                   (calculate-coffee-price price-menu :latte int))))

(defspec coffee-price-test-check-all-params 1000
  (prop/for-all [int (gen/fmap inc gen/nat)
                 price-hash (gen/map gen/keyword
                                     (gen/double* {:min 0.1 :max 999
                                                   :infinite? false
                                                   :NaN? false})
                                     {:min-elements 2})]
                (let [coffee-tuple (first price-hash)]
                  (= (float (* int (second coffee-tuple)))
                     (calculate-coffee-price price-hash
                                             (first coffee-tuple) int)))))

;; activity 10.01
(deftest display-order-test-1
  (testing "Multiple tests with is macro"
    (is (= (display-order {:number 4 :price 3.8 :type :latte})
           "Bought 4 cups of latte for $3.8"))
    (is (= (display-order {:number 7 :price 6.3 :type :espresso})
           "Bought 7 cups of espresso for $6.3"))))

(deftest display-order-test-2
  (testing "Multiple tests with are macro"
    (are [order result]
        (= (display-order order) result)
      {:number 2 :price 1.5 :type :latte} "Bought 2 cups of latte for $1.5"
      {:number 3 :price 6.3 :type :mocha} "Bought 3 cups of mocha for $6.3"
      {:number 8 :price 10 :type :espresso} "Bought 8 cups of espresso for $10")))

(deftest file-exists-test-1
  (testing "file does not exist"
    (testing "multiple tests with is macro"
      (is (false? (file-exists? "no-file")))
      (is (false? (file-exists? "missing-file"))))
    (testing "multiple tests with are macro"
      (are [file] (false? (file-exists? file))
                  "eeettcc"
                  "tmp-tmp"
                  "no-file-here"))))

(deftest file-exists-test-2
  (testing "file does exist"
    (testing "multiple tests with is macro"
      (is (file-exists? "/etc"))
      (is (file-exists? "/lib")))
    (testing "multiple tests with are macro"
      (are [file] (true? (file-exists? file))
        "/etc"
        "/var"
        "/tmp"))))

(defn uuid []
  (str (java.util.UUID/randomUUID)))

(deftest saves-coffee-order
  (testing "saves coffee order"
    (let [test-file (str "/tmp/" (uuid) ".edn")
          test-data {:type :latte, :number 2, :price 2.6}]
      (save-coffee-order test-file :latte 2 2.6)
      (is (= (list test-data) (load-orders test-file))))))

(deftest loads-empty-vector-from-not-existing-file
  (testing "saving and loading"
    (let [test-file (str "/tmp/" (uuid) ".edn")]
      (is (= [] (load-orders test-file))))))

(deftest can-save-and-load-data
  (testing "saving and loading"
    (let [test-file (str "/tmp/" (uuid) ".edn")
          test-data {:number 1 :type :latte}]
      (save-to test-file test-data)
      (is (= (list test-data) (load-orders test-file))))))

(expect "Bought 4 cups of latte for $3.8" (display-order {:number 4 :price 3.8 :type :latte}))

(expect "Bought 7 cups of espresso for $6.3" (display-order {:number 7 :price 6.3 :type :espresso}))

(expect String (display-order {:number 7 :price 6.3 :type :espresso}))

(expect #"Bought 7 cups" (display-order {:number 7 :price 6.3 :type :espresso}))
(expect #"cups of espresso" (display-order {:number 7 :price 6.3 :type :espresso}))
(expect #"for \$6\.3" (display-order {:number 7 :price 6.3 :type :espresso}))

(expect true (file-exists? "/tmp"))
(expect false (file-exists? "no-file"))
(expect Boolean (file-exists? "etc"))

(expect [["/tmp/menu.edn" {:type :latte :number 1 :price 2.4} :append true]
         ["/tmp/menu.edn" {:type :latte :number 3 :price 4.7} :append true]]
        (side-effects [spit]
                      (save-to "/tmp/menu.edn" {:type :latte :number 1 :price 2.4})
                      (save-to "/tmp/menu.edn" {:type :latte :number 3 :price 4.7})))

(expect [["/tmp/orders.edn" :latte 1 2.4]
         ["/tmp/orders.edn" :latte 2 3.9]]
        (side-effects [save-coffee-order]
                      (save-coffee-order "/tmp/orders.edn" :latte 1 2.4)
                      (save-coffee-order "/tmp/orders.edn" :latte 2 3.9)))

(expect [["/tmp/coffees.edn" {:type :latte :number 1 :price 2.4}]
         ["/tmp/coffees.edn" {:type :latte :number 2 :price 3.9}]]
        (side-effects [save-to]
                      (save-coffee-order "/tmp/coffees.edn" :latte 1 2.4)
                      (save-coffee-order "/tmp/coffees.edn" :latte 2 3.9)))

(expect [] (load-orders "/tmp/data.edn"))


(facts "Passing an order should return display message"
       (fact (display-order {:number 4 :price 3.8 :type :latte}) =>
             "Bought 4 cups of latte for $3.8")
       (fact (display-order {:number 7 :price 6.3 :type :espresso}) =>
             "Bought 7 cups of espresso for $6.3"))

(facts "Returned message should match regular expression"
       (fact (display-order {:number 7 :price 6.3 :type :espresso}) =>
             #"Bought 7 cups")
       (fact (display-order {:number 7 :price 6.3 :type :espresso}) =>
             #"cups of espresso")
       (fact (display-order {:number 7 :price 6.3 :type :espresso}) =>
             #"for \$6\.3"))

(facts "True should be returned when a file exists"
       (fact (file-exists? "/tmp") => true)
       (fact (file-exists? "/etc") => true))

(facts "False should be returned when a file doesnt exist"
       (fact (file-exists? "no-file") => false)
       (fact (file-exists? "missing-file") => false))

(facts "Empty vector should be returned when there is no orders file"
       (fact (load-orders "/tmp/data.edn") => [])
       (fact (load-orders "/tmp/no-data.edn") => []))



(defspec display-order-test-check 1000
  (prop/for-all [order (gen/fmap (fn [[number type price]]
                                   {:number number
                                    :type type
                                    :price price})
                                 (gen/tuple (gen/large-integer* {:min 0})
                                            gen/keyword
                                            (gen/double* {:min 0.1 :max 999
                                                          :infinite? false
                                                          :NaN? false})))]
                (= (str "Bought " (:number order) " cups of " (name (:type order)) " for $" (:price order)) (display-order order))))

(defspec file-exists-test-check 1000
  (prop/for-all [file gen/string-alphanumeric]
                (false? (file-exists? file))))

(defspec load-orders-test-check 1000
  (prop/for-all [file gen/string-alphanumeric]
                (vector? (load-orders file))))
