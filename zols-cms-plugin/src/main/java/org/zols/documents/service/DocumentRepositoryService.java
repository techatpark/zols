/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this document file, choose Tools | Documents
 * and open the document in the editor.
 */
package org.zols.documents.service;

import java.util.List;
import java.util.Optional;
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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
    @Secured("ROLE_ADMIN")
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
    @Secured("ROLE_ADMIN")
    public Optional<DocumentRepository> read(String documentRepositoryName) throws DataStoreException {
        LOGGER.info("Reading Document Repository {}", documentRepositoryName);
        return dataStore.getObjectManager(DocumentRepository.class).read(documentRepositoryName);
    }

    /**
     * Update a DocumentRepository with given Object
     *
     * @param documentRepository Object to be update
     * @return status of the update operation
     */
    @Secured("ROLE_ADMIN")
    public DocumentRepository update(DocumentRepository documentRepository) throws DataStoreException {
        DocumentRepository updated = null;
        if (documentRepository != null) {
            LOGGER.info("Updating Document Repository {}", documentRepository);
            updated = dataStore.getObjectManager(DocumentRepository.class).update(documentRepository, documentRepository.getName());
        }
        return updated;
    }

    /**
     * Delete a DocumentRepository with given String
     *
     * @param documentRepositoryName String to be delete
     * @return status of the delete operation
     */
    @Secured("ROLE_ADMIN")
    public Boolean delete(String documentRepositoryName) throws DataStoreException {
        LOGGER.info("Deleting Document Repository {}", documentRepositoryName);
        return dataStore.getObjectManager(DocumentRepository.class).delete( documentRepositoryName);
    }

    /**
     * List all DocumentRepositories
     *
     * @return list of Document Repositories
     */
    @Secured("ROLE_ADMIN")
    public List<DocumentRepository> list() throws DataStoreException {
        LOGGER.info("Getting DocumentRepositories ");
        return dataStore.getObjectManager(DocumentRepository.class).list();
    }
}
