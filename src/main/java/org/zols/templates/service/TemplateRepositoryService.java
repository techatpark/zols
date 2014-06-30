/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.templates.service;

import java.util.List;
import org.jodel.store.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zols.templates.domain.TemplateRepository;

/**
 *
 * @author sathish_ku
 */
@Service
public class TemplateRepositoryService {

    private static final Logger LOGGER = getLogger(TemplateRepositoryService.class);

    @Autowired
    private DataStore dataStore;

    /**
     * Creates a new TemplateRepository with given Object
     *
     * @param templateRepository Object to be Create
     * @return created TemplateRepository object
     */
    public TemplateRepository create(TemplateRepository templateRepository) {
        TemplateRepository createdTemplateRepository = null;
        if (templateRepository != null) {
            createdTemplateRepository = dataStore.create(TemplateRepository.class, templateRepository);
            LOGGER.info("Created Template Repository {}", createdTemplateRepository.getName());
        }
        return createdTemplateRepository;
    }

    /**
     * Get the TemplateRepository with given String
     *
     * @param templateRepositoryName String to be Search
     * @return searched TemplateRepository
     */
    public TemplateRepository read(String templateRepositoryName) {
        LOGGER.info("Reading Template Repository {}", templateRepositoryName);
        return dataStore.read(TemplateRepository.class, templateRepositoryName);
    }

    /**
     * Update a TemplateRepository with given Object
     *
     * @param templateRepository Object to be update
     * @return
     */
    public Boolean update(TemplateRepository templateRepository) {
        Boolean updated = false;
        if (templateRepository != null) {
            LOGGER.info("Updating Template Repository {}", templateRepository);
            updated = dataStore.update(templateRepository);
        }
        return updated;
    }

    /**
     * Delete a TemplateRepository with given String
     *
     * @param templateRepositoryName String to be delete
     * @return
     */
    public Boolean delete(String templateRepositoryName) {
        LOGGER.info("Deleting Template Repository {}", templateRepositoryName);
        return dataStore.delete(TemplateRepository.class, templateRepositoryName);
    }

    /**
     * List all TemplateRepositories
     *
     * @return
     */
    public List<TemplateRepository> list() {
        LOGGER.info("Getting TemplateRepositories ");
        return dataStore.list(TemplateRepository.class);
    }
}
