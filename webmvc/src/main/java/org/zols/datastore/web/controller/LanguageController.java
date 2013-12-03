/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web.controller;


import com.zols.languagemanager.LanguageManager;
import com.zols.languagemanager.domain.Language;
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
public class LanguageController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CategoryController.class);

    @Autowired
    LanguageManager languagesManager;

    @RequestMapping(value = "/api/languages", method = POST)
    @ResponseBody
    public Language create(@RequestBody Language language) {
        LOGGER.info("Creating new language {}", language);
        return languagesManager.add(language);
    }

    @RequestMapping(value = "/api/languages/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Language language) {
        LOGGER.info("Updating language with id {} with {}", name, language);
        if (name.equals(language.getName())) {
            languagesManager.update(language);
        }
    }

    @RequestMapping(value = "/api/languages/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting language with id {}", name);
        languagesManager.delete(name);
    }

    @RequestMapping(value = "/languages/{name}", method = GET)
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("language", languagesManager.get(name));
        return "com/zols/datastore/language";
    }

    @RequestMapping(value = "/languages/add", method = GET)
    public String add(Model model) {
        model.addAttribute("language", new Language());
        return "com/zols/datastore/language";
    }

    @RequestMapping(value = "/api/languages", method = GET)
    @ResponseBody
    public Page<Language> list(
            Pageable page) {
        LOGGER.info("Listing languages");
        return languagesManager.list(page);
    }

    @RequestMapping(value = "/languages", method = GET)
    public String listing() {
        return "com/zols/datastore/listlanguages";
    }
}
