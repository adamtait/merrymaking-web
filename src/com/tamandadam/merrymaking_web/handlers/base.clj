(ns com.tamandadam.merrymaking-web.handlers.base
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [hiccup.compiler :refer [compile-html]]
            [hiccup.page :refer [html5]]))

;; ------------------------------------------------
;; ## Helpers

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


;; ------------------------------------------------
;; ## HTML page header

(def html-doctype-path "com/tamandadam/merrymaking_web/html_doctype.html")

(defn html-doctype-strs []
  (slurp
   (io/file
    (io/resource html-doctype-path))))


;; ------------------------------------------------
;; ## index path

(def index-src-path "com/tamandadam/merrymaking_web/index.html")

(defn load-hiccup-src [path]
  (slurp
   (io/resource path)))

(defn example [component request]
  (let [html-src (load-hiccup-src index-src-path)]
    (str
     (html-doctype-strs)
     html-src)))


;; ------------------------------------------------
;; ## Template

(def template-src-path "com/tamandadam/merrymaking_web/template.html")

(defn index [component request]
  (load-hiccup-src template-src-path))


;; ------------------------------------------------
;; ## Component API

(defrecord BaseAPI [])

(defn new-component []
  (->BaseAPI))
