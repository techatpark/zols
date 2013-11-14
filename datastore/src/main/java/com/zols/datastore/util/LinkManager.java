package com.zols.datastore.util;

import com.zols.datastore.DataStore;
import com.zols.linkmanager.domain.Category;
import com.zols.linkmanager.domain.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LinkManager {

    @Autowired
    private DataStore dataStore;

    public Category add(Category category) {
        return dataStore.create(category, Category.class);
    }

    public void update(Category category) {
        dataStore.update(category, Category.class);
    }

    public void deleteCategory(String categoryName) {
        dataStore.delete(categoryName, Category.class);
    }

    public Category getCategory(String categoryName) {
        return dataStore.read(categoryName, Category.class);

    }

    public Page<Category> categoryList(Pageable page) {
        return dataStore.list(page, Category.class);
    }

    public Link add(Link link) {
        return add(link, null);
    }

    public Link add(Link link, String parentName) {
        link.setChildren(null);
        link.setParentLinkName(parentName);
        return dataStore.create(link, Link.class);
    }

    public Link get(String linkName) {
        Link link = dataStore.read(linkName, Link.class);
        // Populate Children
        if (link != null) {
            Link searchLink = new Link();
            searchLink.setParentLinkName(link.getName());
            link.setChildren(dataStore.list(searchLink));
        }
        return link;
    }

    public void update(Link link) {
        link.setChildren(null);
        dataStore.update(link, Link.class);
    }

    public void delete(String linkName) {
        Link searchLink = new Link();
        //Delete Children
        searchLink.setParentLinkName(linkName);
        dataStore.delete(searchLink);
        // Delete Link
        dataStore.delete(linkName, Link.class);
    }

    public Page<Link> list(Pageable page) {
        return dataStore.list(page, Link.class);
    }

}
