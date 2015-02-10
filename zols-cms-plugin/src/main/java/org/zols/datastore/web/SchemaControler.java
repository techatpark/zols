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
import org.zols.datastore.service.SchemaService;
import org.zols.datatore.exception.DataStoreException;

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
    public String create(@RequestBody Map<String,Object> jsonSchema) throws DataStoreException {
        LOGGER.info("Creating new jsonSchemas {}", jsonSchema);
        return schemaService.create(jsonSchema);
    }

    @RequestMapping(value = "/{id}", method = GET)
    public Map<String,Object> read(@PathVariable(value = "id") String id) throws DataStoreException {
        LOGGER.info("Getting jsonSchema ", id);
        return schemaService.read(id);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "id") String id,
            @RequestBody String jsonSchema) throws DataStoreException {

        LOGGER.info("Updating jsonSchemas with id {} with {}", id, jsonSchema);
        schemaService.update(jsonSchema);

    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "id") String id) throws DataStoreException {
        LOGGER.info("Deleting jsonSchemas with id {}", id);
        schemaService.delete(id);
    }

    @RequestMapping(method = GET)
    public List<Map<String,Object>>  list() throws DataStoreException {
        LOGGER.info("Getting JsonSchemas ");
        return schemaService.list();
    }
}
