;; This test runner is intended to be run from the command line
(ns ch10-test-app.test-runner
  (:require
    ;; require all the namespaces that you want to test
    [ch10-test-app.core-test]
    [figwheel.main.testing :refer [run-tests-async]]))

(defn -main [& args]
  (run-tests-async 5000))
