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
import org.zols.templates.domain.Template;
import org.zols.templates.service.TemplateService;

/**
 *
 * @author sathish_ku
 */
@RestController
@RequestMapping(value = "/api/templates")
public class TemplateControler {
    private static final Logger LOGGER = getLogger(TemplateControler.class);

    @Autowired
    private TemplateService templateService;

    @RequestMapping(method = POST)
    public Template create(@RequestBody Template template) throws DataStoreException {
        LOGGER.info("Creating new templates {}", template.getName());
        return templateService.create(template);
    }

    @RequestMapping(value = "/{name}", method = GET)
    public Template read(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Getting template ", name);
        return templateService.read(name);
    }

    @RequestMapping(value = "/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Template template) throws DataStoreException {
        if (name.equals(template.getName())) {
            LOGGER.info("Updating templates with id {} with {}", name, template);
            templateService.update(template);
        }
    }

    @RequestMapping(value = "/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Deleting templates with id {}", name);
        templateService.delete(name);
    }

    @RequestMapping(method = GET)
    public List<Template> list() throws DataStoreException {
        LOGGER.info("Getting Templates ");
        return templateService.list();
    }
}
