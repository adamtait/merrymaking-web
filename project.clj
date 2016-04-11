(defproject com.tamandadam/merrymaking-web "0.0.1-SNAPSHOT"
  :description "Website for Tam & Adam's wedding"
  :url "http://tamandadam.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}


  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.stuartsierra/component "0.2.1"]
                 [compojure "1.1.8"]
                 [javax.servlet/servlet-api "2.5"]

                 [hiccup "1.0.5"]
                 
                 [com.google.appengine/appengine-api-1.0-sdk "1.9.34"]]


  :source-paths      ["src/clojure"]
  ;;:java-source-paths ["src/java"]
  :javac-options     ["-target" "1.7" "-source" "1.7" "-Xlint:-options"]

  :min-lein-version "2.0.0"

  :resource-paths ["config", "resources"]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [http-kit "2.1.16"]]
                   ;;:aliases {"run-dev" ["trampoline" "run" "-m" "user/go"]}
                   }
             
             :production
             {:ring
              {:open-browser? false
               :stacktraces? true
               :auto-reload? false}}
             
             :uberjar {:aot [com.tamandadam.merrymaking-web.server]}}
  :main ^{:skip-aot true} com.tamandadam.merrymaking-web.server

  :plugins [[lein-ring "0.9.7"]]
  :ring {
         :handler com.tamandadam.merrymaking-web.server/app-engine-handlers
         :init com.tamandadam.merrymaking-web.server/app-engine-start
         :destroy com.tamandadam.merrymaking-web.server/app-engine-stop})

