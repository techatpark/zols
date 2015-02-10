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
import org.zols.templates.domain.TemplateRepository;
import org.zols.templates.service.TemplateRepositoryService;

/**
 *
 * @author sathish_ku
 */
@RestController
@RequestMapping(value = "/api/template_repositories")
public class TemplateRepositoryController {

    private static final Logger LOGGER = getLogger(TemplateRepositoryController.class);

    @Autowired
    private TemplateRepositoryService templateRepositoryService;

    @RequestMapping(method = POST)
    public TemplateRepository create(@RequestBody TemplateRepository templateRepository) throws DataStoreException {
        LOGGER.info("Creating new templateRepositories {}", templateRepository.getName());
        return templateRepositoryService.create(templateRepository);
    }

    @RequestMapping(value = "/{name}", method = GET)
    public TemplateRepository read(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Getting templateRepository ", name);
        return templateRepositoryService.read(name);
    }

    @RequestMapping(value = "/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody TemplateRepository templateRepository) throws DataStoreException {
        if (name.equals(templateRepository.getName())) {
            LOGGER.info("Updating templateRepositories with id {} with {}", name, templateRepository);
            templateRepositoryService.update(templateRepository);
        }
    }

    @RequestMapping(value = "/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Deleting templateRepositories with id {}", name);
        templateRepositoryService.delete(name);
    }

    @RequestMapping(method = GET)
    public List<TemplateRepository> list() throws DataStoreException {
        LOGGER.info("Getting TemplateRepositories ");
        return templateRepositoryService.list();
    }

    @RequestMapping(value = "/{name}/first_level_templates", method = GET)
    public List<Template> listTemplates(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Getting templates of repository {} ", name);
        return templateRepositoryService.listTemplates(name);
    }

    @RequestMapping(value = "/{name}/valid_templates", method = GET)
    public List<String> listTemplateFile(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Getting valid templates of repository {} ", name);
        return templateRepositoryService.listTemplateFiles(name);
    }
}
