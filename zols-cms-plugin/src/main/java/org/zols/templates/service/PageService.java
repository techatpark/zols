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
import org.zols.templates.domain.Page;

/**
 *
 * @author sathish_ku
 */
@Service
public class PageService {

    private static final Logger LOGGER = getLogger(PageService.class);

    @Autowired
    private DataStore dataStore;

    /**
     * Creates a new Page with given Object
     *
     * @param page Object to be Create
     * @return created Page object
     */
    public Page create(Page page) {
        Page createdPage = null;
        if (page != null) {
            createdPage = dataStore.create(Page.class, page);
            LOGGER.info("Created Page {}", createdPage.getName());
        }
        return createdPage;
    }

    /**
     * Get the Page with given String
     *
     * @param pageName String to be Search
     * @return searched Page
     */
    public Page read(String pageName) {
        LOGGER.info("Reading Page {}", pageName);
        return dataStore.read(Page.class, pageName);
    }

    /**
     * Update a Page with given Object
     *
     * @param page Object to be update
     * @return
     */
    public Boolean update(Page page) {
        Boolean updated = false;
        if (page != null) {
            LOGGER.info("Updating Page {}", page);
            updated = dataStore.update(page);
        }
        return updated;
    }

    /**
     * Delete a Page with given String
     *
     * @param pageName String to be delete
     * @return
     */
    public Boolean delete(String pageName) {
        LOGGER.info("Deleting Page {}", pageName);
        return dataStore.delete(Page.class, pageName);
    }

    /**
     * List all Pages
     *
     * @return
     */
    public List<Page> list() {
        LOGGER.info("Getting Pages ");
        return dataStore.list(Page.class);
    }
    
 
}
