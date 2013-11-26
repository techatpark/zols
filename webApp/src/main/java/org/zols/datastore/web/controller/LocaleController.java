/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web.controller;

import com.zols.localemanager.LocaleManager;
import com.zols.localemanager.domain.Locale;
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
 * Controller for Locale Handling
 *
 * @author Navin
 */
@Controller
public class LocaleController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CategoryController.class);

    @Autowired
    LocaleManager localeManager;

    @RequestMapping(value = "/api/locale", method = POST)
    @ResponseBody
    public Locale create(@RequestBody Locale locale) {
        LOGGER.info("Creating new locale {}", locale);
        return localeManager.add(locale);
    }

    @RequestMapping(value = "/api/locale/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Locale locale) {
        LOGGER.info("Updating locale with id {} with {}", name, locale);
        if (name.equals(locale.getName())) {
            localeManager.update(locale);
        }
    }

    @RequestMapping(value = "/api/locale/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting locale with id {}", name);
        localeManager.deleteLocale(name);
    }

    @RequestMapping(value = "/locale/{name}", method = GET)
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("locale", localeManager.getLocale(name));
        return "datastore/locale";
    }

    @RequestMapping(value = "/locale/add", method = GET)
    public String add(Model model) {
        model.addAttribute("locale", new Locale());
        return "datastore/locale";
    }

    @RequestMapping(value = "/api/locale", method = GET)
    @ResponseBody
    public Page<Locale> list(
            Pageable page) {
        LOGGER.info("Listing locale");
        return localeManager.localeList(page);
    }

    @RequestMapping(value = "/locale", method = GET)
    public String listing() {
        return "datastore/listlocale";
    }
}
