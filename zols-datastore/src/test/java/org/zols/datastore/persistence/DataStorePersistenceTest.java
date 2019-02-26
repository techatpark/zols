/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.persistence;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.getJsonSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;
import org.zols.datastore.query.MapQuery;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;

/**
 *
 * @author sathish
 */
public class DataStorePersistenceTest {

    JsonSchema jsonSchema = getJsonSchema("product");

    private final DataStorePersistence dataStorePersistence;

    public DataStorePersistenceTest() {

        HazelcastInstance hazelcastInstance
                = Hazelcast.newHazelcastInstance();
        this.dataStorePersistence = new HazelcastDataStorePersistence(hazelcastInstance);
    }

    @BeforeEach
    public void beforeEach() throws DataStoreException {
        Map<String, Object> product = sampleJson("product");
        dataStorePersistence.create(jsonSchema, product);
    }

    @AfterEach
    public void afterEach() throws DataStoreException {
        dataStorePersistence.delete(jsonSchema, new SimpleEntry("id", "1"));
    }

    @Test
    public void testBasicCreate() throws DataStoreException {
        assertNotNull(dataStorePersistence.read(jsonSchema, new SimpleEntry("id", "1")), "Basic Object Creation");
    }
    
    @Test
    public void testBasicList() throws DataStoreException {
        Map<String, Object> product = sampleJson("product");
        product.put("id", 2);
        product.put("title", "Mac Laptop");
        dataStorePersistence.create(jsonSchema, product);
//        List<Map<String, Object>> list = dataStorePersistence.list(jsonSchema, new MapQuery().string("title").eq("Mac Laptop"));
//        assertEquals(1,list.size(), "Basic Search Creation");
    }

}
