/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this page file, choose Tools | Pages
 * and open the page in the editor.
 */
package org.zols.datastore.web.controller;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.zols.templatemanager.PageManager;
import com.zols.templatemanager.domain.CreatePageRequest;
import com.zols.templatemanager.domain.Page;
import com.zols.templatemanager.domain.PageDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller for Page Handling
 *
 * @author rahul_ma
 */
@Controller
public class PageController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CategoryController.class);

    @Autowired
    PageManager pageManager;

    @ApiIgnore
    @RequestMapping(value = "/api/pages", method = POST)
    @ResponseBody
    public Page create(@RequestBody CreatePageRequest createPageRequest) {
        LOGGER.info("Creating new pages {}", createPageRequest);
        return pageManager.add(createPageRequest);
    }

    @RequestMapping(value = "/api/pages/{name}", method = PUT)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Page page) {
        LOGGER.info("Updating pages with id {} with {}", name, page);
        if (name.equals(page.getName())) {
            pageManager.update(page);
        }
    }

    @RequestMapping(value = "/api/pages/{name}", method = DELETE)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting page with id {}", name);
        pageManager.deletePage(name);
    }

    @RequestMapping(value = "/pages/{name}", method = GET)
    @ApiIgnore
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("page", pageManager.getPage(name));
        return "com/zols/datastore/page";
    }

    @RequestMapping(value = "/page/{name}", method = GET)
    @ApiIgnore
    public String pageDetails(@PathVariable(value = "name") String name, Model model) {
        PageDetail pageDetail = pageManager.getPageDetail(name);
        model.addAttribute(pageDetail.getEntity().getName(), pageDetail.getData());
        return pageDetail.getTemplate().getPath();
    }

    @RequestMapping(value = "/pages/add", method = GET)
    @ApiIgnore
    public String add(Model model) {
        model.addAttribute("page", new Page());
        return "com/zols/datastore/page";
    }

    @RequestMapping(value = "/api/pages", method = GET)
    @ApiIgnore
    @ResponseBody
    public org.springframework.data.domain.Page<Page> list(
            Pageable page) {
        LOGGER.info("Listing Pages");
        return pageManager.pageList(page);
    }

    @RequestMapping(value = "/pages", method = GET)
    @ApiIgnore
    public String listing() {
        return "com/zols/datastore/listpages";
    }
}
