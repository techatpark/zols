package com.zols.linkmanager;

import com.zols.datastore.DataStore;
import com.zols.linkmanager.domain.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LinkManager {

    @Autowired
    private DataStore dataStore;

    public Link add(Link link) {
        return add(link,null);
    }
            
    public Link add(Link link, String parentName) {        
        link.setParentLinkName(parentName);
        return dataStore.create(link, Link.class);
    }

    public Link get(String linkName) {
        return dataStore.read(linkName, Link.class);
    }

    public void update(Link link) {
        dataStore.update(link, Link.class);
    }

    public void delete(String name) {
        dataStore.delete(name, Link.class);
    }

    public Page<Link> list(Pageable page) {
        return dataStore.list(page, Link.class);
    }

}
