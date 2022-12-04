(ns ch10-testing.utils-test
  (:require [clojure.test :refer [are is deftest testing]]
            [ch10-testing.core :refer [price-menu]]
            [ch10-testing.utils :refer :all]
            [expectations :refer [expect in]]
            [midje.sweet :refer [=> fact provided unfinished]]))

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
