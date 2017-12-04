/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.elasticsearch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.getJsonSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;
import org.zols.datastore.query.Filter;
import org.zols.datastore.query.Query;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;

/**
 *
 * @author sathish
 */
public class ElasticSearchDataStorePersistenceTest {
    
    private final ElasticSearchDataStorePersistence dataStorePersistence;
    
    private final JsonSchema jsonSchema ;
    
    public ElasticSearchDataStorePersistenceTest() {
        RestHighLevelClient client = new RestHighLevelClient(
        RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9201, "http")));
        dataStorePersistence = new ElasticSearchDataStorePersistence("zols",client);
        jsonSchema = getJsonSchema("person");
    }
    
    @Before
    public void beforeTest() throws DataStoreException {
        
        Map<String,Object> personMap = sampleJson("person");
        dataStorePersistence.create(jsonSchema, personMap);
    }
    
    @After
    public void afterTest() throws DataStoreException {
        Query query = new Query();
        
        query.addFilter(new Filter("age",Filter.Operator.EQUALS, 25));
        dataStorePersistence.delete(jsonSchema, query);
        
        System.out.println("");
    }
    
    @Test
    public void testCreate() throws DataStoreException {
        Assert.assertNotNull("Crated Simple Object",dataStorePersistence.read(jsonSchema, "1"));
    }
    
    @Test
    public void testDelete() throws DataStoreException {
        dataStorePersistence.delete(jsonSchema, "1");
        Assert.assertNull("Deleted Simple Object",dataStorePersistence.read(jsonSchema, "1"));
    }
    
    @Test
    public void testUpdate() throws DataStoreException {
        Map<String,Object> personMap = dataStorePersistence.read(jsonSchema, "1");
        personMap.remove("age");
        dataStorePersistence.update(jsonSchema, "1", personMap);
        Assert.assertNull("Updated Simple Object",dataStorePersistence.read(jsonSchema, "1").get("age"));
    }
    
    @Test
    public void testUpdatePartially() throws DataStoreException {
        Map<String,Object> personMap = new HashMap<>();
        personMap.put("age",32);
        dataStorePersistence.updatePartially(jsonSchema, "1", personMap);
        Assert.assertEquals("Updated Simple Object Partially",32,Integer.parseInt( dataStorePersistence.read(jsonSchema, "1").get("age").toString()));
        Assert.assertNotNull("Updated Simple Object",dataStorePersistence.read(jsonSchema, "1").get("firstName"));
    }
    
    @Test
    public void testList() throws DataStoreException {
         Map<String,Object> personMap = dataStorePersistence.read(jsonSchema, "1");
        
        Query query = new Query();
        
        query.addFilter(new Filter("age",Filter.Operator.EQUALS, 35));
        
        Assert.assertEquals("Listing Simple Object",1,dataStorePersistence.list(jsonSchema, query).size());
        
        query = new Query();
        
        query.addFilter(new Filter("age",Filter.Operator.NOT_EQUALS, 25));
        
        Assert.assertEquals("Listing Simple Object",1,dataStorePersistence.list(jsonSchema, query).size());
        
        query = new Query();
        
        query.addFilter(new Filter("age",Filter.Operator.IS_NULL));
        
        Assert.assertNull("Listing Simple Object",dataStorePersistence.list(jsonSchema, query));
        
       
        personMap.remove("age");
        dataStorePersistence.update(jsonSchema, "1", personMap);
        
        personMap = dataStorePersistence.read(jsonSchema, "1");
        
        Assert.assertEquals("Listing Simple Object",1,dataStorePersistence.list(jsonSchema, query).size());
        
        query = new Query();
        
        query.addFilter(new Filter("lastName",Filter.Operator.EXISTS_IN,Arrays.asList("thyagarajan")));
        Assert.assertEquals("Listing Simple Object",1,dataStorePersistence.list(jsonSchema, query).size());
        
        query = new Query();
        
        query.addFilter(new Filter("firstName",Filter.Operator.NOT_EXISTS_IN,Arrays.asList("sathish kumar")));
        Assert.assertNull("Listing Simple Object",dataStorePersistence.list(jsonSchema, query));
        
        query = new Query();
        
        query.addFilter(new Filter("firstName",Filter.Operator.FULL_TEXT_SEARCH,"sathish"));
        Assert.assertEquals("Listing Simple Object",1,dataStorePersistence.list(jsonSchema, query).size());
        
        
    }
}
