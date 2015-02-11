/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.templates.service;

import java.util.List;
import java.util.Map;
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
@Service
public class PageService {

    private static final Logger LOGGER = getLogger(PageService.class);

    @Autowired
    private DataService dataService;
    
    @Autowired
    private LinkService linkService;
    
    @Autowired
    private DataStore dataStore;

    @Autowired
    private TemplateService templateService;
    /**
     * Creates a new Page with given Object
     *
     * @param pageRequest Object to be Create
     * @return created Page object
     */
    public Page create(PageRequest pageRequest) throws DataStoreException {
        Page createdPage = null;
        if (pageRequest != null) {
            
            Map<String,Object> createdData = dataService.create(pageRequest.getTemplate().getDataType(), pageRequest.getData());
            
            String dataName = createdData.get(dataService.getIdField(pageRequest.getTemplate().getDataType())).toString();
            
            Page page = new Page();
            page.setDataName(dataName);
            page.setTemplateName(pageRequest.getTemplate().getName());
            
            createdPage = dataStore.create(Page.class, page);
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
    public PageRequest readRequest(String pageName) throws DataStoreException {
        LOGGER.info("Reading Page Request {}", pageName);
        PageRequest pageRequest;
        Page page = read(pageName);
        if(page != null) {
            pageRequest = new PageRequest();
            Template template = templateService.read(page.getTemplateName());
            pageRequest.setTemplate(template);
            pageRequest.setData(dataService.read(template.getDataType(), page.getDataName()));
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
    public Page read(String pageName) throws DataStoreException {
        LOGGER.info("Reading Page {}", pageName);
        return dataStore.read(Page.class, pageName);
    }

    /**
     * Update a Page with given Object
     *
     * @param page Object to be update
     * @return returns the status of the Update Operation
     */
    public Boolean update(Page page) throws DataStoreException {
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
     * @return status of the delete operation
     */
    public Boolean delete(String pageName) throws DataStoreException {
        LOGGER.info("Deleting Page {}", pageName);
        return dataStore.delete(Page.class, pageName);
    }

    /**
     * List all Pages
     *
     * @return list of all pages
     */
    public List<Page> list() throws DataStoreException {
        LOGGER.info("Getting Pages ");
        return dataStore.list(Page.class);
    }
    
 
}
