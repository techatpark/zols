/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zols.datastore.elasticsearch.ElasticSearchDataStore;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchemaText;
import org.zols.datastore.model.Employee;
import org.zols.datatore.exception.DataStoreException;

public class DataManagementTest {

    private final DataStore dataStore;

    public DataManagementTest() {
        dataStore = new ElasticSearchDataStore();
    }

    @Before
    public void beforeTest() throws DataStoreException {
        dataStore.createSchema(sampleJsonSchemaText("employee"));
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Sathish");
        map.put("salary", 1000);
        map.put("isContractor", true);
        dataStore.create("employee", map);
    }

    @After
    public void afterTest() throws DataStoreException {
        dataStore.delete("employee", "Sathish");
        dataStore.deleteSchema("employee");
    }

    @Test
    public void testCreateData() throws DataStoreException {
        Assert.assertNotNull("Creating Simple Data", dataStore.read("employee", "Sathish"));
    }

    @Test
    public void testUpdateData() throws DataStoreException {
        Map<String, Object> map = dataStore.read("employee", "Sathish");
        map.put("salary", 2000);
        dataStore.update("employee", map);
        map = dataStore.read("employee", "Sathish");
        Assert.assertEquals("Updating Simple Data", 2000, (int) map.get("salary"));
    }

    @Test
    public void testDeleteData() throws DataStoreException {
        dataStore.delete("employee", "Sathish");
        Assert.assertNull("Deleting Simple Data", dataStore.read("employee", "Sathish"));
    }

    @Test
    public void testListData() throws DataStoreException {
        Assert.assertEquals("Listing Simple Data", 1, dataStore.list("employee").size());
    }
}
