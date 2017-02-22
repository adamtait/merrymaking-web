package com.tamandadam.merrymaking_web;

import com.google.gcloud.datastore;
import com.google.gcloud.datastore.Datastore;
import com.google.gcloud.datastore.DatastoreOptions;
import com.google.gcloud.datastore.Entity;
import com.google.gcloud.datastore.Key;
import com.google.gcloud.datastore.KeyFactory;


public class GoogleDatastore {

    private Datastore service;
    private KeyFactory keyFactory;

    public GoogleDatastore() {
        this.service = DatastoreOptions.defaultInstance().service();
        this.keyFactory = this.service.newKeyFactory();
    }

    public Key newKey(String type) {
        return this.keyFactory.kind(type).newKey(type);
    }
    
}
