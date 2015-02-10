/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.web;

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
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zols.datatore.exception.DataStoreException;
import org.zols.links.domain.Link;
import org.zols.links.service.LinkService;

@RestController
@RequestMapping(value = "/api/links")
public class LinkController {

    private static final Logger LOGGER = getLogger(LinkController.class);

    @Autowired
    private LinkService linkService;

    @RequestMapping(method = POST)
    public Link create(@RequestBody Link link) throws DataStoreException {
        LOGGER.info("Creating new links {}", link.getName());
        return linkService.create(link);
    }

    @RequestMapping(value = "/{name}", method = GET)
    public Link read(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Getting link ", name);
        return linkService.read(name);
    }

    @RequestMapping(value = "/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Link link) throws DataStoreException {
        if (name.equals(link.getName())) {
            LOGGER.info("Updating links with id {} with {}", name, link);
            linkService.update(link);
        }
    }

    @RequestMapping(value = "/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Deleting links with id {}", name);
        linkService.delete(name);
    }

    @RequestMapping(method = GET)
    public List<Link> list() throws DataStoreException {
        LOGGER.info("Getting Links ");
        return linkService.list();
    }

    @RequestMapping(value = "/childen_of/{name}", method = GET)
    public List<Link> listChildren(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Getting childen of Link {}", name);
        return linkService.listChildren(name);
    }

    @RequestMapping(value = "/{name}/link_url", method = PATCH)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void linkUrl(@PathVariable(value = "name") String name,
            @RequestBody String url) throws DataStoreException {
        linkService.linkUrl(name, url);
    }
}
