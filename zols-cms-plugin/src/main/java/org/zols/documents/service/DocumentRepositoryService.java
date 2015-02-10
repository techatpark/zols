/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this document file, choose Tools | Documents
 * and open the document in the editor.
 */
package org.zols.documents.service;

import java.util.List;
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zols.datatore.exception.DataStoreException;
import org.zols.documents.domain.DocumentRepository;

/**
 *
 * @author sathish_ku
 */
@Service
public class DocumentRepositoryService {

    private static final Logger LOGGER = getLogger(DocumentRepositoryService.class);

    @Autowired
    private DataStore dataStore;

    /**
     * Creates a new DocumentRepository with given Object
     *
     * @param documentRepository Object to be Create
     * @return created DocumentRepository object
     */
    public DocumentRepository create(DocumentRepository documentRepository) throws DataStoreException {
        DocumentRepository createdDocumentRepository = null;
        if (documentRepository != null) {
            createdDocumentRepository = dataStore.create(DocumentRepository.class, documentRepository);
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
    public DocumentRepository read(String documentRepositoryName) throws DataStoreException {
        LOGGER.info("Reading Document Repository {}", documentRepositoryName);
        return dataStore.read(DocumentRepository.class, documentRepositoryName);
    }

    /**
     * Update a DocumentRepository with given Object
     *
     * @param documentRepository Object to be update
     * @return status of the update operation
     */
    public Boolean update(DocumentRepository documentRepository) throws DataStoreException {
        Boolean updated = false;
        if (documentRepository != null) {
            LOGGER.info("Updating Document Repository {}", documentRepository);
            updated = dataStore.update(documentRepository);
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
        return dataStore.delete(DocumentRepository.class, documentRepositoryName);
    }

    /**
     * List all DocumentRepositories
     *
     * @return list of Document Repositories
     */
    public List<DocumentRepository> list() throws DataStoreException {
        LOGGER.info("Getting DocumentRepositories ");
        return dataStore.list(DocumentRepository.class);
    }
}
