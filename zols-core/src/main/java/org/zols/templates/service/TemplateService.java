/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.templates.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.zols.datatore.exception.DataStoreException;
import org.zols.templates.domain.Template;

/**
 *
 * @author sathish_ku
 */

public class TemplateService {

    private static final Logger LOGGER = getLogger(TemplateService.class);

    
    private final DataStore dataStore;

    public TemplateService(DataStore dataStore) {
        this.dataStore = dataStore;
    }
    
    

    /**
     * Creates a new Template with given Object
     *
     * @param template Object to be Create
     * @return created Template object
     */
    
    public Template create(Template template) throws DataStoreException {
        Template createdTemplate = null;
        if (template != null) {
            createdTemplate = dataStore.getObjectManager(Template.class).create(template);
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
    public Optional<Template> read(String templateName) throws DataStoreException {
        LOGGER.info("Reading Template  {}", templateName);
        return dataStore.getObjectManager(Template.class).read(new SimpleEntry("name", templateName));
    }

    /**
     * Update a Template with given Object
     *
     * @param template Object to be update
     * @return status of the update Operation
     */
    
    public Template update(Template template) throws DataStoreException {
        Template updated = null;
        if (template != null) {
            LOGGER.info("Updating Template  {}", template);
            updated = dataStore.getObjectManager(Template.class).update(template,new SimpleEntry("name", template.getName()));
        }
        return updated;
    }

    /**
     * Delete a Template with given String
     *
     * @param templateName String to be delete
     * @return status of the Delete Operation
     */
    
    public Boolean delete(String templateName) throws DataStoreException {
        LOGGER.info("Deleting Template  {}", templateName);
        return dataStore.getObjectManager(Template.class).delete( new SimpleEntry("name", templateName));
    }

    /**
     * List all Templates
     *
     * @return list of all the Templates
     */
    public List<Template> list() throws DataStoreException {
        LOGGER.info("Getting Templates ");
        return dataStore.getObjectManager(Template.class).list();
    }
}
