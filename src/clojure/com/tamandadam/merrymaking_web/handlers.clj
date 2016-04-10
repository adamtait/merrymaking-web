(ns com.tamandadam.merrymaking-web.handlers
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint]
            [compojure.core :as compojure]
            [compojure.handler]
            [compojure.route]
            [com.stuartsierra.component :as component]
            [com.tamandadam.merrymaking-web.handlers.middleware :as middleware]))

;; ------------------------------------------------
;; ## Load Routes configuration

(def routes-file-name
  "com/tamandadam/merrymaking_web/routes.edn")

(defn load-routes []
  (clojure.edn/read-string (slurp (io/resource routes-file-name))))


;; ------------------------------------------------
;; ## Helpers

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


;; ------------------------------------------------
;; ## Development Helpers

(defn ^:private print-routes
  [route-maps]
  (doseq [r route-maps]
    (println "---- new route ----")
    (clojure.pprint/pprint r)
    (println "full path: " (build-path r))
    (println)))


;; ------------------------------------------------
;; ## Route Map Building

(defn ^:private compojure-route-maps
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

(defn ^:private route-maps
  [component-routes-m]
  {:pre [(map? component-routes-m)]}
  (let [namespace (:namespace component-routes-m)
        base-path (get component-routes-m :base-path "")]
    (->>
     (:routes component-routes-m)
     (map #(dissoc % :component))
     (map #(assoc % :base-path base-path))
     (map #(assoc % :namespace namespace))
     (map
      #(update-in % [:path-params]
                  concat (:path-params component-routes-m))))))

(defn ^:private build-routes-for-component
  [system component-routes-m]
  (let [component (get system (:component component-routes-m))
        route-maps (route-maps component-routes-m)]
    (print-routes route-maps)

    (map
     #(compojure-route-maps component %)
     route-maps)))

(defn ^:private routes
  [component routes]
  (flatten
   (map
    #(build-routes-for-component component %)
    routes)))

(defn ^:private compojure-wrap
  [middleware routes]
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
    (let [route-maps (routes this (load-routes))
          routes (compojure-wrap middleware/routes route-maps)]
      (assoc this
             :compiled-routes routes))))

(defn new-component []
  (component/using
    (->Handlers)
    [:com.tamandadam.merrymaking-web.server/base]))
