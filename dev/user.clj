(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]

   [com.stuartsierra.component :as component]
   [org.httpkit.server :as httpkit]
   [com.tamandadam.merrymaking-web.server :as server]

   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [hiccup.compiler :refer [compile-html]]
   [hiccup.page :refer [html5]]))

;; ------------------------------------------------
;; ## Development Server

(defrecord DevServerComponent [port]
  component/Lifecycle
  (start [this]
    (server/app-engine-start)
    (assoc
     this :server
     (httpkit/run-server
      server/app-engine-handlers
      {:port port})))

  (stop [this]
    ((get this :server) :timeout 1000)
    (dissoc this :server)))

(defn new-dev-server-component [port]
  (component/using
    (->DevServerComponent port)
    [::handlers]))


;; ------------------------------------------------
;; ## Reloaded

(def system
  "A Var containing an object representing the application under
  development."
  nil)

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  [port]
  (alter-var-root
   #'system
   (constantly
    (new-dev-server-component port))))

(defn start
  "Starts the system running, updates the Var #'system."
  []
  (alter-var-root #'system component/start))

(defn stop
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (alter-var-root #'system component/stop))

(defn go
  "Initializes and starts the system running."
  ([]
   (go 8080))
  ([port]
   (init port)
   (start)
   :ready))

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (refresh :after `go))
