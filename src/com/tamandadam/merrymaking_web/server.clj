(ns com.tamandadam.merrymaking-web.server
  (:gen-class :extends javax.servlet.http.HttpServlet) ; for -main method in uberjar
  (:require [com.stuartsierra.component :as component]
            [com.tamandadam.merrymaking-web.handlers :as handlers]
            [com.tamandadam.merrymaking-web.handlers.base :as base]
            [compojure.handler :as compojure]))

            
(defn new-system []
  (component/system-map
   ::handlers (handlers/new-component)
   ::base (base/new-component)))

(def system nil)

(defn init []
  (alter-var-root #'system
                  (constantly
                   (new-system))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system component/stop))

(defn started-handlers []
  (init)
  (start)
  
  (:compiled-routes
   (::handlers system)))

;; ------------------------------------------------
;; ## Ring Handlers (used by Google App Engine)
;;   might replace the need for servlet handlers

(defn ring-init []
  (println "Merrymaking-Web is starting"))

(defn ring-destroy []
  (println "Merrymaking-Web is shutting down"))

(def ring-app
  (let [handlers (started-handlers)]
    (compojure/site handlers)))

