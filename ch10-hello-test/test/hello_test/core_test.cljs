(ns hello-test.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer-macros [for-all]]
            [clojure.test.check.clojure-test :refer-macros [defspec]]
            [cuerdas.core :as str]
            [hello-test.core :refer [profanity-filter http-get]]))

(deftest profanity-filter-test
  (testing "filter replaced bad word"
    (is (= "Clojure is great" (profanity-filter "Clojure is bad"))))
  (testing "filter does not replace good words"
    (are [string result] (= result (profanity-filter string))
      "Clojure is great" "Clojure is great"
      "Clojure is brilliant" "Clojure is brilliant")))

(deftest capitalize-test-is
  (testing "test capitalize? function using is macro"
    (is (= "Katy" (str/capitalize "katy")))
    (is (= "John" (str/capital "john")))
    (is (= "Mike" (str/capitalize "mike")))))

(deftest error-thrown-test
  (testing "catching errors in cljs"
    (is (thrown? js/Error (assoc ["dog" "cat" "parrot"] 4 "apple")))))

(deftest http-get-test
  (async done
         (http-get "https://api.github.com/users" {:with-credentials? false
                                                   :query-params {"since" 135}}
                   (fn [response]
                     (is (= 200 (:status response)))
                     (done)))))

;;  property based testing
(defspec simple-test-check 1000
  (for-all [some-string gen/string-ascii]
           (= (str/replace some-string "bad" "great") (profanity-filter some-string))))
