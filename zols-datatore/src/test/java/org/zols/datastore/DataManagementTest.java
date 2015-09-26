/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchemaText;
import org.zols.datastore.query.Filter;
import static org.zols.datastore.query.Filter.Operator.EQUALS;
import static org.zols.datastore.query.Filter.Operator.EXISTS_IN;
import org.zols.datastore.query.Query;
import static org.zols.datastore.util.TestUtil.testDataStore;
import org.zols.datatore.exception.DataStoreException;

public class DataManagementTest {

    private final DataStore dataStore;

    public DataManagementTest() {
        dataStore = testDataStore();
    }

    @Before
    public void beforeTest() throws DataStoreException {
        dataStore.createSchema(sampleJsonSchemaText("employee"));
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Sathish");
        map.put("salary", 1000);
        map.put("department", "IT");
        map.put("isContractor", true);
        dataStore.create("employee", map);
    }

    @After
    public void afterTest() throws DataStoreException {
        dataStore.delete("employee");
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
    public void testDeleteAllData() throws DataStoreException {
        dataStore.delete("employee");
        Assert.assertNull("Deleting Simple Data", dataStore.read("employee", "Sathish"));
    }

    @Test
    public void testListData() throws DataStoreException {
        Assert.assertEquals("Listing Simple Data", 1, dataStore.list("employee").size());
    }

    @Test
    public void testListDataWithQuery() throws DataStoreException {
        Query query = new Query();
        query.addFilter(new Filter("name", EQUALS, "Sathish"));
        Assert.assertEquals("Listing Simple Data with valid query", 1, dataStore.list("employee", query).size());
    }

    @Test
    public void testListDataWithExistsInQuery() throws DataStoreException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Rajan");
        map.put("salary", 2000);
        map.put("isContractor", false);
        map.put("department", "CSE");
        dataStore.create("employee", map);
        List<String> departments = new ArrayList<>();
        departments.add("IT");
        departments.add("CSE");
        Query query = new Query();
        query.addFilter(new Filter("department", EXISTS_IN, departments));
        Assert.assertEquals("Listing Simple Data with valid Exists In query", 2, dataStore.list("employee", query).size());
    }

    @Test
    public void testListDataWithInvalidQuery() throws DataStoreException {
        Query query = new Query();
        query.addFilter(new Filter("name", EQUALS, "Saravana"));
        Assert.assertEquals("Listing Simple Data with valid query", 0, dataStore.list("employee", query).size());
    }
}
