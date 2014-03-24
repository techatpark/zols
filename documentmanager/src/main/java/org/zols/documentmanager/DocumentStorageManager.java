/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documentmanager;

import org.zols.datastore.DataStore;
import org.zols.documentmanager.domain.DocumentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Navin.
 */
@Service
public class DocumentStorageManager {

    @Autowired
    private DataStore dataStore;

    public DocumentStorage add(DocumentStorage documentStorage) {
        return dataStore.create(documentStorage, DocumentStorage.class);
    }

    public void update(DocumentStorage documentStorage) {
        dataStore.update(documentStorage, DocumentStorage.class);
    }

    public void delete(String documentStorageName) {
        dataStore.delete(documentStorageName, DocumentStorage.class);
    }

    public DocumentStorage get(String documentStorageName) {
        return dataStore.read(documentStorageName, DocumentStorage.class);
    }

    public Page<DocumentStorage> list(Pageable page) {
        return dataStore.list(page, DocumentStorage.class);
    }

}
