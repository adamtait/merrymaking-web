(ns com.tamandadam.merrymaking-web.handlers.base
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [hiccup.compiler :refer [compile-html]]
            [hiccup.page :refer [html5]]))

;; ------------------------------------------------
;; ## Template

(def template-src-path "com/tamandadam/merrymaking_web/index.html")

(defn load-hiccup-src [path]
  (slurp
   (io/resource path)))

(defn index
  "cental landing place for the top-level page"
  [component request]
  (load-hiccup-src template-src-path))


;; ------------------------------------------------
;; ## Component API

(defrecord BaseAPI [])

(defn new-component []
  (->BaseAPI))
