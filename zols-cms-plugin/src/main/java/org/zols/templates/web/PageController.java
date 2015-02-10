/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.templates.web;

import java.util.List;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zols.datatore.exception.DataStoreException;
import org.zols.templates.domain.PageRequest;
import org.zols.templates.domain.Page;
import org.zols.templates.service.PageService;

@RestController
@RequestMapping(value = "/api/pages")
public class PageController {
    private static final Logger LOGGER = getLogger(PageController.class);

    @Autowired
    private PageService pageService;

    @RequestMapping(method = POST)
    public Page create(@RequestBody PageRequest createPageRequest) throws DataStoreException {
        LOGGER.info("Creating new pages {}", createPageRequest.getLinkName());
        return pageService.create(createPageRequest);
    }

    @RequestMapping(value = "/{name}", method = GET)
    public Page read(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Getting page ", name);
        return pageService.read(name);
    }

    @RequestMapping(value = "/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Page page) throws DataStoreException {
        if (name.equals(page.getName())) {
            LOGGER.info("Updating page with id {} with {}", name, page);
            pageService.update(page);
        }
    }

    @RequestMapping(value = "/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Deleting page with id {}", name);
        pageService.delete(name);
    }

    @RequestMapping(method = GET)
    public List<Page> list() throws DataStoreException {
        LOGGER.info("Getting pages ");
        return pageService.list();
    }
}
