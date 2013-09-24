/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.springframework.web.bind.annotation.ResponseBody;

import org.zols.datastore.DataStore;
import org.zols.datastore.model.BaseObject;
import org.zols.datastore.model.Entity;
import org.zols.datastore.util.DynamicBeanGenerator;

/**
 *
 * Data Level Controller
 */
@Controller
@RequestMapping(value = "/data")
public class DataController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DataController.class);
    
    @Autowired
    private DataStore dataStore;
    
    @Autowired
    private DynamicBeanGenerator beanGenerator;

    @RequestMapping(value = "/{entityName}", method = POST)
    @ResponseBody
    public BaseObject create(
            @PathVariable(value = "entityName") String entityName,
            @RequestBody String jsonString) {
        LOGGER.info("Creating new baseObject {}", jsonString);
        BaseObject baseObject = beanGenerator.getBaseObject("basic", jsonString);
        return dataStore.create(baseObject, BaseObject.class);
    }
}
