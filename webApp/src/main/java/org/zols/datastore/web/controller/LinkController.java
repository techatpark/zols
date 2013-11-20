package org.zols.datastore.web.controller;

import com.zols.linkmanager.LinkManager;
import com.zols.linkmanager.domain.Link;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class LinkController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(LinkController.class);

    @Autowired
    private LinkManager linkManager;

    @RequestMapping(value = "/api/links", method = POST)
    @ResponseBody
    public Link create(@RequestBody Link links) {
        LOGGER.info("Creating new links {}", links);
        return linkManager.add(links);
    }

    @RequestMapping(value = "/api/links/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Link links) {
        LOGGER.info("Updating link with id {} with {}", name, links);
        if (name.equals(links.getName())) {
            linkManager.update(links);
        }
    }

    @RequestMapping(value = "/api/links/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting links with id {}", name);
        linkManager.delete(name);
    }

    @RequestMapping(value = "/api/links", method = GET)
    @ResponseBody
    public Page<Link> list(
            Pageable page) {
        LOGGER.info("Listing entities");
        return linkManager.linksByPageable(page);
    }

    @RequestMapping(value = "/api/links/category/{categoryName}", method = GET)
    @ResponseBody
    public List<Link> listByCategory(@PathVariable(value = "categoryName") String categoryName) {
        LOGGER.info("Listing entities");
        return linkManager.listByCategory(categoryName);
    }

    @RequestMapping(value = "/api/links/{parentName}", method = GET)
    @ResponseBody
    public List<Link> listByParent(@PathVariable(value = "parentName") String parentName) {
        LOGGER.info("Listing entities");
        return linkManager.listByParent(parentName);
    }

    @RequestMapping(value = "/links/addunder/{categoryName}", method = GET)
    public String addCategory(Model model, @PathVariable(value = "categoryName") String categoryName) {
        model.addAttribute("link", new Link());
        model.addAttribute("categoryName", categoryName);
        return "datastore/link";
    }

    @RequestMapping(value = "/links/addchild/{parentName}", method = GET)
    public String addParent(Model model, @PathVariable(value = "parentName") String parentName) {
        model.addAttribute("link", new Link());
        model.addAttribute("parentName", parentName);
        return "datastore/link";
    }

    @RequestMapping(value = "/links/edit/{name}", method = GET)
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("link", linkManager.getLink(name));
        return "datastore/link";
    }

    @RequestMapping(value = "/links", method = GET)
    public String listing() {
        return "datastore/listlinks";
    }
}
