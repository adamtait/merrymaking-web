(ns dev)

;; ------------------------------------------------
;; ## HTML page header

(def html-doctype-path "com/tamandadam/merrymaking_web/html_doctype.html")

(defn html-doctype-strs []
  (slurp
   (io/file
    (io/resource html-doctype-path))))


;; ------------------------------------------------
;; Test loading Hiccup from EDN

(defn collapse-strs
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

(def header-src-path "com/tamandadam/merrymaking_web/header.edn")
(def index-src-path "com/tamandadam/merrymaking_web/index.edn")

(defn load-src-as-hiccup [path]
  (edn/read-string
   (slurp
    (io/resource path))))

(defn index []
  (let [header-src (load-src-as-hiccup header-src-path)
        body-src (load-src-as-hiccup index-src-path)]
    [(compile-html header-src)
     (compile-html body-src)]))
