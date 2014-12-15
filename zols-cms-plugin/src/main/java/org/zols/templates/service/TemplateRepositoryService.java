/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.templates.service;

import java.util.List;
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zols.datastore.query.Filter;
import static org.zols.datastore.query.Filter.Operator.EQUALS;
import org.zols.datastore.query.Query;
import org.zols.templates.domain.Template;
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
     * @return status of the Update Operation
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
     * @return status of the Delete Operation
     */
    public Boolean delete(String templateRepositoryName) {
        LOGGER.info("Deleting Template Repository {}", templateRepositoryName);
        return dataStore.delete(TemplateRepository.class, templateRepositoryName);
    }

    /**
     * List all TemplateRepositories
     *
     * @return list of all Template Repositories
     */
    public List<TemplateRepository> list() {
        LOGGER.info("Getting TemplateRepositories ");
        return dataStore.list(TemplateRepository.class);
    }
    
    /**
     * List templates under given repository
     * @param repositoryName name of the repository
     * @return list of templates
     */
    public List<Template> listTemplates(String repositoryName) {
        LOGGER.info("Getting templates of repository  {}", repositoryName);
        Query query = new Query();
        query.addFilter(new Filter<>("repositoryName", EQUALS, repositoryName));
        return dataStore.list(Template.class, query);
    }
}
