(ns com.tamandadam.merrymaking-web.handlers
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [compojure.core :as compojure]
            [compojure.handler]
            [compojure.route]
            [com.stuartsierra.component :as component]
            [com.tamandadam.merrymaking-web.handlers.middleware :as middleware]))

(def routes-file-name
  "com/tamandadam/merrymaking_web/routes.edn")

(defn load-routes []
  (clojure.edn/read-string (slurp (io/resource routes-file-name))))

(defn ^:private build-handler
  [route-m]
  {:pre [(symbol? (:namespace route-m))]}
  (let [action-sym (-> route-m :action name symbol)]
    (ns-resolve
     (:namespace route-m)
     action-sym)))

(defn ^:private build-path
  [route-m]
  (let [base-path (:base-path route-m)]
    (if-let [path (:path route-m)]
      (str base-path "/" path)  ;; path override
      (if-let [http-action (name (:action route-m))]
        (str base-path "/" http-action)
        (str base-path)    ;; default route
        ))))

(defn ^:private build-route
  [component route-m]
  (let [method-k (get route-m :method)
        path-str (build-path route-m)
        path-param-binding (mapv
                            (comp symbol :name)
                            (get route-m :path-params))
        handler (build-handler route-m)]

    (cond
      (= :post method-k)
      (compojure/POST
       path-str path-param-binding
       #(handler component %))
      
      (= :delete method-k)
      (compojure/DELETE
       path-str path-param-binding
       #(handler component %))

      :else
      (compojure/GET
       path-str path-param-binding
       #(handler component %)))))

(defn ^:private build-routes-for-component
  [system component-routes-m]
  {:pre [(map? component-routes-m)]}
  (let [component (get system (:component component-routes-m))
        namespace (:namespace component-routes-m)
        base-path (get component-routes-m :base-path "")]
    (->> (:routes component-routes-m)
         (map #(dissoc % :component))
         (map #(assoc % :base-path base-path))
         (map #(assoc % :namespace namespace))
         (map #(update-in % [:path-params]
                          concat (:path-params component-routes-m)))
         (map (partial build-route component)))))

(defn ^:private build-routes
  [component routes]
  (flatten
   (map
    #(build-routes-for-component component %)
    routes)))

(defn compojure-wrap [middleware routes]
  (compojure.handler/site
   (apply
    compojure/routes
    (concat
     routes
     middleware))))


;; ------------------------------------------------
;; ## Component

(defrecord Handlers []
  component/Lifecycle

  (start [this]
    (let [route-maps (build-routes this (load-routes))
          routes (compojure-wrap middleware/routes route-maps)]
      (assoc this
             :compiled-routes routes))))

(defn new-component []
  (component/using
    (->Handlers)
    [:com.tamandadam.merrymaking-web.server/base]))
