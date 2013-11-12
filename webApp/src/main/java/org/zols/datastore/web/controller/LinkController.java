package org.zols.datastore.web.controller;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.zols.datastore.DataStore;
import com.zols.datastore.domain.Entity;
import com.zols.datastore.domain.Link;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/links")
public class LinkController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(LinkController.class);

    @Autowired
    private DataStore dataStore;

    @RequestMapping(method = POST)
    @ResponseBody
    public Link create(@RequestBody Link links) {
        LOGGER.info("Creating new entity {}", links);
        return dataStore.create(links, Link.class);
    }

    @RequestMapping(value = "/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Link links) {
        LOGGER.info("Updating entity with id {} with {}", name, links);
        if (name.equals(links.getName())) {
            dataStore.update(links, Link.class);
        }
    }

    @RequestMapping(value = "/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting entity with id {}", name);
        dataStore.delete(name, Link.class);
    }

    @RequestMapping(method = GET)
    @ResponseBody
    public Page<Link> list(
            Pageable page) {
        LOGGER.info("Listing entities");
        return dataStore.list(page, Link.class);
    }

    @RequestMapping(value = "/add", method = GET)
    public String add(Model model) {
        model.addAttribute("link", new Link());
        return "datastore/link";
    }

    @RequestMapping(value = "/edit/{name}", method = GET)
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("link", dataStore.read(name, Link.class));
        return "datastore/link";
    }

    @RequestMapping(value = "/listing", method = GET)
    public String listing() {
        return "datastore/listlinks";
    }
}
