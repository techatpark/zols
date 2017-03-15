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
import org.junit.Ignore;
import org.junit.Test;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;
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
        dataStore.createSchema(sampleJsonSchemaText("address"));
        dataStore.createSchema(sampleJsonSchemaText("person"));
        dataStore.createSchema(sampleJsonSchemaText("teacher"));
        dataStore.create("teacher", sampleJson("teacher"));
    }

    @After
    public void afterTest() throws DataStoreException {
        dataStore.delete("address");
        dataStore.deleteSchema(sampleJsonSchemaText("address"));
        dataStore.delete("person");
        dataStore.deleteSchema(sampleJsonSchemaText("person"));
        dataStore.delete("teacher");
        dataStore.deleteSchema(sampleJsonSchemaText("teacher"));
    }

    @Test
    public void testCreate() throws DataStoreException {
        Assert.assertNotNull("Creating Simple Data", dataStore.read("teacher", "SATHISH"));
    }

    @Test
    public void testUpdate() throws DataStoreException {
        Map<String, Object> map = dataStore.read("teacher", "SATHISH");
        map.put("subject", "TAMIL");
        dataStore.update("teacher", map);
        map = dataStore.read("teacher", "SATHISH");
        Assert.assertEquals("Updating Simple Data", "TAMIL", map.get("subject"));
    }
    
    @Test
    public void testPartialUpdate() throws DataStoreException {
        Map<String, Object> partialMap = new HashMap<>();
        partialMap.put("subject", "TAMIL");
        dataStore.updatePartial("teacher", "SATHISH", partialMap);
        Map<String, Object> map = dataStore.read("teacher", "SATHISH");
        Assert.assertEquals("Partially Updating Simple Data", "TAMIL", map.get("subject"));
    }

    @Test
    public void testDelete() throws DataStoreException {
        dataStore.delete("teacher", "SATHISH");
        Assert.assertNull("Deleting Simple Data", dataStore.read("teacher", "SATHISH"));
    }

    @Test
    public void testDeleteAll() throws DataStoreException {
        dataStore.delete("teacher");
        Assert.assertNull("Deleting Simple Data", dataStore.read("teacher", "SATHISH"));
    }

    @Test
    public void testList() throws DataStoreException {
        Assert.assertEquals("Listing Simple Data", 1, dataStore.list("teacher").size());
    }

    @Test
    public void testListDataWithQuery() throws DataStoreException {
        Query query = new Query();
        query.addFilter(new Filter("name", EQUALS, "SATHISH"));
        Assert.assertEquals("Listing Simple Data with valid query", 1, dataStore.list("teacher", query).size());
    }

    @Test
    @Ignore
    public void testListDataWithExistsInQuery() throws DataStoreException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Rajan");
        map.put("salary", 2000);
        map.put("isContractor", false);
        map.put("department", "CSE");
        dataStore.create("teacher", map);
        List<String> departments = new ArrayList<>();
        departments.add("IT");
        departments.add("CSE");
        Query query = new Query();
        query.addFilter(new Filter("department", EXISTS_IN, departments));
        Assert.assertEquals("Listing Simple Data with valid Exists In query", 2, dataStore.list("teacher", query).size());
    }

    @Test
    public void testListDataWithInvalidQuery() throws DataStoreException {
        Query query = new Query();
        query.addFilter(new Filter("name", EQUALS, "Saravana"));
        Assert.assertEquals("Listing Simple Data with valid query", null, dataStore.list("teacher", query));
    }
}
