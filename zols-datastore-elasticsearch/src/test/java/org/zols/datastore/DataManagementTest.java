/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.zols.datastore.elasticsearch.ElasticSearchDataStorePersistence;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.createAllData;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.createAllSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.deleteAllSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;
import org.zols.datastore.query.Filter;
import static org.zols.datastore.query.Filter.Operator.EQUALS;
import static org.zols.datastore.query.Filter.Operator.EXISTS_IN;
import org.zols.datastore.query.MapQuery;
import org.zols.datastore.query.Page;
import org.zols.datastore.query.Query;
import org.zols.datatore.exception.DataStoreException;

@RunWith(JUnitPlatform.class)
public class DataManagementTest {

    private final DataStore dataStore;

    public DataManagementTest() {
        dataStore = new DataStore(new ElasticSearchDataStorePersistence());
    }

    @BeforeEach
    public void beforeTest() throws DataStoreException {
        createAllSchema(dataStore);
        createAllData(dataStore);

    }

    @AfterEach
    public void afterTest() throws DataStoreException {
        deleteAllSchema(dataStore);
    }

    @Test
    public void testCreate() throws DataStoreException {
        assertNotNull(dataStore.read("computer", new SimpleEntry("id", "1")), "Creating Simple Data");
    }

    @Test
    public void testCreateLocalized() throws DataStoreException {
        Map<String, Object> computer = sampleJson("computer");
        computer.put("id", 2);
        computer.put("title", "Taiwan Title");
        dataStore.create("computer", computer, Locale.TAIWAN);
        assertEquals("Taiwan Title", dataStore.read("computer", Locale.TAIWAN, new SimpleEntry("id", "2")).get().get("title"), "Creating Localized Data");
    }

    @Test
    public void testReadLocalized() throws DataStoreException {
        Map<String, Object> computer = sampleJson("computer");
        computer.put("id", 2);
        computer.put("title", "Normal Title");
        dataStore.create("computer", computer);
        computer.put("title", "Taiwan Title");
        dataStore.update("computer", computer, Locale.TAIWAN, new SimpleEntry("id", "2"));
        assertEquals("Normal Title", dataStore.read("computer", new SimpleEntry("id", "2")).get().get("title"), "Reading Localized Data");
        assertEquals("Taiwan Title", dataStore.read("computer", Locale.TAIWAN, new SimpleEntry("id", "2")).get().get("title"), "Reading Localized Data");
    }

    @Test
    public void testListLocalized() throws DataStoreException {
        Map<String, Object> computer = sampleJson("computer");
        computer.put("id", 2);
        computer.put("title", "Normal Title");
        dataStore.create("computer", computer);
        computer.put("title", "Taiwan Title");
        dataStore.update("computer", computer, Locale.TAIWAN, new SimpleEntry("id", "2"));

        Page<Map<String, Object>> page = dataStore.list("computer", 0, 10);

        Page<Map<String, Object>> taiwanPage = dataStore.list("computer", Locale.TAIWAN, 0, 10);

        assertNotEquals(page.getContent(), taiwanPage.getContent(), "Listing localized data");
    }

    @Test
    public void testUpdate() throws DataStoreException {
        Map<String, Object> computer = dataStore.read("computer", new SimpleEntry("id", "1")).get();
        computer.put("title", "Changed");
        dataStore.update("computer", computer, new SimpleEntry("id", "1"));
        computer = dataStore.read("computer", new SimpleEntry("id", "1")).get();
        assertEquals("Changed", computer.get("title"), "Updating Simple Data");
    }

    @Test
    public void testPartialUpdate() throws DataStoreException {
        Map<String, Object> computer = new HashMap<>();
        computer.put("title", "Changed");
        dataStore.updatePartial("computer", computer, new SimpleEntry("id", "1"));
        Map<String, Object> map = dataStore.read("computer", new SimpleEntry("id", "1")).get();
        assertEquals("Changed", map.get("title"), "Partially Updating Simple Data");
    }

    @Test
    public void testDelete() throws DataStoreException {
        dataStore.delete("computer", new SimpleEntry("id", "1"));
        assertFalse(dataStore.read("computer", new SimpleEntry("id", "1")).isPresent(), "Deleting Simple Data");
    }

    @Test
    public void testDeleteAllOfBasicType() throws DataStoreException {
        dataStore.delete("product");
        assertNull(dataStore.list("product"), "Deleting All the data of a type");
        assertNull(dataStore.list("mobile"), "Deleting All the data of a type should affect child type");
    }

    @Test
    public void testDeleteAllOfChildType() throws DataStoreException {
        dataStore.delete("computer");
        assertNotNull(dataStore.list("mobile"), "Deleting All the data of only a child type");
        dataStore.delete("device");
        assertNull(dataStore.list("mobile"), "Deleting All the data of a child type");
    }

    @Test
    public void testList() throws DataStoreException {
        assertEquals(2, dataStore.list("product").size(), "Listing Simple Data");
    }

    @Test
    public void testListDataWithQuery() throws DataStoreException {
        assertEquals(1, dataStore.list("mobile", new MapQuery().string("os").eq("ios")).size(), "Listing Simple Data with valid query");
    }

    @Test
    public void testListDataWithExistsInQueryWithChildType() throws DataStoreException {

        assertEquals(2, dataStore.list("product", new MapQuery().string("tags").in("Electronics")).size(), "Listing Simple Data with valid Exists In query on Child Type");

        assertEquals(1, dataStore.list("mobile", new MapQuery().string("tags").in("Electronics")).size(), "Listing Simple Data with valid Exists In query on Child Type");

        assertEquals(1, dataStore.list("product", new MapQuery().string("tags").in("Personal")).size(), "Listing Simple Data with valid Exists In query on Child Type");
    }

}
