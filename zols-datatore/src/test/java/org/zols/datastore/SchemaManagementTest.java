/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchemaText;
import static org.zols.datastore.util.TestUtil.testDataStore;
import org.zols.datatore.exception.DataStoreException;

/**
 *
 * This class will test Schema Management functionalities provided by Data Store
 */
public class SchemaManagementTest {

    private final DataStore dataStore;

    public SchemaManagementTest() {
        dataStore = testDataStore();
    }

    @Before
    public void beforeTest() throws DataStoreException {
        dataStore.deleteSchema("vechicle");
        dataStore.deleteSchema("car");
        dataStore.deleteSchema("sportscar");
        dataStore.deleteSchema("insurance");
        dataStore.createSchema(sampleJsonSchemaText("vechicle"));
    }

    @After
    public void afterTest() throws DataStoreException {
        dataStore.deleteSchema("vechicle");
        dataStore.deleteSchema("car");
        dataStore.deleteSchema("sportscar");
        dataStore.deleteSchema("insurance");
    }

    @Test
    public void testCreateSchema() throws DataStoreException {
        Assert.assertNotNull("Creating Simple Schema", dataStore.getSchema("vechicle"));
    }

    @Test
    public void testCreateChildSchema() throws DataStoreException {
        dataStore.createSchema(sampleJsonSchemaText("insurance"));
        dataStore.createSchema(sampleJsonSchemaText("car"));
        Assert.assertNotNull("Creating Simple Schema", dataStore.getSchema("car"));
    }

    @Test
    public void testCreateInvalidChildSchema() throws DataStoreException {
        // No Insurance. So car can not be created
        dataStore.createSchema(sampleJsonSchemaText("car"));
        Assert.assertNull("Creating Simple Schema", dataStore.getSchema("car"));
    }

    @Test
    public void testListSchema() throws DataStoreException {
        Assert.assertEquals("Listing Schema", 1, dataStore.listSchema().size());
    }

    @Test
    public void testDeleteSchema() throws DataStoreException {
        dataStore.deleteSchema("vechicle");
        Assert.assertNull("Deleted Simple Schema", dataStore.getSchema("vechicle"));
    }

    @Test
    public void testGetSchemaWithDefinitions() throws DataStoreException {
        dataStore.createSchema(sampleJsonSchemaText("insurance"));
        dataStore.createSchema(sampleJsonSchemaText("car"));
        Assert.assertNull("Getting Schema with Defenisions",
                dataStore.validate("car", sampleJson("car")));
    }
    
    @Test
    public void testGetEnlargedSchema() throws DataStoreException {
        dataStore.createSchema(sampleJsonSchemaText("insurance"));
        dataStore.createSchema(sampleJsonSchemaText("car"));
        Assert.assertNotNull("Getting Enlarged Schema with Defenisions",
                dataStore.getEnlargedSchema("car"));
    }

    @Test
    public void testGetSchemaWithMultiLevelInheritance() throws DataStoreException {
        dataStore.createSchema(sampleJsonSchemaText("insurance"));
        dataStore.createSchema(sampleJsonSchemaText("car"));
        dataStore.createSchema(sampleJsonSchemaText("sportscar"));

        Assert.assertNull("Getting Schema with Multi Level Inheritance",
                dataStore.validate("sportscar", sampleJson("sportscar")));
    }

//    @Test
//    public void testGetSchemaWithInvalidMultiLevelInheritance() throws DataStoreException {
//        dataStore.createSchema(sampleJsonSchemaText("insurance"));
//        dataStore.createSchema(sampleJsonSchemaText("car"));
//        dataStore.createSchema(sampleJsonSchemaText("sportscar"));
//
//        Assert.assertNotNull("Invalid Schema with Multi Level Inheritance",
//                dataStore.validate("sportscar", sampleJson("sportscar_invalid")));
//    }

}
