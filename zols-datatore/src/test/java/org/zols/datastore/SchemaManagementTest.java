/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zols.datastore.elasticsearch.ElasticSearchDataStore;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonData;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchema;
import org.zols.datatore.exception.DataStoreException;

/**
 *
 * This class will test Schema Management functionalities provided by Data Store
 */
public class SchemaManagementTest {

    private final DataStore dataStore;

    public SchemaManagementTest() {
        dataStore = new ElasticSearchDataStore();
    }

    @Before
    public void beforeTest() throws DataStoreException {
        dataStore.createSchema(sampleJsonSchema("vechicle"));
    }

    @After
    public void afterTest() throws DataStoreException {
        dataStore.deleteSchema("vechicle");
        dataStore.deleteSchema("car");
    }

    @Test
    public void testCreateSchema() throws DataStoreException {
        Assert.assertNotNull("Creating Simple Schema", dataStore.getSchema("vechicle"));
    }

    @Test
    public void testListSchema() throws DataStoreException {
        Assert.assertEquals("Creating Simple Schema", 1, dataStore.listSchema().size());
    }

    @Test
    public void testDeleteSchema() throws DataStoreException {
        dataStore.deleteSchema("vechicle");
        Assert.assertNull("Deleted Simple Schema", dataStore.getSchema("vechicle"));
    }

    @Test
    public void testGetSchemaWithDefinitions() throws DataStoreException {
        dataStore.createSchema(sampleJsonSchema("car"));
        Map<String, Object> carSchemaWithDefinitions = dataStore.getSchema("car", true);
        Assert.assertNull("Getting Schema with Defenisions", 
                jsonSchema(carSchemaWithDefinitions)
                .validate(sampleJsonData("car")));
    }

}