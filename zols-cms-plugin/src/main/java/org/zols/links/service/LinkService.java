/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zols.datastore.DataStore;
import org.zols.datastore.query.Filter;
import org.zols.datastore.query.Query;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import static org.zols.datastore.query.Filter.Operator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.zols.datatore.exception.DataStoreException;
import org.zols.links.domain.LinkGroup;
import org.zols.links.domain.Link;
import org.zols.links.provider.LinkProvider;

@Service
public class LinkService {

    private static final Logger LOGGER = getLogger(LinkService.class);
    
    
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private LinkGroupService linkGroupService;

    @Autowired
    private DataStore dataStore;

    /**
     * Creates a new Link with given Object
     *
     * @param link Object to be Create
     * @return created Link object
     */
    public Link create(Link link) throws DataStoreException {
        Link createdLink = null;
        if (link != null) {

            createdLink = dataStore.create(link);
            LOGGER.info("Created Link {}", createdLink.getName());

            if (link.getTargetUrl() == null || link.getTargetUrl().trim().length() == 0) {
                LOGGER.info("Setting Default Link URL {}", createdLink.getName());
                createdLink.setTargetUrl("/create_page/" + createdLink.getName());
                update(createdLink);
            }
        }
        return createdLink;
    }

    /**
     * Get the Link with given String
     *
     * @param linkName String to be Search
     * @return Link
     */
    public Link read(String linkName) throws DataStoreException {
        LOGGER.info("Reading Link {}", linkName);
        return dataStore.read(Link.class, linkName);
    }

    /**
     * Update a Link with given Object
     *
     * @param link Object to be update
     * @return status of the Update operation
     */
    public Link update(Link link) throws DataStoreException {
        Link updated = null;
        if (link != null) {
            LOGGER.info("Updating Link {}", link);
            updated = dataStore.update(link,link.getName());
        }
        return updated;
    }

    /**
     * Delete a Link with given String
     *
     * @param linkName String to be delete
     * @return status of the Delete operation
     */
    public Boolean delete(String linkName) throws DataStoreException {
        LOGGER.info("Deleting Link {}", linkName);
        List<Link> children = listChildren(linkName);
        if (children != null) {
            for (Link child : children) {
                delete(child.getName());
            }
        }
        return dataStore.delete(Link.class, linkName);
    }

    /**
     * Get the list of link with given Parent name
     *
     * @param groupName String to be search
     * @return list of links
     */
    public Boolean deleteLinksUnder(String groupName) throws DataStoreException {
        LOGGER.info("Deleting links under group {}", groupName);
        Query query = new Query();
        query.addFilter(new Filter<>("groupName", EQUALS, groupName));
        return dataStore.delete(Link.class, query);
    }

    /**
     * Get the list of link with given Parent name
     *
     * @param parentLinkName String to be search
     * @return list of links
     */
    public List<Link> listChildren(String parentLinkName) throws DataStoreException {
        LOGGER.info("Getting children of link {}", parentLinkName);
        Query query = new Query();
        query.addFilter(new Filter<>("parentLinkName", EQUALS, parentLinkName));
        return dataStore.list(Link.class, query);
    }

    /**
     *
     * @return list of links
     */
    public List<Link> list() throws DataStoreException {
        LOGGER.info("Getting Links ");
        return dataStore.list(Link.class);
    }

    /**
     *
     * @return list of application links
     */
    public Map<String, List<Link>> getApplicationLinks() throws DataStoreException {
        List<LinkGroup> categories = linkGroupService.list();
        if (categories != null) {
            Map<String, List<Link>> applicationLinks = new HashMap<>(categories.size());
            List<Link> firstlevelLinks;
            for (LinkGroup group : categories) {
                firstlevelLinks = linkGroupService.getFirstLevelLinks(group.getName());
                if (firstlevelLinks != null) {
                    for (Link link : firstlevelLinks) {
                        link.setChildren(listChildren(link.getName()));
                    }
                    applicationLinks.put(group.getName(), firstlevelLinks);
                }

            }

            Map<String, LinkProvider> beansMap = applicationContext.getBeansOfType(LinkProvider.class);

            for (Map.Entry<String, LinkProvider> entry : beansMap.entrySet()) {
                applicationLinks.put(entry.getKey(), entry.getValue().getLinks());
            }
            return applicationLinks;
        }

        return null;
    }

    /**
     * Update a Link with given Url
     *
     * @param linkName name of the Link
     * @param url URL to be linked
     * @return status of the Update
     */
    public Link linkUrl(String linkName, String url) throws DataStoreException {
        Link updated = null;
        if (linkName != null) {
            LOGGER.info("Updating Link with url {}", linkName);
            Link link = read(linkName);
            link.setTargetUrl(url);
            updated = update(link);
        }
        return updated;
    }

}
