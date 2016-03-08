(ns dev
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
    (assoc this
           :server
           (httpkit/run-server
            (server/started-handlers)
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



;; ------------------------------------------------
;; Test loading Hiccup from EDN

(defn ^:private collapse-strs
  "Collapse nested str expressions into one, where possible. Taken
  from hiccup.compiler"
  [expr]
  (if (seq? expr)
    (cons
     (first expr)
     (mapcat
      #(if (and
            (seq? %)
            (symbol? (first %))
            (= (first %) (first expr) `str))
         (rest (collapse-strs %))
         (list (collapse-strs %)))
      (rest expr)))
    expr))

(def header-src-path "com/tamandadam/merrymaking_web/header.edn")
(def index-src-path "com/tamandadam/merrymaking_web/index.edn")

(defn load-src-as-hiccup [path]
  (edn/read-string
   (slurp
    (io/resource path))))

(defn index []
  (let [header-src (load-src-as-hiccup header-src-path)
        body-src (load-src-as-hiccup index-src-path)]
    [(compile-html header-src)
     (compile-html body-src)]))
