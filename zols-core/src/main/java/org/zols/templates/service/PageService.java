/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.templates.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.zols.datastore.service.DataService;
import org.zols.datatore.exception.DataStoreException;
import org.zols.links.service.LinkService;
import org.zols.templates.domain.PageRequest;
import org.zols.templates.domain.Page;
import org.zols.templates.domain.Template;

/**
 *
 * @author sathish_ku
 */

public class PageService {

    private static final Logger LOGGER = getLogger(PageService.class);

    
    private final DataService dataService;
    
    
    private final LinkService linkService;
    
    
    private final DataStore dataStore;
    
    private final TemplateService templateService;

    public PageService(DataService dataService, LinkService linkService, DataStore dataStore, TemplateService templateService) {
        this.dataService = dataService;
        this.linkService = linkService;
        this.dataStore = dataStore;
        this.templateService = templateService;
    }
    
    
    
    /**
     * Creates a new Page with given Object
     *
     * @param pageRequest Object to be Create
     * @return created Page object
     */
    
    public Page create(PageRequest pageRequest,Locale loc) throws DataStoreException {
        Page createdPage = null;
        if (pageRequest != null) {
            
            Map<String,Object> createdData = dataService.create(pageRequest.getTemplate().getDataType(), pageRequest.getData(),loc);
            
            String dataName = createdData.get(dataService.getIdField(pageRequest.getTemplate().getDataType())).toString();
            
            Page page = new Page();
            page.setDataName(dataName);
            page.setTemplateName(pageRequest.getTemplate().getName());
            
            createdPage = dataStore.getObjectManager(Page.class).create(page);
            LOGGER.info("Created Page {}", createdPage.getName());
            
            linkService.linkUrl(pageRequest.getLinkName(), "/pages/"+createdPage.getName());
        }
        return createdPage;
    }
    
    /**
     * Get the Page Request with given String
     *
     * @param pageName String to be Search
     * @return searched Page
     */
    public PageRequest readRequest(String pageName,Locale loc) throws DataStoreException {
        LOGGER.info("Reading Page Request {}", pageName);
        PageRequest pageRequest;
        Optional<Page> page = read(pageName);
        if(page.isPresent()) {
            pageRequest = new PageRequest();
            Optional<Template> template = templateService.read(page.get().getTemplateName());
            pageRequest.setTemplate(template.get());
            //pageRequest.setData(dataService.read(template.get().getDataType(), loc).get());
            return pageRequest;
        }
        return null;
    }

    /**
     * Get the Page with given String
     *
     * @param pageName String to be Search
     * @return searched Page
     */
    public Optional<Page> read(String pageName) throws DataStoreException {
        LOGGER.info("Reading Page {}", pageName);
        return dataStore.getObjectManager(Page.class).read(new SimpleEntry("name", pageName));
    }

    /**
     * Update a Page with given Object
     *
     * @param page Object to be update
     * @return returns the status of the Update Operation
     */
    
    public Page update(Page page) throws DataStoreException {
        Page updated = null;
        if (page != null) {
            LOGGER.info("Updating Page {}", page);
            updated = dataStore.getObjectManager(Page.class).update(page,new SimpleEntry("name", page.getName()));
        }
        return updated;
    }

    /**
     * Delete a Page with given String
     *
     * @param pageName String to be delete
     * @return status of the delete operation
     */
    
    public Boolean delete(String pageName) throws DataStoreException {
        LOGGER.info("Deleting Page {}", pageName);
        return dataStore.getObjectManager(Page.class).delete(new SimpleEntry("name", pageName));
    }

    /**
     * List all Pages
     *
     * @return list of all pages
     */
    public List<Page> list() throws DataStoreException {
        LOGGER.info("Getting Pages ");
        return dataStore.getObjectManager(Page.class).list();
    }
    
 
}
