/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.elasticsearch;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zols.datastore.DataStoreException;
import org.zols.datastore.query.MapQuery;
import org.zols.jsonschema.JsonSchema;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.getJsonSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;

/**
 * @author sathish
 */
public class ElasticSearchDataStorePersistenceTest {

    private final ElasticSearchDataStorePersistence dataStorePersistence;

    private final JsonSchema jsonSchema;

    public ElasticSearchDataStorePersistenceTest() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        dataStorePersistence =
                new ElasticSearchDataStorePersistence("zols", client);
        jsonSchema = getJsonSchema("person");
    }

    @BeforeEach
    public void beforeTest() throws DataStoreException {

        Map<String, Object> personMap = sampleJson("person");
        dataStorePersistence.create(jsonSchema, personMap);
    }

    @AfterEach
    public void afterTest() throws DataStoreException {
        dataStorePersistence.delete(jsonSchema, new SimpleEntry("id", "1"));
    }

    @Test
    public void testCreate() throws DataStoreException {
        assertNotNull(dataStorePersistence.read(jsonSchema,
                new SimpleEntry("id", "1")), "Crated Simple Object");
    }

    @Test
    public void testDelete() throws DataStoreException {
        dataStorePersistence.delete(jsonSchema, new SimpleEntry("id", "1"));
        assertNull(dataStorePersistence.read(jsonSchema,
                new SimpleEntry("id", "1")), "Deleted Simple Object");
    }

    @Test
    public void testUpdate() throws DataStoreException {
        Map<String, Object> personMap = dataStorePersistence.read(jsonSchema,
                new SimpleEntry("id", "1"));
        personMap.remove("age");
        dataStorePersistence.update(jsonSchema, personMap,
                new SimpleEntry("id", "1"));
        assertNull(dataStorePersistence.read(jsonSchema,
                        new SimpleEntry("id", "1")).get("age"),
                "Updated Simple Object");
    }

    @Test
    public void testUpdatePartially() throws DataStoreException {
        Map<String, Object> personMap = new HashMap<>();
        personMap.put("age", 32);
        dataStorePersistence.updatePartially(jsonSchema, personMap,
                new SimpleEntry("id", "1"));

        assertEquals(32, Integer.parseInt(dataStorePersistence.read(jsonSchema,
                        new SimpleEntry("id", "1")).get("age").toString()),
                "Updated Simple Object Partially");

        assertNotNull(dataStorePersistence.read(jsonSchema,
                        new SimpleEntry("id", "1")).get("firstName"),
                "Updated Simple Object");
    }

    @Test
    public void testList() throws DataStoreException {
        assertEquals(1, dataStorePersistence.list(jsonSchema,
                (Condition<MapQuery>) null).size(), "Listing Simple Object");
    }

    @Test
    public void testListWithQuery() throws DataStoreException {
        Map<String, Object> personMap = dataStorePersistence.read(jsonSchema,
                new SimpleEntry("id", "1"));

        assertEquals(1, dataStorePersistence.list(jsonSchema,
                        new MapQuery().intNum("age").eq(35)).size(),
                "Listing Simple Object");

        assertEquals(1, dataStorePersistence.list(jsonSchema,
                        new MapQuery().intNum("age").ne(25)).size(),
                "Listing Simple Object");

        assertEquals(1, dataStorePersistence.list(jsonSchema,
                        new MapQuery().intNum("age").exists()).size(),
                "Listing Simple Object");

        personMap.remove("age");
        dataStorePersistence.update(jsonSchema, personMap,
                new SimpleEntry("id", "1"));
        assertNull(dataStorePersistence.list(jsonSchema,
                        new MapQuery().intNum("age").exists()),
                "Listing Simple Object");

        assertEquals(1, dataStorePersistence.list(jsonSchema,
                        new MapQuery().string("lastName").in("thyagarajan")).size(),
                "Listing Simple Object");

        assertNull(dataStorePersistence.list(jsonSchema,
                        new MapQuery().string("firstName").nin("sathish kumar")),
                "Listing Simple Object");

//        query = new Query();
//
//
//        query = new Query();
//
//        query.addFilter(new Filter("firstName", Filter.Operator.FULL_TEXT_SEARCH, "sathish"));
//        assertEquals(1, dataStorePersistence.list(jsonSchema, query).size(), "Listing Simple Object");
    }
}
