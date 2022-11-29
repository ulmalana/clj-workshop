(ns ch08-ns-lib-lein.core-test
  (:require [clojure.test :refer :all]
            [ch08-ns-lib-lein.core :refer :all]
            [expectations :refer [expect]]))

(expect (generate-json-from-hash {:name "Riz" :job "Programmer"})
        "{\"name\":\"Riz\",\"job\":\"Programmer\"}")

(expect (generate-hash-from-json "{\"name\":\"Riz\",\"job\":\"programmer\"}")
        {"name" "Riz", "job" "programmer"})
