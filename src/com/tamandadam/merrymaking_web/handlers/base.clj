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

(defn index [component request]
  (let [html-src (load-hiccup-src index-src-path)]
    (str
     (html-doctype-strs)
     html-src)))

;; ------------------------------------------------
;; ## index path, using Hiccup

#_(defn index [component request]
  (html5
   [:head
    [:title "Merrymaking with Tam & Adam"]]

   [:body
    [:div
     [:h1 "Merrymaking with Tam & Adam"]]
    [:div
     [:h3 "September 17th, 2016"]]
    [:div
     [:h3 "San Francisco, California"]]

    [:div "placeholder"]


    [:div "footer"]
    ]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## Component API

(defrecord BaseAPI [])

(defn new-component []
  (->BaseAPI))
