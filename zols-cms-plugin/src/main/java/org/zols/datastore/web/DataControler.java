/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this jsonSchema file, choose Tools | JsonSchemas
 * and open the jsonSchema in the editor.
 */
package org.zols.datastore.web;

import java.util.List;
import java.util.Map;
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
import org.zols.datastore.service.DataService;
import org.zols.datatore.exception.DataStoreException;

/**
 *
 * @author sathish_ku
 */
@RestController
@RequestMapping(value = "/api/data/{schemaId}")
public class DataControler {

    private static final Logger LOGGER = getLogger(DataControler.class);

    @Autowired
    private DataService dataService;

    @RequestMapping(method = POST)
    public Map<String, Object> create(@PathVariable(value = "schemaId") String schemaName,
            @RequestBody Map<String, Object> jsonData) throws DataStoreException {
        LOGGER.info("Creating new instance of {}", schemaName);
        return dataService.create(schemaName, jsonData);
    }
    
    @RequestMapping(value = "/{id}", method = GET)
    public Map<String, Object> read(@PathVariable(value = "schemaId") String schemaName,
            @PathVariable(value = "id") String id) throws DataStoreException {
        LOGGER.info("Getting Data ", id);
        return dataService.read(schemaName,id);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "schemaId") String schemaName,
            @PathVariable(value = "id") String id,
            @RequestBody Map<String, Object> jsonData) throws DataStoreException {
            dataService.update(schemaName,jsonData);
        
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "schemaId") String schemaName,
            @PathVariable(value = "id") String id) throws DataStoreException {
        LOGGER.info("Deleting jsonSchemas with id {}", id);
        dataService.delete(schemaName,id);
    }

    @RequestMapping(method = GET)
    public List<Map<String, Object>> list(@PathVariable(value = "schemaId") String schemaName) throws DataStoreException {
        LOGGER.info("Getting Datas ");
        return dataService.list(schemaName);
    }

}
