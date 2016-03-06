(ns com.tamandadam.merrymaking-web.handlers.base
  (:require [com.stuartsierra.component :as component]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## resource actions

(defn api
  [component request]
  "<h2>the future home of Merrymaking with Tam & Adam</h2>")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## Component API

(defrecord BaseAPI [])

(defn new-component []
  (->BaseAPI))
