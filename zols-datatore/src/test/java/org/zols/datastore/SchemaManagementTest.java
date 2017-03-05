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
        dataStore.createSchema(sampleJsonSchemaText("address"));
        dataStore.createSchema(sampleJsonSchemaText("person"));
    }

    @After
    public void afterTest() throws DataStoreException {
        dataStore.delete("address");
        dataStore.deleteSchema(sampleJsonSchemaText("address"));
        dataStore.delete("person");
        dataStore.deleteSchema(sampleJsonSchemaText("person"));
    }

    @Test
    public void testCreateSchema() throws DataStoreException {
        Assert.assertTrue("Creating Schemas",dataStore.getSchema("address") != null && dataStore.getSchema("person") != null);
    }

}
