/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.zols.datastore.DataStore;
import org.zols.datastore.query.Filter;
import static org.zols.datastore.query.Filter.Operator.EQUALS;
import static org.zols.datastore.query.Filter.Operator.IS_NULL;
import org.zols.datastore.query.Query;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zols.datatore.exception.DataStoreException;
import org.zols.links.domain.LinkGroup;
import org.zols.links.domain.Link;

@Service
public class LinkGroupService {

    private static final Logger LOGGER = getLogger(LinkGroupService.class);

    @Autowired
    private DataStore dataStore;

    @Autowired
    private LinkService linkService;

    /**
     * Creates a new Group with given Object
     *
     * @param group Object to be Create
     * @return created Group object
     */
    public LinkGroup create(@Valid LinkGroup group) throws DataStoreException {
        LinkGroup createdGroup = null;
        if (group != null) {
            createdGroup = dataStore.create(group);
            LOGGER.info("Created Group {}", createdGroup.getName());
        }
        return createdGroup;
    }

    /**
     * Get the Group with given String
     *
     * @param groupName String to be Search
     * @return searched Group
     */
    public LinkGroup read(String groupName) throws DataStoreException {
        LOGGER.info("Reading Group {}", groupName);
        return dataStore.read(LinkGroup.class, groupName);
    }

    /**
     * Update a Group with given Object
     *
     * @param group Object to be update
     * @return status of the Update
     */
    public LinkGroup update(LinkGroup group) throws DataStoreException {
        LinkGroup updated = null;
        if (group != null) {
            LOGGER.info("Updating Group {}", group);
            updated = dataStore.update(group, group.getName());
        }
        return updated;
    }

    /**
     * Delete a Group with given String
     *
     * @param groupName String to be delete
     * @return status of Delete
     */
    public Boolean delete(String groupName) throws DataStoreException {
        LOGGER.info("Deleting Group {}", groupName);
        linkService.deleteLinksUnder(groupName);
        return dataStore.delete(LinkGroup.class, groupName);
    }

    /**
     *
     * @return list all the categories
     */
    public List<LinkGroup> list() throws DataStoreException {
        return dataStore.list(LinkGroup.class);
    }

    /**
     * Get the list of first level links with given group name
     *
     * @param groupName Object to be search
     * @return list of links
     */
    public List<Link> getFirstLevelLinks(String groupName) throws DataStoreException {
        LOGGER.info("Getting first level links of group {}", groupName);
        Query query = new Query();
        query.addFilter(new Filter<>("groupName", EQUALS, groupName));
        query.addFilter(new Filter<>("parentLinkName", IS_NULL));
        return dataStore.list(Link.class, query);
    }

    /**
     *
     * @return all the application links
     */
    public Map<String, List<Link>> getApplicationLinks() throws DataStoreException {
        Map<String, List<Link>> applicationLinks = new HashMap<>();
        List<LinkGroup> categories = list();
        if (categories != null) {
            List<Link> firstlevelLinks;
            for (LinkGroup group : categories) {
                firstlevelLinks = getFirstLevelLinks(group.getName());
                walkLinkTree(firstlevelLinks);
                applicationLinks.put(group.getName(), firstlevelLinks);
            }
        }

        return applicationLinks;
    }

    private void walkLinkTree(List<Link> links) throws DataStoreException {
        for (Link link : links) {
            //Assign Default Url
            if (link.getTargetUrl() == null || link.getTargetUrl().trim().length() == 0) {
                link.setTargetUrl("/pages/add?link=" + link.getName());
            }
            List<Link> childLinks = linkService.listChildren(link.getName());
            link.setChildren(childLinks);
            walkLinkTree(childLinks);
        }
    }

}
