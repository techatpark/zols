/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this page file, choose Tools | Pages
 * and open the page in the editor.
 */
package org.zols.templatemanager.web;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.zols.templatemanager.PageManager;
import org.zols.templatemanager.domain.CreatePageRequest;
import org.zols.templatemanager.domain.Page;
import org.zols.templatemanager.domain.PageDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
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
@Api(value = "Page",description = "operations about Page")
public class PageController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PageController.class);

    @Autowired
    PageManager pageManager;

    @ApiOperation(value = "Create", response = Page.class, notes = "Returns new page")
    @RequestMapping(value = "/api/pages", method = POST,consumes = APPLICATION_JSON_VALUE,produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page create(@RequestBody CreatePageRequest createPageRequest) {
        LOGGER.info("Creating new pages {}", createPageRequest);
        return pageManager.add(createPageRequest);
    }

    @ApiOperation(value = "Update", notes = "Updates existing page")
    @RequestMapping(value = "/api/pages/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Page page) {
        LOGGER.info("Updating pages with id {} with {}", name, page);
        if (name.equals(page.getName())) {
            pageManager.update(page);
        }
    }

    @ApiOperation(value = "Delete", notes = "Deletes given page")
    @RequestMapping(value = "/api/pages/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting page with id {}", name);
        pageManager.deletePage(name);
    }

    @RequestMapping(value = "/pages/{name}", method = GET)
    @ApiIgnore
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("page", pageManager.getPage(name));
        return "org/zols/templatemanager/page";
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
        return "org/zols/templatemanager/page";
    }
    
    @ApiOperation(value = "Get page by name", response = Page.class, notes = "Returns page of given name")
    @RequestMapping(value = "/api/pages/{name}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page getPageByName(@PathVariable(value = "name")String name){
        return pageManager.getPage(name);
    }

    @ApiOperation(value = "List", response = org.springframework.data.domain.Page.class, notes = "Returns all saved pages")
    @RequestMapping(value = "/api/pages", method = GET,produces = APPLICATION_JSON_VALUE)
     @ResponseBody
    public org.springframework.data.domain.Page<Page> list(
            Pageable page) {
        LOGGER.info("Listing Pages");
        return pageManager.pageList(page);
    }

    @RequestMapping(value = "/pages", method = GET)
    @ApiIgnore
    public String listing() {
        return "org/zols/templatemanager/listpages";
    }
}
