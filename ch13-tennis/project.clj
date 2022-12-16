(defproject ch13-tennis "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [clojure.java-time "0.3.2"]
                 [hikari-cp "2.8.0"]
                 [org.apache.derby/derby "10.14.2.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [org.clojure/java.jdbc "0.7.9"]
                 [semantic-csv "0.2.1-alpha1"]
                 [clj-http "3.10.0"]
                 [compojure "1.6.1"]
                 [metosin/muuntaja "0.6.4"]
                 [org.clojure/data.json "0.2.6"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-jetty-adapter "1.7.1"]]
  :main ^:skip-aot ch13-tennis.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
