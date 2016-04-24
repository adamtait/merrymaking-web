(ns com.tamandadam.merrymaking-web.handlers.rsvp
  (:require [clojure.edn :as edn]
            [clojure.pprint]
            [com.stuartsierra.component :as component]
            #_[ring.util.response :as response]
            [com.tamandadam.merrymaking-web.mail :refer [send-new-rsvp]]
            [com.tamandadam.merrymaking-web.log :refer [log log-error]])
  (:import
   #_(com.google.gcloud.datastore DateTime Entity Key Query StringValue Transaction)
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

(defn first-senders-name
  "Get the first of the sender's name. Handles both cases that only
  one person is responding or many people are."
  [data]
  (let [n (:name data)]
    (if (coll? n)
      (first n)
      n)))

(defn log-request [data]
  (log
   (str
    "Recieved request to submit RSVP form: \n"
    (hashmap-to-string data))))

(defn send-email [data]
  (let [name (first-senders-name data)
        msg (hashmap-to-string data)]
    (send-new-rsvp name msg)))

(defn submit
  "called upon submitting the RSVP form"
  [component request]
  
  (let [data (:params request)]
    (log-request data)
    (send-email data)
    
    {:status 200
     :headers {}
     :body (str "Thanks for your RSVP!")}))


;; ------------------------------------------------
;; ## Component API

(defrecord BaseAPI [])

(defn new-component []
  (->BaseAPI))
