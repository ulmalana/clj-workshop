(ns ch10-testing.utils-test
  (:require [clojure.test :refer [are is deftest testing]]
            [ch10-testing.core :refer [price-menu]]
            [ch10-testing.utils :refer :all]))

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
