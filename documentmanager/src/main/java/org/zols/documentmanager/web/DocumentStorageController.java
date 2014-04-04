/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documentmanager.web;

import com.mangofactory.swagger.annotations.ApiIgnore;
import org.zols.documentmanager.DocumentStorageManager;
import org.zols.documentmanager.domain.DocumentStorage;
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
 * Controller for Language Handling
 *
 * @author Navin.
 */
@Controller
public class DocumentStorageController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocumentStorageController.class);

    @Autowired
    DocumentStorageManager documentStorageManager;

    @RequestMapping(value = "/api/documentstorages", method = POST)
    @ApiIgnore
    @ResponseBody
    public DocumentStorage create(@RequestBody DocumentStorage documentstorage) {
        LOGGER.info("Creating new documentstorage {}", documentstorage);
        return documentStorageManager.add(documentstorage);
    }

    @RequestMapping(value = "/api/documentstorages/{name}", method = PUT)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody DocumentStorage documentstorage) {
        LOGGER.info("Updating documentstorage with id {} with {}", name, documentstorage);
        if (name.equals(documentstorage.getName())) {
            documentStorageManager.update(documentstorage);
        }
    }

    @RequestMapping(value = "/api/documentstorages/{name}", method = DELETE)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting documentstorage with id {}", name);
        documentStorageManager.delete(name);
    }

    @RequestMapping(value = "/documentstorages/{name}", method = GET)
    @ApiIgnore
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("documentStorage", documentStorageManager.get(name));
        return "org/zols/documentmanager/documentstorage";
    }

    @RequestMapping(value = "/documentstorages/add", method = GET)
    @ApiIgnore
    public String add(Model model) {
        model.addAttribute("documentStorage", new DocumentStorage());
        return "org/zols/documentmanager/documentstorage";
    }

    @RequestMapping(value = "/api/documentstorages", method = GET)
    @ApiIgnore
    @ResponseBody
    public Page<DocumentStorage> list(
            Pageable page) {
        LOGGER.info("Listing documentstorages");
        return documentStorageManager.list(page);
    }

    @RequestMapping(value = "/documentstorages", method = GET)
    @ApiIgnore
    public String listing() {
        return "org/zols/documentmanager/listdocumentstorages";
    }
}
