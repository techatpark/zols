/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.persistence;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.getJsonSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;

/**
 *
 * @author sathish
 */
public class DataStorePersistenceTest {

    private final DataStorePersistence dataStorePersistence;

    public DataStorePersistenceTest() {

        HazelcastInstance hazelcastInstance
                = Hazelcast.newHazelcastInstance();
        this.dataStorePersistence = new HazelcastDataStorePersistence(hazelcastInstance);
    }

    @Test
    public void testBasicCreate() throws DataStoreException {
        JsonSchema jsonSchema = getJsonSchema("product");
        Map<String, Object> product = sampleJson("product");
        dataStorePersistence.create(jsonSchema, product);
        //System.out.println(product);
    }

}
