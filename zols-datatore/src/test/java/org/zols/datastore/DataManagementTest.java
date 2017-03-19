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
        dataStore.createSchema(sampleJsonSchemaText("headmaster"));

        dataStore.create("person", sampleJson("person"));
        dataStore.create("teacher", sampleJson("teacher"));
        dataStore.create("headmaster", sampleJson("headmaster"));
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
    public void testDeleteAllOfBasicType() throws DataStoreException {
        dataStore.delete("person");
        Assert.assertNull("Deleting All the data of a type", dataStore.list("person"));
        Assert.assertNull("Deleting All the data of a type should not affect child type", dataStore.list("teacher"));
    }
    
    @Test
    public void testDeleteAllOfChildType() throws DataStoreException {
        dataStore.delete("teacher");
        Assert.assertNull("Deleting All the data of a child type", dataStore.list("teacher"));
    }

    @Test
    public void testList() throws DataStoreException {
        Assert.assertEquals("Listing Simple Data", 2, dataStore.list("teacher").size());
    }

    @Test
    public void testListDataWithQuery() throws DataStoreException {
        Query query = new Query();
        query.addFilter(new Filter("name", EQUALS, "SATHISH"));
        Assert.assertEquals("Listing Simple Data with valid query", 1, dataStore.list("teacher", query).size());
    }

    @Test
    public void testListDataWithExistsInQueryWithChildType() throws DataStoreException {
        List<String> subjects = new ArrayList<>();
        subjects.add("English");
        subjects.add("Science");
        Query query = new Query();
        query.addFilter(new Filter("subject", EXISTS_IN, subjects));
        Assert.assertEquals("Listing Simple Data with valid Exists In query on Child Type", 1, dataStore.list("headmaster", query).size());
    }
    
    @Test
    public void testListDataWithExistsInQueryWithSuperType() throws DataStoreException {
        List<String> subjects = new ArrayList<>();
        subjects.add("English");
        subjects.add("Science");
        Query query = new Query();
        query.addFilter(new Filter("subject", EXISTS_IN, subjects));
        Assert.assertEquals("Listing Simple Data with valid Exists In query on Super Type", 2, dataStore.list("teacher", query).size());
    }

    @Test
    public void testListDataWithInvalidQuery() throws DataStoreException {
        Query query = new Query();
        query.addFilter(new Filter("name", EQUALS, "Saravana"));
        Assert.assertEquals("Listing Simple Data with valid query", null, dataStore.list("teacher", query));
    }

    /**
     * Teacher should be available if i list person
     *
     * @throws DataStoreException
     */
    @Test
    public void testExistanceAsSupertype() throws DataStoreException {

        List<Map<String, Object>> persons = dataStore.list("person");
        List<Map<String, Object>> teachers = dataStore.list("teacher");
        List<Map<String, Object>> headmasters = dataStore.list("headmaster");
        Assert.assertEquals("Test Existance As Base type Person", 3, dataStore.list("person").size());
        Assert.assertEquals("Test Existance As Base type teacher", 2, dataStore.list("teacher").size());
        Assert.assertEquals("Test Existance As Base type headmaster", 1, dataStore.list("headmaster").size());
    }
}
