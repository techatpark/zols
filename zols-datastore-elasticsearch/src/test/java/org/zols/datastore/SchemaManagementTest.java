/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.zols.datastore.elasticsearch.ElasticSearchDataStorePersistence;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.createAllSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.deleteAllSchema;
import org.zols.datatore.exception.DataStoreException;

@RunWith(JUnitPlatform.class)
public class SchemaManagementTest {

    private final DataStore dataStore;

    public SchemaManagementTest() {
        dataStore = new DataStore(new ElasticSearchDataStorePersistence());
    }

    @BeforeEach
    public void beforeTest() throws DataStoreException {
        createAllSchema(dataStore);

    }

    @AfterEach
    public void afterTest() throws DataStoreException {
        deleteAllSchema(dataStore);

    }

    @Test
    public void testCreateSchema() throws DataStoreException {
        assertNotNull(dataStore.getSchemaManager().get("product"), "Test Create Schema");
    }

    @Test
    public void testUpdateSchema() throws DataStoreException {
        Map<String, Object> schemaMap = dataStore.getSchemaManager().get("product");
        schemaMap.put("description", "Updated description");
        dataStore.getSchemaManager().update("product", schemaMap);
        assertEquals("Updated description", dataStore.getSchemaManager().get("product").get("description"), "Test Update Schema");
    }

//    @Test
//    public void testGetChildrenSchema() throws DataStoreException {
//
//        List<Map<String, Object>> children = dataStore.getSchemaManager().listChildren("product");
//        assertEquals(1, children.size(), "Listing children Schema");
//
//    }
//
//    @Test
//    public void testGetExtentins() throws DataStoreException {
//        List<Map<String, Object>> children = dataStore.getSchemaManager().listExtenstions("device");
//        assertEquals(2, children.size(), "Listing children Schema");
//
//    }

}
