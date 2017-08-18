/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchemaText;
import org.zols.datastore.util.DataStoreProvider;
import org.zols.datatore.exception.DataStoreException;

/**
 *
 * This class will test Schema Management functionalities provided by Data Store
 */
public class SchemaManagementTest {

    private final DataStore dataStore;

    public SchemaManagementTest() {
        dataStore = DataStoreProvider.getDataStore();
    }

    @Before
    public void beforeTest() throws DataStoreException {
        dataStore.createSchema(sampleJsonSchemaText("address"));
        dataStore.createSchema(sampleJsonSchemaText("person"));
        dataStore.createSchema(sampleJsonSchemaText("teacher"));
        dataStore.createSchema(sampleJsonSchemaText("headmaster"));
        dataStore.createSchema(sampleJsonSchemaText("ceo"));
    }

    @After
    public void afterTest() throws DataStoreException {
        dataStore.delete("address");
        dataStore.deleteSchema(sampleJsonSchemaText("address"));
        dataStore.delete("person");
        dataStore.deleteSchema(sampleJsonSchemaText("person"));
        dataStore.delete("teacher");
        dataStore.deleteSchema(sampleJsonSchemaText("teacher"));
        dataStore.delete("headmaster");
        dataStore.deleteSchema(sampleJsonSchemaText("headmaster"));
        
        dataStore.delete("ceo");
        dataStore.deleteSchema(sampleJsonSchemaText("ceo"));
        
    }

    @Test
    public void testGetRawJsonSchema() throws DataStoreException {
        Map<String, Object> rawJsonSchema = dataStore.getRawJsonSchema("headmaster");
        Assert.assertEquals("Getting Raw Schema for Complex Type", sampleJsonSchema("raw/headmaster"), rawJsonSchema);
    }


    @Test
    public void testGetChildrenSchema() throws DataStoreException {
        List<Map<String, Object>> children = dataStore.listChildSchema("person");
        Assert.assertEquals("Listing children Schema", 1, children.size());

    }
    
    @Test
    public void testGetExtentins() throws DataStoreException {
        List<Map<String, Object>> children = dataStore.listExtenstions("teacher");
        Assert.assertEquals("Listing children Schema", 2, children.size());

    }

}
