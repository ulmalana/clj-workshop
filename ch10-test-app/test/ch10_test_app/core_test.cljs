(ns ch10-test-app.core-test
    (:require
     [cljs.test :refer-macros [deftest is testing are use-fixtures]]
     [ch10-test-app.core :refer [multiply handle-click]]))

(deftest multiply-test
  (is (= (* 1 2) (multiply 1 2))))

(deftest multiply-test-2
  (is (= (* 75 10) (multiply 10 75))))

(use-fixtures :each {:before (fn [] (def app-state (atom {:counter 0})))
                     :after (fn [] (reset! app-state nil))})

(deftest handle-click-test-multiple
  (testing "Handle multipl clicks"
    (are [result] (= result (handle-click app-state))
      {:counter 1}
      {:counter 2}
      {:counter 3})))

(deftest handle-click-test-one
  (testing "handle one click"
    (is (= {:counter 1} (handle-click app-state)))))
