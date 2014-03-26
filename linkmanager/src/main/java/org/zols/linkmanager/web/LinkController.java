/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.linkmanager.web;

import com.mangofactory.swagger.annotations.ApiIgnore;
import org.zols.linkmanager.LinkManager;
import org.zols.linkmanager.domain.Link;
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
import org.zols.datastore.web.controller.MenuNode;

/**
 * Controller for link
 *
 * @author poomalai
 */
@Controller
public class LinkController {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(LinkController.class);
    
    @Autowired
    private LinkManager linkManager;
    
    @RequestMapping(value = "/api/links", method = POST)
    @ApiIgnore
    @ResponseBody
    public Link create(@RequestBody Link link) {
        LOGGER.info("Creating new links {}", link);
        if (link.getParentLinkName() != null && link.getParentLinkName().trim().length() != 0) {
            Link parentLink = linkManager.getLink(link.getParentLinkName());
            link.setCategoryName(parentLink.getCategoryName());
        }       
        return linkManager.add(link);
    }
    
    @RequestMapping(value = "/api/links/{name}", method = PUT)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Link links) {
        LOGGER.info("Updating link with id {} with {}", name, links);
        if (name.equals(links.getName())) {
            linkManager.update(links);
        }
    }
    
    @RequestMapping(value = "/api/links/{name}", method = DELETE)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting links with id {}", name);
        linkManager.delete(name);
    }
    
    @RequestMapping(value = "/api/links", method = GET)
    @ApiIgnore
    @ResponseBody
    public Page<Link> list(
            Pageable page) {
        LOGGER.info("Listing links");
        return linkManager.linksByPageable(page);
    }
    
    @RequestMapping(value = "/api/links/categories/{categoryName}", method = GET)
    @ApiIgnore
    @ResponseBody
    public List<Link> listByCategory(@PathVariable(value = "categoryName") String categoryName) {
        LOGGER.info("Listing links by category " +  categoryName);
        return linkManager.listFirstLevelByCategory(categoryName);
    }
    
    @RequestMapping(value = "/api/links/children/{parentName}", method = GET)
    @ApiIgnore
    @ResponseBody
    public List<Link> childrenOf(@PathVariable(value = "parentName") String parentName) {
        LOGGER.info("Listing links by parent " + parentName);
        return linkManager.listByParent(parentName);
    }
    
    @RequestMapping(value = "/links/addunder/{categoryName}", method = GET)
    @ApiIgnore
    public String addCategory(Model model, @PathVariable(value = "categoryName") String categoryName) {
        Link link = new Link();
        link.setCategoryName(categoryName);
        model.addAttribute("link", link);        
        return "org/zols/linkmanager/link";
    }
    
    @RequestMapping(value = "/links/addchild/{parentLinkName}", method = GET)
    @ApiIgnore
    public String addParent(Model model, @PathVariable(value = "parentLinkName") String parentLinkName) {
        Link link = new Link();
        link.setParentLinkName(parentLinkName);
        model.addAttribute("link", link);
        return "org/zols/linkmanager/link";
    }
    
    @RequestMapping(value = "/links/edit/{name}", method = GET)
    @ApiIgnore
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("link", linkManager.getLink(name));
        return "org/zols/linkmanager/link";
    }
    
    @RequestMapping(value = "/links", method = GET)
    @ApiIgnore
    public String listing(Model model) {
        return "org/zols/linkmanager/listlinks";
    }
}
