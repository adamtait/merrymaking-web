(ns com.tamandadam.merrymaking-web.datastore.component
  (:require [com.stuartsierra.component :as component])
  (:import [com.google.api.client.googleapis.auth.oauth2 GoogleCredential]))

(def credential (.getApplicationDefault GoogleCredential))


