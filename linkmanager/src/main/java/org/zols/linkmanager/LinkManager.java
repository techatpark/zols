package org.zols.linkmanager;

import org.zols.datastore.DataStore;
import org.zols.datastore.domain.Criteria;
import org.zols.linkmanager.domain.Category;
import org.zols.linkmanager.domain.Link;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LinkManager {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(LinkManager.class);

    @Autowired
    private DataStore dataStore;

    /**
     * Creates a new Category with given Object
     *
     * @param category Object to be Create
     * @return created Category object
     */
    public Category add(Category category) {
        return dataStore.create(category, Category.class);
    }

    /**
     * Update a Category with given Object
     *
     * @param category Object to be update
     */
    public void update(Category category) {
        dataStore.update(category, Category.class);
    }

    /**
     * Delete a Category with given String
     *
     * @param categoryName String to be delete
     */
    public void deleteCategory(String categoryName) {
        List<Link> linksUnderCategory = listFirstLevelByCategory(categoryName);
        for (Link link : linksUnderCategory) {
            delete(link.getName());
        }
        dataStore.delete(categoryName, Category.class);

    }

    /**
     * Get the Category with given String
     *
     * @param categoryName String to be Search
     * @return searched Category
     */
    public Category getCategory(String categoryName) {
        return dataStore.read(categoryName, Category.class);

    }

    /**
     * Get the Category list
     *
     * @return searched Category List
     */
    public List<Category> getAllCategories() {
        return dataStore.list(Category.class);
    }

    /**
     * Get the list of Category with given Pageable
     *
     * @param pageable Object to be get
     * @return list of categories
     */
    public Page<Category> categoriesByPageable(Pageable pageable) {
        return dataStore.list(pageable, Category.class);

    }

    /**
     * Create a new Link with given link Object
     *
     * @param link Object to be create
     * @return created link Object
     */
    public Link add(Link link) {
        return dataStore.create(link, Link.class);
    }

    /**
     * update a link with given link Object
     *
     * @param link Object to be update
     */
    public void update(Link link) {
        dataStore.update(link, Link.class);
    }

    /**
     * Get the list of link with given category name
     *
     * @param categoryName Object to be search
     * @return link list object
     */
    public List<Link> listFirstLevelByCategory(String categoryName) {
        List<Criteria> criterias = new ArrayList<Criteria>();
        criterias.add(new Criteria("categoryName", Criteria.Type.IS, categoryName));
        criterias.add(new Criteria("parentLinkName", Criteria.Type.IS_NULL, null));
        return dataStore.list(criterias, Link.class);

    }
    

    /**
     * Get the link with given link String
     *
     * @param link Object to be search
     * @return link object
     */
    public Link getLink(String link) {
        return dataStore.read(link, Link.class);
    }

    /**
     * Get the list of link with given Parent name
     *
     * @param parentName String to be search
     * @return list of links
     */
    public List<Link> listByParent(String parentName) {
        Link listByParent = new Link();
        listByParent.setParentLinkName(parentName);
        return dataStore.listByExample(listByParent);
    }

    /**
     * Get the list of link with given Pageable
     *
     * @param pageable Number of records to display
     * @return link object
     */
    public Page<Link> linksByPageable(Pageable pageable) {
        return dataStore.list(pageable, Link.class);

    }

    /**
     * Delete a link with given String
     *
     * @param linkName Object to be Delete
     */
    public void delete(String linkName) {
        List<Link> children = listByParent(linkName);
        for (Link link : children) {
            delete(link.getName());
        }
        dataStore.delete(linkName, Link.class);
    }

    public Map<String, List<Link>> getApplicationLinks() {
        List<Category> categories = getAllCategories();
        if (categories != null) {
            Map<String, List<Link>> applicationLinks = new HashMap<String, List<Link>>(categories.size());
            List<Link> firstlevelLinks ;
            for (Category category : categories) {
                firstlevelLinks = listFirstLevelByCategory(category.getName()) ;
                walkLinkTree(firstlevelLinks);
                applicationLinks.put(category.getName(), firstlevelLinks);
            }
            LOGGER.debug("" + applicationLinks.size());
            return applicationLinks;
        }

        return null;
    }

	private void walkLinkTree(List<Link> links) {
		for (Link link : links) {
		    List<Link> childLinks = getChildLinks(link);
			link.setChildren(childLinks);
			walkLinkTree(childLinks);
		}
	}
    
    private List<Link> getChildLinks(Link link){
    	return listByParent(link.getName());
    }

}
