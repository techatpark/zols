/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this schema file, choose Tools | JsonSchemas
 * and open the schema in the editor.
 */
package org.zols.datastore.service;

import java.util.List;
import java.util.Map;
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zols.datatore.exception.DataStoreException;



/**
 *
 * @author sathish_ku
 */
@Service
public class SchemaService {

    private static final Logger LOGGER = getLogger(SchemaService.class);

    @Autowired
    private DataStore dataStore;

    /**
     * Creates a new JsonSchema with given Object
     *
     * @param schema Object to be Create
     * @return created JsonSchema object
     */
    public String create(Map<String,Object> schema) throws DataStoreException {
        String createdJsonSchema = null;
        if (schema != null) {
            createdJsonSchema = dataStore.create(schema);
            LOGGER.info("Created JsonSchema  {}", createdJsonSchema);
        }
        return createdJsonSchema;
    }

    /**
     * Get the JsonSchema with given String
     *
     * @param schemaName String to be Search
     * @return searched JsonSchema
     */
    public Map<String,Object> read(String schemaName) throws DataStoreException {
        LOGGER.info("Reading JsonSchema  {}", schemaName);
        return dataStore.readAsMap(schemaName);
    }

    /**
     * Update a JsonSchema with given Object
     *
     * @param schema Object to be update
     * @return status of the Update Operation
     */
    public Boolean update(String schema) throws DataStoreException {
        Boolean updated = false;
        if (schema != null) {
            LOGGER.info("Updating JsonSchema  {}", schema);
            updated = dataStore.update(schema);
        }
        return updated;
    }

    /**
     * Delete a JsonSchema with given String
     *
     * @param schemaName String to be delete
     * @return status of the Delete Operation
     */
    public Boolean delete(String schemaName) throws DataStoreException {
        LOGGER.info("Deleting JsonSchema  {}", schemaName);
        return dataStore.delete(schemaName);
    }

    /**
     * List all JsonSchemas
     *
     * @return list of schema
     */
    public List<Map<String,Object>> list() throws DataStoreException {
        LOGGER.info("Getting JsonSchemas ");
        return dataStore.list();
    }
}
