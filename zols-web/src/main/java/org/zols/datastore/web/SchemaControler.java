/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this jsonSchema file, choose Tools | JsonSchemas
 * and open the jsonSchema in the editor.
 */
package org.zols.datastore.web;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.zols.datastore.service.SchemaService;
import org.zols.datatore.exception.DataStoreException;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 *
 * @author sathish_ku
 */
@RestController
@RequestMapping(value = "/api/schema")
public class SchemaControler {

    private static final Logger LOGGER = getLogger(SchemaControler.class);

    @Autowired
    private SchemaService schemaService;

    @RequestMapping(method = POST)
    public Map<String, Object> create(@RequestBody Map<String, Object> jsonSchema) throws DataStoreException {
        LOGGER.info("Creating new jsonSchemas {}", jsonSchema);
        return schemaService.create(jsonSchema);
    }

    @RequestMapping(value = "/{id}", method = GET)
    public Map<String, Object> read(@PathVariable(value = "id") String id, @RequestParam(value = "enlarged",required = false) String enlarged) throws DataStoreException {
        LOGGER.info("Getting jsonSchema ", id);
        return (enlarged == null ) ? schemaService.read(id) : schemaService.readEnlargedSchema(id);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "id") String id,
            @RequestBody Map<String, Object> jsonSchema) throws DataStoreException {

        LOGGER.info("Updating jsonSchemas with id {} with {}", id, jsonSchema);
        schemaService.update(id,jsonSchema);

    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "id") String id) throws DataStoreException {
        LOGGER.info("Deleting jsonSchemas with id {}", id);
        schemaService.delete(id);
    }

    @RequestMapping(method = GET)
    public List<Map<String, Object>> list() throws DataStoreException {
        LOGGER.info("Getting JsonSchemas ");
        return schemaService.list();
    }
}
