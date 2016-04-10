(ns com.tamandadam.merrymaking-web.server.component
  (:require [com.stuartsierra.component :as component]))

(defrecord ServerComponent [port]
  component/Lifecycle
  (start [this]
    (let [compiled-routes (get-in this
                                  [:com.adamtait.thefutureserver.system/handlers
                                   :compiled-routes])]
      compiled-routes))
  (stop [this]))

(defn new-component [port]
  (component/using
    (->ServerComponent port)
    [:com.adamtait.thefutureserver.system/handlers]))
