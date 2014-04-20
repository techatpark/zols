/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.languagemanager.web;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.zols.languagemanager.LanguageManager;
import org.zols.languagemanager.domain.Language;

/**
 * Controller for Language Handling
 *
 * @author Navin.
 */
@Controller
@Api(value = "Language",description = "operations about Language")
public class LanguageController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(LanguageController.class);

    @Autowired
    LanguageManager languagesManager;

    @ApiOperation(value = "Create", response = Language.class, notes = "Returns new language")
    @RequestMapping(value = "/api/languages", method = POST,consumes = APPLICATION_JSON_VALUE,produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Language create(@RequestBody Language language) {
        LOGGER.info("Creating new language {}", language);
        return languagesManager.add(language);
    }

    @ApiOperation(value = "Update", notes = "Updates existing language")
    @RequestMapping(value = "/api/languages/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Language language) {
        LOGGER.info("Updating language with id {} with {}", name, language);
        if (name.equals(language.getName())) {
            languagesManager.update(language);
        }
    }

    @ApiOperation(value = "Delete", notes = "Deletes given language")
    @RequestMapping(value = "/api/languages/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting language with id {}", name);
        languagesManager.delete(name);
    }
    
    @ApiOperation(value = "get Language by name",response = Language.class , notes = "Return language")
    @RequestMapping(value = "/api/languages/{name}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Language getLanguageByName(@PathVariable(value = "name") String name){
        return languagesManager.get(name);
    }

    @RequestMapping(value = "/languages/{name}", method = GET)
    @ApiIgnore
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("language", languagesManager.get(name));
        return "org/zols/languagemanager/language";
    }

    @RequestMapping(value = "/languages/add", method = GET)
    @ApiIgnore
    public String add(Model model) {
        model.addAttribute("language", new Language());
        return "org/zols/languagemanager/language";
    }

    @ApiOperation(value = "List", response = Page.class, notes = "Returns all saved languages")
    @RequestMapping(value = "/api/languages", method = GET)
    @ResponseBody
    public Page<Language> list(
            Pageable page) {
        LOGGER.info("Listing languages");
        return languagesManager.list(page);
    }

    @RequestMapping(value = "/languages", method = GET)
    @ApiIgnore
    public String listing() {
        return "org/zols/languagemanager/listlanguages";
    }
}
