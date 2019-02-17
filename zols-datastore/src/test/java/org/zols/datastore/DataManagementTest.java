/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import org.zols.datastore.query.Page;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.createAllData;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.createAllSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.deleteAllSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;
import org.zols.datastore.query.Filter;
import static org.zols.datastore.query.Filter.Operator.EQUALS;
import static org.zols.datastore.query.Filter.Operator.EXISTS_IN;
import org.zols.datastore.query.Query;
import org.zols.datastore.util.DataStoreProvider;
import org.zols.datatore.exception.DataStoreException;

public class DataManagementTest {

    private final DataStore dataStore;

    public DataManagementTest() {
        dataStore = DataStoreProvider.getDataStore();
    }

    /*
    @Before
    public void beforeTest() throws DataStoreException {
       createAllSchema(dataStore);
        createAllData(dataStore);

    }

    @After
    public void afterTest() throws DataStoreException {
        deleteAllSchema(dataStore);
    }

    @Test
    public void testCreate() throws DataStoreException {
        Assert.assertNotNull("Creating Simple Data", dataStore.read("computer", "1"));
    }

    @Test
    public void testCreateLocalized() throws DataStoreException {
        Map<String, Object> computer = sampleJson("computer");
        computer.put("id", 2);
        computer.put("title", "Taiwan Title");
        dataStore.create("computer", computer, Locale.TAIWAN);
        Assert.assertEquals("Creating Localized Data", "Taiwan Title", dataStore.read("computer", "2", Locale.TAIWAN).get().get("title"));
    }

    @Test
    public void testReadLocalized() throws DataStoreException {
        Map<String, Object> computer = sampleJson("computer");
        computer.put("id", 2);
        computer.put("title", "Normal Title");
        dataStore.create("computer", computer);
        computer.put("title", "Taiwan Title");
        dataStore.update("computer", "2",computer,  Locale.TAIWAN);
        Assert.assertEquals("Reading Localized Data", "Normal Title", dataStore.read("computer", "2").get().get("title"));
        Assert.assertEquals("Reading Localized Data", "Taiwan Title", dataStore.read("computer", "2", Locale.TAIWAN).get().get("title"));
    }
    


    @Test
    public void testListLocalized() throws DataStoreException {
        Map<String, Object> computer = sampleJson("computer");
        computer.put("id", 2);
        computer.put("title", "Normal Title");
        dataStore.create("computer", computer);
        computer.put("title", "Taiwan Title");
        dataStore.update("computer", "2",computer, Locale.TAIWAN);

        Page<Map<String, Object>> page = dataStore.list("computer", 0, 10);

        Page<Map<String, Object>> taiwanPage = dataStore.list("computer", Locale.TAIWAN, 0, 10);

        Assert.assertNotEquals("Listing localized data", page.getContent(), taiwanPage.getContent());
    }

    
    @Test
    public void testUpdate() throws DataStoreException {
        Map<String, Object> computer = dataStore.read("computer", "1").get();
        computer.put("title", "Changed");
        dataStore.update("computer", "1",computer);
        computer = dataStore.read("computer", "1").get();
        Assert.assertEquals("Updating Simple Data", "Changed", computer.get("title"));
    }

    @Test
    public void testPartialUpdate() throws DataStoreException {
        Map<String, Object> computer = new HashMap<>();
        computer.put("title", "Changed");
        dataStore.updatePartial("computer", computer,"1");
        Map<String, Object> map = dataStore.read("computer", "1").get();
        Assert.assertEquals("Partially Updating Simple Data", "Changed", map.get("title"));
    }

    @Test
    public void testDelete() throws DataStoreException {
        dataStore.delete("computer", "1");
        Assert.assertFalse("Deleting Simple Data", dataStore.read("computer", "1").isPresent());
    }

    @Test
    public void testDeleteAllOfBasicType() throws DataStoreException {
        dataStore.delete("product");
        Assert.assertNull("Deleting All the data of a type", dataStore.list("product"));
        Assert.assertNull("Deleting All the data of a type should not affect child type", dataStore.list("mobile"));
    }

    @Test
    public void testDeleteAllOfChildType() throws DataStoreException {
        dataStore.delete("computer");
        Assert.assertNotNull("Deleting All the data of only a child type", dataStore.list("mobile"));
        dataStore.delete("device");
        Assert.assertNull("Deleting All the data of a child type", dataStore.list("mobile"));
    }

    @Test
    public void testList() throws DataStoreException {
        Assert.assertEquals("Listing Simple Data", 2, dataStore.list("product").size());
    }

    @Test
    public void testListDataWithQuery() throws DataStoreException {
        Query query = new Query();
        query.addFilter(new Filter("os", EQUALS, "ios"));
        Assert.assertEquals("Listing Simple Data with valid query", 1, dataStore.list("mobile", query).size());
    }

    @Test
    public void testListDataWithExistsInQueryWithChildType() throws DataStoreException {

        Query query = new Query();
        query.addFilter(new Filter("tags", EXISTS_IN, Arrays.asList("Electronics")));
        Assert.assertEquals("Listing Simple Data with valid Exists In query on Child Type", 2, dataStore.list("product", query).size());

        query = new Query();
        query.addFilter(new Filter("tags", EXISTS_IN, Arrays.asList("Electronics")));
        Assert.assertEquals("Listing Simple Data with valid Exists In query on Child Type", 1, dataStore.list("mobile", query).size());

        query = new Query();
        query.addFilter(new Filter("tags", EXISTS_IN, Arrays.asList("Personal")));
        Assert.assertEquals("Listing Simple Data with valid Exists In query on Child Type", 1, dataStore.list("product", query).size());
    }
     */
}
