(ns com.tamandadam.merrymaking-web.log
  (:import
   (java.util.logging Logger)))

(defmacro get-logger []
  `(let [current-ns-name# (str (ns-name *ns*))]
    (Logger/getLogger current-ns-name#)))

(defmacro log [^String message]
  `(let [logger# (get-logger)]
    (.info logger# ~message)))

(defmacro log-error [^Throwable exception ^String message]
  `(let [logger# (get-logger)]
     (.severe logger#
              (str ~message "\n"
                   (.printStackTrace ~exception)))))
