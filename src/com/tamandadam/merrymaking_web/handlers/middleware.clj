(ns com.tamandadam.merrymaking-web.handlers.middleware
  (:require [compojure.route]))

(def routes
  [(compojure.route/resources "/assets/")
   (compojure.route/not-found "<h1>Page not found</h1>")])
