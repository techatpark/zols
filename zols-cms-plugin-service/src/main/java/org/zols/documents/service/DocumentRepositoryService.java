/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this document file, choose Tools | Documents
 * and open the document in the editor.
 */
package org.zols.documents.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.zols.datatore.exception.DataStoreException;
import org.zols.documents.domain.DocumentRepository;

/**
 *
 * @author sathish_ku
 */
public class DocumentRepositoryService {

    private static final Logger LOGGER = getLogger(DocumentRepositoryService.class);

    private final DataStore dataStore;

    public DocumentRepositoryService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Creates a new DocumentRepository with given Object
     *
     * @param documentRepository Object to be Create
     * @return created DocumentRepository object
     */
    public DocumentRepository create(DocumentRepository documentRepository) throws DataStoreException {
        DocumentRepository createdDocumentRepository = null;
        if (documentRepository != null) {
            createdDocumentRepository = dataStore.getObjectManager(DocumentRepository.class).create(documentRepository);
            LOGGER.info("Created Document Repository {}", createdDocumentRepository.getName());
        }
        return createdDocumentRepository;
    }

    /**
     * Get the DocumentRepository with given String
     *
     * @param documentRepositoryName String to be Search
     * @return searched DocumentRepository
     */
    public Optional<DocumentRepository> read(String documentRepositoryName) throws DataStoreException {
        LOGGER.info("Reading Document Repository {}", documentRepositoryName);
        return dataStore.getObjectManager(DocumentRepository.class).read(new SimpleEntry("name", documentRepositoryName));
    }

    /**
     * Update a DocumentRepository with given Object
     *
     * @param documentRepository Object to be update
     * @return status of the update operation
     */
    public DocumentRepository update(DocumentRepository documentRepository) throws DataStoreException {
        DocumentRepository updated = null;
        if (documentRepository != null) {
            LOGGER.info("Updating Document Repository {}", documentRepository);
            updated = dataStore.getObjectManager(DocumentRepository.class).update(documentRepository, new SimpleEntry("name", documentRepository.getName()));
        }
        return updated;
    }

    /**
     * Delete a DocumentRepository with given String
     *
     * @param documentRepositoryName String to be delete
     * @return status of the delete operation
     */
    public Boolean delete(String documentRepositoryName) throws DataStoreException {
        LOGGER.info("Deleting Document Repository {}", documentRepositoryName);
        return dataStore.getObjectManager(DocumentRepository.class).delete(new SimpleEntry("name", documentRepositoryName));
    }

    /**
     * List all DocumentRepositories
     *
     * @return list of Document Repositories
     */
    public List<DocumentRepository> list() throws DataStoreException {
        LOGGER.info("Getting DocumentRepositories ");
        return dataStore.getObjectManager(DocumentRepository.class).list();
    }
}
