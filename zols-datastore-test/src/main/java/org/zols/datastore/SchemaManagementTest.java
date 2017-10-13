/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.createAllSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.deleteAllSchema;
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
        createAllSchema(dataStore);

    }

    @After
    public void afterTest() throws DataStoreException {
        deleteAllSchema(dataStore);
        
    }

    @Test
    public void testCreateSchema() throws DataStoreException {
        Assert.assertNotNull("Test Create Schema", dataStore.getSchema("product"));
    }
    
    @Test
    public void testUpdateSchema() throws DataStoreException {
        Map<String,Object> schema = dataStore.getSchema("product");
        schema.put("description", "Updated description");
        dataStore.updateSchema(new JSONObject(schema).toString());
        Assert.assertEquals("Test Update Schema","Updated description", dataStore.getSchema("product").get("description"));
    }

    @Test
    public void testGetChildrenSchema() throws DataStoreException {
        List<Map<String, Object>> children = dataStore.listChildSchema("product");
        Assert.assertEquals("Listing children Schema", 1, children.size());

    }
    
    @Test
    public void testGetExtentins() throws DataStoreException {
        List<Map<String, Object>> children = dataStore.listExtenstions("device");
        Assert.assertEquals("Listing children Schema", 2, children.size());

    }

}