(ns com.tamandadam.merrymaking-web.handlers.rsvp
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [clojure.pprint]
            [com.stuartsierra.component :as component]
            [ring.util.response :as response]
            [com.tamandadam.merrymaking-web.mail :refer [send-new-rsvp]])
  (:import
   (com.google.gcloud.datastore DateTime Entity Key Query StringValue Transaction)
   #_(com.tamandadam.merrymaking_web GoogleDatastore)))

;; ------------------------------------------------
;; ## Google Cloud Datastore

#_(defn add-rsvp-entity
  [datastore params]
  (let [k (.allocateId datastore
                       (.newKey datastore
                                "com.tamandadam.merrymaking_web/rsvp"))
        
        rsvp-entity (.. (.builder Entity k)
                        (.set "names" (:names params))
                        (.set "created" (.now DateTime))
                        (.build))]
    (.put datastore rsvp-entity)
    k))

#_(defn add-test-entity [names]
  (let [ds (GoogleDatastore.)]
    (add-rsvp-entity ds {:names names})))


;; ------------------------------------------------
;; ## Submit

(defn hashmap-to-string [m] 
  (let [w (java.io.StringWriter.)]
    (clojure.pprint/pprint m w)
    (.toString w)))

(defn log-request [request]
  (println "request details /")
  (clojure.pprint/pprint request)
  (println "/")

  (log/info (str "Recieved request to submit RSVP form: /" (pr-str request) "/")))

(defn send-email [request]
  (let [name "Tam"
        msg (hashmap-to-string request)]
   (send-new-rsvp name msg)))

(defn submit
  "for submitting the RSVP form"
  [component request]

  (log-request request)
  (send-email request)
  
  {:status 200
   :headers {}
   :body "Thanks for playing!"})


;; ------------------------------------------------
;; ## Component API

(defrecord BaseAPI [])

(defn new-component []
  (->BaseAPI))
