package org.zols.documentmanager;

import org.zols.datastore.DataStore;
import org.zols.documentmanager.domain.DocumentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DocumentStorageManager {

    @Autowired
    private DataStore dataStore;

    /**
     * Add a new document storage
     * @param documentStorage to be added
     * @return an instance of the new document storage
     */
    public DocumentStorage add(DocumentStorage documentStorage) {
        return dataStore.create(documentStorage, DocumentStorage.class);
    }

    /**
     * Update a document storage.
     * @param documentStorage to be updated.
     */
    public void update(DocumentStorage documentStorage) {
        dataStore.update(documentStorage, DocumentStorage.class);
    }

    /**
     * Delete a document storage from the system
     * @param documentStorageName name of the document storage to be deleted.
     */
    public void delete(String documentStorageName) {
        dataStore.delete(documentStorageName, DocumentStorage.class);
    }

    /**
     * Get a document storage by its name
     * @param documentStorageName name of the document storage.
     * @return the document storage.
     */
    public DocumentStorage get(String documentStorageName) {
        return dataStore.read(documentStorageName, DocumentStorage.class);
    }

    /**
     * List the document storages in the system
     * @param page
     * @return 
     */
    public Page<DocumentStorage> list(Pageable page) {
        return dataStore.list(page, DocumentStorage.class);
    }

}
