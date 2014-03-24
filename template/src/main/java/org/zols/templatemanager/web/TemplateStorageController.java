/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this templateStorage file, choose Tools | TemplateStorages
 * and open the templateStorage in the editor.
 */

package org.zols.templatemanager.web;

import com.mangofactory.swagger.annotations.ApiIgnore;
import org.zols.templatemanager.TemplateStorageManager;
import org.zols.templatemanager.domain.TemplateStorage;
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
 * Controller for TemplateStorage Handling
 * @author rahul_ma
 */
@Controller
public class TemplateStorageController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TemplateStorageController.class);
    
    @Autowired
    TemplateStorageManager templateStorageManager;
    @ApiIgnore
     @RequestMapping(value = "/api/templateStorages", method = POST)
    @ResponseBody
    public TemplateStorage create(@RequestBody TemplateStorage templateStorage) {
        LOGGER.info("Creating new templateStorage {}", templateStorage);
        return templateStorageManager.add(templateStorage);
    }
    
     @RequestMapping(value = "/api/templateStorages/{name}", method = PUT)
     @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody TemplateStorage templateStorage) {
        LOGGER.info("Updating templateStorage with id {} with {}", name, templateStorage);
        if (name.equals(templateStorage.getName())) {
            templateStorageManager.update(templateStorage);
        }
    }
    
    @RequestMapping(value = "/api/templateStorages/{name}", method = DELETE)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting templateStorage with id {}", name);
        templateStorageManager.deleteTemplateStorage(name);
    }
    
     @RequestMapping(value = "/templateStorages/{name}", method = GET)
     @ApiIgnore
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("templateStorage", templateStorageManager.getTemplateStorage(name));
        return "org/zols/templatemanager/templateStorage";
    }
    @RequestMapping(value = "/templateStorages/add", method = GET)
    @ApiIgnore
    public String add(Model model) {
        model.addAttribute("templateStorage", new TemplateStorage());
        return "org/zols/templatemanager/templateStorage";
    }

    @RequestMapping(value = "/api/templateStorages", method = GET)
    @ApiIgnore
    @ResponseBody
    public Page<TemplateStorage> list(
            Pageable page) {
        LOGGER.info("Listing TemplateStorages");
        return templateStorageManager.templateStorageList(page);
    }

    @RequestMapping(value = "/templateStorages", method = GET)
    @ApiIgnore
    public String listing() {
        return "org/zols/templatemanager/listtemplateStorages";
    }
}
