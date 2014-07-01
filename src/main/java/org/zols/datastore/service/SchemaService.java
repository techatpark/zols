/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this schema file, choose Tools | JsonSchemas
 * and open the schema in the editor.
 */
package org.zols.datastore.service;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import java.util.List;
import org.jodel.store.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



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
    public JsonSchema create(JsonSchema schema) {
        JsonSchema createdJsonSchema = null;
        if (schema != null) {
            createdJsonSchema = dataStore.create(schema);
            LOGGER.info("Created JsonSchema  {}", createdJsonSchema.getId());
        }
        return createdJsonSchema;
    }

    /**
     * Get the JsonSchema with given String
     *
     * @param schemaName String to be Search
     * @return searched JsonSchema
     */
    public JsonSchema read(String schemaName) {
        LOGGER.info("Reading JsonSchema  {}", schemaName);
        return dataStore.read(schemaName);
    }

    /**
     * Update a JsonSchema with given Object
     *
     * @param schema Object to be update
     * @return
     */
    public Boolean update(JsonSchema schema) {
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
     * @return
     */
    public Boolean delete(String schemaName) {
        LOGGER.info("Deleting JsonSchema  {}", schemaName);
        return dataStore.delete(schemaName);
    }

    /**
     * List all JsonSchemas
     *
     * @return
     */
    public List<JsonSchema> list() {
        LOGGER.info("Getting JsonSchemas ");
        return dataStore.list();
    }
}
