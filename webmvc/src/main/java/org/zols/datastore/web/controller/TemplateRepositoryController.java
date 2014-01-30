/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this templateRepository file, choose Tools | TemplateRepositorys
 * and open the templateRepository in the editor.
 */

package org.zols.datastore.web.controller;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.zols.templatemanager.TemplateRepositoryManager;
import com.zols.templatemanager.domain.TemplateRepository;
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
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller for TemplateRepository Handling
 * @author rahul_ma
 */
@Controller
public class TemplateRepositoryController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CategoryController.class);
    
    @Autowired
    TemplateRepositoryManager templateRepositoryManager;
    @ApiIgnore
     @RequestMapping(value = "/api/templateRepositories", method = POST)
    @ResponseBody
    public TemplateRepository create(@RequestBody TemplateRepository templateRepository) {
        LOGGER.info("Creating new templateRepositories {}", templateRepository);
        return templateRepositoryManager.add(templateRepository);
    }
    
     @RequestMapping(value = "/api/templateRepositories/{name}", method = PUT)
     @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody TemplateRepository templateRepository) {
        LOGGER.info("Updating templateRepositories with id {} with {}", name, templateRepository);
        if (name.equals(templateRepository.getName())) {
            templateRepositoryManager.update(templateRepository);
        }
    }
    
    @RequestMapping(value = "/api/templateRepositories/{name}", method = DELETE)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting templateRepository with id {}", name);
        templateRepositoryManager.deleteTemplateRepository(name);
    }
    
     @RequestMapping(value = "/templateRepositories/{name}", method = GET)
     @ApiIgnore
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("templateRepository", templateRepositoryManager.getTemplateRepository(name));
        return "com/zols/datastore/templateRepository";
    }
    @RequestMapping(value = "/templateRepositories/add", method = GET)
    @ApiIgnore
    public String add(Model model) {
        model.addAttribute("templateRepository", new TemplateRepository());
        return "com/zols/datastore/templateRepository";
    }

    @RequestMapping(value = "/api/templateRepositories", method = GET)
    @ApiIgnore
    @ResponseBody
    public Page<TemplateRepository> list(
            Pageable page) {
        LOGGER.info("Listing TemplateRepositorys");
        return templateRepositoryManager.templateRepositoryList(page);
    }

    @RequestMapping(value = "/templateRepositories", method = GET)
    @ApiIgnore
    public String listing() {
        return "com/zols/datastore/listtemplateRepositories";
    }
}
