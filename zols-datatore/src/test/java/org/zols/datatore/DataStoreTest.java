/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datatore;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zols.datastore.DataStore;
import org.zols.datastore.elasticsearch.ElasticSearchDataStore;

/**
 *
 * @author sathish
 */
public class DataStoreTest {

    private final DataStore dataStore;

    public DataStoreTest() {
        dataStore = new ElasticSearchDataStore();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of create method, of class DataStore.
     */
    @Test
    public void testCreate_String_Map() {

    }

    /**
     * Test of read method, of class DataStore.
     */
    @Test
    public void testRead_String_String() {

    }

    /**
     * Test of update method, of class DataStore.
     */
    @Test
    public void testUpdate_String_Map() {

    }

    /**
     * Test of delete method, of class DataStore.
     */
    @Test
    public void testDelete_String_String() {

    }

    /**
     * Test of list method, of class DataStore.
     */
    @Test
    public void testList_String() {
    }

    /**
     * Test of create method, of class DataStore.
     */
    @Test
    public void testCreate_Class_Object() {
    }

    /**
     * Test of read method, of class DataStore.
     */
    @Test
    public void testRead_Class_String() {
    }

    /**
     * Test of list method, of class DataStore.
     */
    @Test
    public void testList_Class() {

    }

    /**
     * Test of list method, of class DataStore.
     */
    @Test
    public void testList_Class_Query() {

    }

    /**
     * Test of list method, of class DataStore.
     */
    @Test
    public void testList_3args() {

    }

    /**
     * Test of update method, of class DataStore.
     */
    @Test
    public void testUpdate_Object() {
    }

    /**
     * Test of delete method, of class DataStore.
     */
    @Test
    public void testDelete_Class_String() {
    }

    /**
     * Test of delete method, of class DataStore.
     */
    @Test
    public void testDelete_Class_Query() {
    }

    /**
     * Test of getIdField method, of class DataStore.
     */
    @Test
    public void testGetIdField_String() {
    }

    /**
     * Test of getId method, of class DataStore.
     */
    @Test
    public void testGetId() {
    }

    /**
     * Test of getIdField method, of class DataStore.
     */
    @Test
    public void testGetIdField_JsonSchema() {
    }

    /**
     * Test of create method, of class DataStore.
     */
    @Test
    public void testCreate_Map() {
    }

    /**
     * Test of read method, of class DataStore.
     */
    @Test
    public void testRead_String() {
    }

    /**
     * Test of readAsMap method, of class DataStore.
     */
    @Test
    public void testReadAsMap() {
    }

    /**
     * Test of delete method, of class DataStore.
     */
    @Test
    public void testDelete_String() {

    }

    /**
     * Test of update method, of class DataStore.
     */
    @Test
    public void testUpdate_String() {
    }

    /**
     * Test of list method, of class DataStore.
     */
    @Test
    public void testList_0args() {
    }

    /**
     * Test of createData method, of class DataStore.
     */
    @Test
    public void testCreateData() {
    }

    /**
     * Test of readData method, of class DataStore.
     */
    @Test
    public void testReadData() {

    }

    /**
     * Test of deleteData method, of class DataStore.
     */
    @Test
    public void testDeleteData_String_String() {

    }

    /**
     * Test of deleteData method, of class DataStore.
     */
    @Test
    public void testDeleteData_String_Query() {
    }

    /**
     * Test of updateData method, of class DataStore.
     */
    @Test
    public void testUpdateData() {
    }

    /**
     * Test of listData method, of class DataStore.
     */
    @Test
    public void testListData_String() {
    }

    /**
     * Test of listData method, of class DataStore.
     */
    @Test
    public void testListData_String_Query() {
    }

}
