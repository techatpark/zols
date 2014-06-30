/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jodel.store.DataStore;
import org.jodel.store.query.Filter;
import static org.jodel.store.query.Filter.Operator.EQUALS;
import static org.jodel.store.query.Filter.Operator.IS_NULL;
import org.jodel.store.query.Query;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.zols.links.domain.Category;
import org.zols.links.domain.Link;
import org.zols.links.provider.LinkProvider;

@Service
public class CategoryService {

    private static final Logger LOGGER = getLogger(CategoryService.class);

    @Autowired
    private DataStore dataStore;

    @Autowired
    private LinkService linkService;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Creates a new Category with given Object
     *
     * @param category Object to be Create
     * @return created Category object
     */
    public Category create(Category category) {
        Category createdCategory = null;
        if (category != null) {
            createdCategory = dataStore.create(Category.class, category);
            LOGGER.info("Created Category {}", createdCategory.getName());
        }
        return createdCategory;
    }

    /**
     * Get the Category with given String
     *
     * @param categoryName String to be Search
     * @return searched Category
     */
    public Category read(String categoryName) {
        LOGGER.info("Reading Category {}", categoryName);
        return dataStore.read(Category.class, categoryName);
    }

    /**
     * Update a Category with given Object
     *
     * @param category Object to be update
     * @return
     */
    public Boolean update(Category category) {
        Boolean updated = false;
        if (category != null) {
            LOGGER.info("Updating Category {}", category);
            updated = dataStore.update(category);
        }
        return updated;
    }

    /**
     * Delete a Category with given String
     *
     * @param categoryName String to be delete
     * @return
     */
    public Boolean delete(String categoryName) {
        LOGGER.info("Deleting Category {}", categoryName);
        List<Link> linksUnderCategory = getFirstLevelLinks(categoryName);
        for (Link link : linksUnderCategory) {
            delete(link.getName());
        }
        return dataStore.delete(Category.class, categoryName);
    }

    public List<Category> list() {
        return dataStore.list(Category.class);
    }
    
    /**
     * Get the list of first level links with given category name
     *
     * @param categoryName Object to be search
     * @return list of links
     */
    public List<Link> getFirstLevelLinks(String categoryName) {
        LOGGER.info("Getting first level links of category {}", categoryName);
        Query query = new Query();
        query.addFilter(new Filter<>("categoryName", EQUALS, categoryName));
        query.addFilter(new Filter<>("parentLinkName", IS_NULL));
        return dataStore.list(Link.class, query);
    }

    public Map<String, List<Link>> getApplicationLinks() {
        Map<String, List<Link>> applicationLinks = new HashMap<>();
        List<Category> categories = list();
        if (categories != null) {
            List<Link> firstlevelLinks;
            for (Category category : categories) {
                firstlevelLinks = getFirstLevelLinks(category.getName());
                walkLinkTree(firstlevelLinks);
                applicationLinks.put(category.getName(), firstlevelLinks);
            }
        }

        Map<String, LinkProvider> beansMap = applicationContext.getBeansOfType(LinkProvider.class);

        for (Map.Entry<String, LinkProvider> entry : beansMap.entrySet()) {
            applicationLinks.put(entry.getKey(), entry.getValue().getLinks());
        }

        return applicationLinks;
    }

    private void walkLinkTree(List<Link> links) {
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
