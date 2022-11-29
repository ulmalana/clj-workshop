(defproject ch08-ns-lib-lein "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [clojure.java-time "0.3.2"]
                 [cheshire "3.0.0"]]
  :main ^:skip-aot ch08-ns-lib-lein.core
  :repl-options {:init-ns ch08-ns-lib-lein.core}
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[expectations "2.1.10"]]}
             :qa {:plugins [[lein-expectations "0.0.8"]]
                  :dependencies [[expectations "2.1.10"]]}})

