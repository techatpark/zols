/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.templatemanager.web;

import com.mangofactory.swagger.annotations.ApiIgnore;
import org.zols.templatemanager.TemplateManager;
import org.zols.templatemanager.domain.Template;
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
 * Controller for Template Handling
 * @author rahul_ma
 */
@Controller
public class TemplateController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TemplateController.class);
    
    @Autowired
    TemplateManager templateManager;
    @ApiIgnore
     @RequestMapping(value = "/api/templates", method = POST)
    @ResponseBody
    public Template create(@RequestBody Template template) {
        LOGGER.info("Creating new templates {}", template);
        return templateManager.add(template);
    }
    
     @RequestMapping(value = "/api/templates/{name}", method = PUT)
     @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Template template) {
        LOGGER.info("Updating templates with id {} with {}", name, template);
        if (name.equals(template.getName())) {
            templateManager.update(template);
        }
    }
    
    @RequestMapping(value = "/api/templates/{name}", method = DELETE)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting template with id {}", name);
        templateManager.deleteTemplate(name);
    }
    
     @RequestMapping(value = "/templates/{name}", method = GET)
     @ApiIgnore
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("template", templateManager.getTemplate(name));
        return "org/zols/templatemanager/template";
    }
    @RequestMapping(value = "/templates/add", method = GET)
    @ApiIgnore
    public String add(Model model) {
        model.addAttribute("template", new Template());
        return "org/zols/templatemanager/template";
    }

    @RequestMapping(value = "/api/templates", method = GET)
    @ApiIgnore
    @ResponseBody
    public Page<Template> list(
            Pageable page) {
        LOGGER.info("Listing Templates");
        return templateManager.templateList(page);
    }

    @RequestMapping(value = "/templates", method = GET)
    @ApiIgnore
    public String listing() {
        return "org/zols/templatemanager/listtemplates";
    }
}
