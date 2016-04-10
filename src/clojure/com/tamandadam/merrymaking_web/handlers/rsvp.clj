(ns com.tamandadam.merrymaking-web.handlers.rsvp
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint]
            [com.stuartsierra.component :as component]
            [ring.util.response :as response])
  (:import
   (com.google.gcloud.datastore Datastore DatastoreOptions DateTime Entity Key KeyFactory Query StringValue Transaction)))

;; ------------------------------------------------
;; ## Google Cloud Datastore

(defn datastore []
  (.service (DatastoreOptions/defaultInstance)))

(defn key-factory [datastore]
  (let [^KeyFactory kf (.newKeyFactory datastore)]
    (.kind
     ^KeyFactory kf
     "com.tamandadam.merrymaking-web/rsvp")))

(defn add-rsvp-entity [datastore key-factory params]
  (let [k (.allocateId datastore (.newKey key-factory))
        rsvp-entity (.. (.builder Entity k)
                        (.set "names" (:names params))
                        (.set "created" (.now DateTime))
                        (.build))]
    (.put datastore rsvp-entity)
    k))

(defn add-test-entity [names]
  (let [ds (datastore)
        kf (key-factory ds)]
    (add-rsvp-entity ds kf {:names names})))

;; ------------------------------------------------
;; ## Submit

(defn submit
  "for submitting the RSVP form"
  [component request]

  (println "request details /")
  (clojure.pprint/pprint request)
  (println "/")
  
  {:status 200
   :headers {}
   :body "Thanks for playing!"})


;; ------------------------------------------------
;; ## Component API

(defrecord BaseAPI [])

(defn new-component []
  (->BaseAPI))
