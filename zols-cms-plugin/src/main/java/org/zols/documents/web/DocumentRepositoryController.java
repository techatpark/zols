/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this document file, choose Tools | Documents
 * and open the document in the editor.
 */
package org.zols.documents.web;

import java.util.List;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zols.datatore.exception.DataStoreException;
import org.zols.documents.domain.DocumentRepository;
import org.zols.documents.service.DocumentRepositoryService;


/**
 *
 * @author sathish_ku
 */
@RestController
@RequestMapping(value = "/api/document_repositories")
public class DocumentRepositoryController {

    private static final Logger LOGGER = getLogger(DocumentRepositoryController.class);

    @Autowired
    private DocumentRepositoryService documentRepositoryService;

    @RequestMapping(method = POST)
    public DocumentRepository create(@RequestBody DocumentRepository documentRepository) throws DataStoreException {
        LOGGER.info("Creating new documentRepositories {}", documentRepository.getName());
        return documentRepositoryService.create(documentRepository);
    }

    @RequestMapping(value = "/{name}", method = GET)
    public DocumentRepository read(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Getting documentRepository ", name);
        return documentRepositoryService.read(name);
    }

    @RequestMapping(value = "/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody DocumentRepository documentRepository) throws DataStoreException {
        if (name.equals(documentRepository.getName())) {
            LOGGER.info("Updating documentRepositories with id {} with {}", name, documentRepository);
            documentRepositoryService.update(documentRepository);
        }
    }

    @RequestMapping(value = "/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Deleting documentRepositories with id {}", name);
        documentRepositoryService.delete(name);
    }

    @RequestMapping(method = GET)
    public List<DocumentRepository> list() throws DataStoreException {
        LOGGER.info("Getting DocumentRepositories ");
        return documentRepositoryService.list();
    }
}
