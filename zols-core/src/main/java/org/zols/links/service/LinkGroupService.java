/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.zols.datastore.query.MapQuery;
import org.zols.datatore.exception.DataStoreException;
import org.zols.links.domain.LinkGroup;
import org.zols.links.domain.Link;


public class LinkGroupService {

    private static final Logger LOGGER = getLogger(LinkGroupService.class);

    
    private final DataStore dataStore;

    


    public LinkGroupService(DataStore dataStore) {
        this.dataStore = dataStore;
        
    }
    
    

    /**
     * Creates a new Group with given Object
     *
     * @param group Object to be Create
     * @return created Group object
     */
    
    public LinkGroup create(@Valid LinkGroup group) throws DataStoreException {
        LinkGroup createdGroup = null;
        if (group != null) {
            createdGroup = dataStore.getObjectManager(LinkGroup.class).create(group);
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
    public Optional<LinkGroup> read(String groupName) throws DataStoreException {
        LOGGER.info("Reading Group {}", groupName);
        return dataStore.getObjectManager(LinkGroup.class).read(new SimpleEntry("name", groupName));
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
            updated = dataStore.getObjectManager(LinkGroup.class).update(group, new SimpleEntry("name", group.getName()));
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
        deleteLinksUnder(groupName);
        return dataStore.getObjectManager(LinkGroup.class).delete(new SimpleEntry("name", groupName));
    }
    
    /**
     * Get the list of link with given Parent name
     *
     * @param groupName String to be search
     * @return list of links
     */
    public Boolean deleteLinksUnder(String groupName) throws DataStoreException {
        LOGGER.info("Deleting links under group {}", groupName);
        return dataStore.getObjectManager(Link.class).delete(new MapQuery().string("groupName").eq(groupName));
    }

    /**
     *
     * @return list all the categories
     */
    public List<LinkGroup> list() throws DataStoreException {
        return dataStore.getObjectManager(LinkGroup.class).list();
    }

    /**
     * Get the list of first level links with given group name
     *
     * @param groupName Object to be search
     * @return list of links
     */
    public List<Link> getFirstLevelLinks(String groupName) throws DataStoreException {
        LOGGER.info("Getting first level links of group {}", groupName);
        return dataStore.getObjectManager(Link.class).list(new MapQuery().string("groupName").eq(groupName).and().string("parentLinkName").doesNotExist());
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
            List<Link> childLinks = listChildren(link.getName());
            link.setChildren(childLinks);
            walkLinkTree(childLinks);
        }
    }
    
    /**
     * Get the list of link with given Parent name
     *
     * @param parentLinkName String to be search
     * @return list of links
     */
    public List<Link> listChildren(String parentLinkName) throws DataStoreException {
        LOGGER.info("Getting children of link {}", parentLinkName);
        return dataStore.getObjectManager(Link.class).list(new MapQuery().string("parentLinkName").eq(parentLinkName));
    }

}
