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
import org.zols.datastore.query.Query;
import org.zols.templates.domain.Template;

/**
 *
 * @author sathish_ku
 */
@Service
public class TemplateService {

    private static final Logger LOGGER = getLogger(TemplateService.class);

    @Autowired
    private DataStore dataStore;

    /**
     * Creates a new Template with given Object
     *
     * @param template Object to be Create
     * @return created Template object
     */
    public Template create(Template template) {
        Template createdTemplate = null;
        if (template != null) {
            createdTemplate = dataStore.create(Template.class, template);
            LOGGER.info("Created Template  {}", createdTemplate.getName());
        }
        return createdTemplate;
    }

    /**
     * Get the Template with given String
     *
     * @param templateName String to be Search
     * @return searched Template
     */
    public Template read(String templateName) {
        LOGGER.info("Reading Template  {}", templateName);
        return dataStore.read(Template.class, templateName);
    }

    /**
     * Update a Template with given Object
     *
     * @param template Object to be update
     * @return status of the update Operation
     */
    public Boolean update(Template template) {
        Boolean updated = false;
        if (template != null) {
            LOGGER.info("Updating Template  {}", template);
            updated = dataStore.update(template);
        }
        return updated;
    }

    /**
     * Delete a Template with given String
     *
     * @param templateName String to be delete
     * @return status of the Delete Operation
     */
    public Boolean delete(String templateName) {
        LOGGER.info("Deleting Template  {}", templateName);
        return dataStore.delete(Template.class, templateName);
    }

    /**
     * List all Templates
     *
     * @return list of all the Templates
     */
    public List<Template> list() {
        LOGGER.info("Getting Templates ");
        return dataStore.list(Template.class);
    }
}
