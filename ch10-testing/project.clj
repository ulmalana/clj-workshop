(defproject ch10-testing "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [expectations "2.1.10"]
                 [midje "1.9.4"]
                 [org.clojure/test.check "0.10.0"]]
  :plugins [[lein-expectations "0.0.8"]]
  :main ^:skip-aot ch10-testing.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
