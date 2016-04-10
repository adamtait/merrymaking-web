(ns com.tamandadam.merrymaking-web.server
  (:gen-class :extends javax.servlet.http.HttpServlet) ; for -main method in uberjar
  (:require [com.stuartsierra.component :as component]
            [compojure.handler :as compojure]
            [com.tamandadam.merrymaking-web.handlers :as handlers]
            [com.tamandadam.merrymaking-web.handlers.base :as base]
            [com.tamandadam.merrymaking-web.handlers.rsvp :as rsvp]
            ))

            
(defn new-system []
  (component/system-map
   ::handlers (handlers/new-component)
   ::base (base/new-component)
   ::rsvp (rsvp/new-component)))

(def system nil)

(defn init []
  (alter-var-root
   #'system
   (constantly
    (new-system))))

(defn start []
  (println "Merrymaking-Web is starting")
  (alter-var-root #'system component/start)
  :ready)

(defn stop []
  (println "Merrymaking-Web is shutting down")
  (alter-var-root #'system component/stop))



;; ------------------------------------------------
;; ## Handlers used by Google App Engine
;;   might replace the need for servlet handlers

(def app-engine-handlers nil)

(defn app-engine-start []
  (init)
  (start)

  (alter-var-root
   #'app-engine-handlers
   (constantly
    (:compiled-routes
     (::handlers system)))))

(defn app-engine-stop []
  (stop)
  (alter-var-root #'app-engine-handlers nil))

