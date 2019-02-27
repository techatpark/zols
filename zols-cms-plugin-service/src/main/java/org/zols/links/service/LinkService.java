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
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.zols.datatore.exception.DataStoreException;
import org.zols.links.domain.LinkGroup;
import org.zols.links.domain.Link;

public class LinkService {

    private static final Logger LOGGER = getLogger(LinkService.class);

    private final LinkGroupService linkGroupService;

    private final DataStore dataStore;

    public LinkService(LinkGroupService linkGroupService, DataStore dataStore) {
        this.linkGroupService = linkGroupService;
        this.dataStore = dataStore;
    }
    
    /**
     * Creates a new Link for given group (e.g header)
     *
     * @param groupName
     * @param link Object to be Create
     * @return created Link object
     */
    public Link createFor(String groupName, Link link) throws DataStoreException {
        Link createdLink = null;
        if (link != null) {
            link.setGroupName(groupName);
            createdLink = dataStore.getObjectManager(Link.class).create(link);
            LOGGER.info("Created Link {} for {}", createdLink.getName(), groupName);

            if (link.getTargetUrl() == null || link.getTargetUrl().trim().length() == 0) {
                LOGGER.info("Setting Default Link URL {}", createdLink.getName());
                createdLink.setTargetUrl("/create_page/" + createdLink.getName());
                update(createdLink);
            }
        }
        return createdLink;
    }

    public Link createUnder(String parentLinkName, Link link) throws DataStoreException {
        Link createdLink = null;
        if (link != null) {
            Optional<Link> parentLink = read(parentLinkName);
            if (parentLink.isPresent()) {
                link.setParentLinkName(parentLinkName);
                link.setGroupName(parentLink.get().getGroupName());
                createdLink = dataStore.getObjectManager(Link.class).create(link);
                LOGGER.info("Created Link {} under {}", createdLink.getName(), parentLinkName);

                if (link.getTargetUrl() == null || link.getTargetUrl().trim().length() == 0) {
                    LOGGER.info("Setting Default Link URL {}", createdLink.getName());
                    createdLink.setTargetUrl("/create_page/" + createdLink.getName());
                    update(createdLink);
                }
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
    public Optional<Link> read(String linkName) throws DataStoreException {
        LOGGER.info("Reading Link {}", linkName);
        return dataStore.getObjectManager(Link.class).read(new SimpleEntry("name", linkName));
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
            updated = dataStore.getObjectManager(Link.class).update(link, new SimpleEntry("name", link.getName()));
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
        List<Link> children = linkGroupService.listChildren(linkName);
        if (children != null) {
            for (Link child : children) {
                delete(child.getName());
            }
        }
        return dataStore.getObjectManager(Link.class).delete(new SimpleEntry("name", linkName));
    }

    

    

    /**
     *
     * @return list of links
     */
    public List<Link> list() throws DataStoreException {
        LOGGER.info("Getting Links ");
        return dataStore.getObjectManager(Link.class).list();
    }

    /**
     *
     * @return list of application links
     */
    public Map<String, List<Link>> getApplicationLinks() throws DataStoreException {
        Map<String, List<Link>> applicationLinks = new HashMap<>();
        List<LinkGroup> categories = linkGroupService.list();
        if (categories != null) {
            List<Link> firstlevelLinks;
            for (LinkGroup group : categories) {
                firstlevelLinks = linkGroupService.getFirstLevelLinks(group.getName());
                if (firstlevelLinks != null) {
                    for (Link link : firstlevelLinks) {
                        link.setChildren(linkGroupService.listChildren(link.getName()));
                    }
                    applicationLinks.put(group.getName(), firstlevelLinks);
                }
            }
        }

//        Map<String, LinkProvider> beansMap = applicationContext.getBeansOfType(LinkProvider.class);
//        beansMap.entrySet().stream().forEach((entry) -> {
//            applicationLinks.put(entry.getKey(), entry.getValue().getLinks());
//        });

        return applicationLinks;
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
            Optional<Link> link = read(linkName);
            if (link.isPresent()) {
                link.get().setTargetUrl(url);
                updated = update(link.get());
            }

        }
        return updated;
    }

}
