package com.zols.linkmanager;

import com.zols.datastore.DataStore;
import com.zols.linkmanager.domain.Category;
import com.zols.linkmanager.domain.Link;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LinkManager {

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
    public List<Category> categoryList() {
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
    public List<Link> listByCategory(String categoryName) {
        Link linkByExample = new Link();
        linkByExample.setCategoryName(categoryName);
        return dataStore.listByExample(linkByExample);

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
     * @param link Object to be Delete
     */
    public void delete(String link) {
        dataStore.delete(link, Link.class);
    }

    /**
     * Delete a link with given Category name
     *
     * @param categoryName Object to be Delete
     */
    public void deleteLinkByCategory(String categoryName) {
        Link link = new Link();
        link.setCategoryName(categoryName);
        dataStore.deleteByExample(link);
    }

}
