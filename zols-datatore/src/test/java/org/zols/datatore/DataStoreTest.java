/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datatore;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.zols.datastore.query.Query;

/**
 *
 * @author sathish
 */
public class DataStoreTest {

    private DataStore dataStore;

    public DataStoreTest() {
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
        System.out.println("create");
        String schemaName = "";
        Map<String, Object> jsonData = null;

        Map<String, Object> expResult = null;
        Map<String, Object> result = dataStore.create(schemaName, jsonData);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of read method, of class DataStore.
     */
    @Test
    public void testRead_String_String() {
        System.out.println("read");
        String schemaName = "";
        String name = "";

        Map<String, Object> expResult = null;
        Map<String, Object> result = dataStore.read(schemaName, name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class DataStore.
     */
    @Test
    public void testUpdate_String_Map() {
        System.out.println("update");
        String schemaName = "";
        Map<String, Object> jsonData = null;

        boolean expResult = false;
        boolean result = dataStore.update(schemaName, jsonData);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of delete method, of class DataStore.
     */
    @Test
    public void testDelete_String_String() {
        System.out.println("delete");
        String schemaName = "";
        String name = "";

        boolean expResult = false;
        boolean result = dataStore.delete(schemaName, name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of list method, of class DataStore.
     */
    @Test
    public void testList_String() {
        System.out.println("list");
        String schemaName = "";

        List<Map<String, Object>> expResult = null;
        List<Map<String, Object>> result = dataStore.list(schemaName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of create method, of class DataStore.
     */
    @Test
    public void testCreate_Class_Object() {
        System.out.println("create");

        Object expResult = null;
        Object result = dataStore.create(null);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of read method, of class DataStore.
     */
    @Test
    public void testRead_Class_String() {
        System.out.println("read");

        Object expResult = null;
        Object result = dataStore.read(null);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
        System.out.println("update");
        Object object = null;

        boolean expResult = false;
        boolean result = dataStore.update(object);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of delete method, of class DataStore.
     */
    @Test
    public void testDelete_Class_String() {
        System.out.println("delete");
        Class clazz = null;
        String name = "";

        boolean expResult = false;
        boolean result = dataStore.delete(clazz, name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of delete method, of class DataStore.
     */
    @Test
    public void testDelete_Class_Query() {
        System.out.println("delete");
        Class clazz = null;
        Query query = null;

        boolean expResult = false;
        boolean result = dataStore.delete(clazz, query);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIdField method, of class DataStore.
     */
    @Test
    public void testGetIdField_String() {
        System.out.println("getIdField");
        String jsonSchema = "";

        String expResult = "";
        String result = dataStore.getIdField(jsonSchema);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getId method, of class DataStore.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        String jsonSchema = "";

        String expResult = "";
        String result = dataStore.getId(jsonSchema);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIdField method, of class DataStore.
     */
    @Test
    public void testGetIdField_JsonSchema() {
        System.out.println("getIdField");
        JsonSchema jsonSchema = null;

        String expResult = "";
        String result = dataStore.getIdField(jsonSchema);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of create method, of class DataStore.
     */
    @Test
    public void testCreate_Map() {
        System.out.println("create");
        Map<String, Object> jsonSchemaObject = null;

        String expResult = "";
        String result = dataStore.create(jsonSchemaObject);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of read method, of class DataStore.
     */
    @Test
    public void testRead_String() {
        System.out.println("read");
        String schemaName = "";

        String expResult = "";
        String result = dataStore.read(schemaName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readAsMap method, of class DataStore.
     */
    @Test
    public void testReadAsMap() {
        System.out.println("readAsMap");
        String schemaName = "";

        Map<String, Object> expResult = null;
        Map<String, Object> result = dataStore.readAsMap(schemaName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of delete method, of class DataStore.
     */
    @Test
    public void testDelete_String() {
        System.out.println("delete");
        String schemaName = "";

        boolean expResult = false;
        boolean result = dataStore.delete(schemaName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class DataStore.
     */
    @Test
    public void testUpdate_String() {
        System.out.println("update");
        String jsonSchema = "";

        boolean expResult = false;
        boolean result = dataStore.update(jsonSchema);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of list method, of class DataStore.
     */
    @Test
    public void testList_0args() {
        System.out.println("list");

        List<Map<String, Object>> expResult = null;
        List<Map<String, Object>> result = dataStore.list();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createData method, of class DataStore.
     */
    @Test
    public void testCreateData() {
        System.out.println("createData");
        String jsonSchema = "";
        Map<String, Object> validatedDataObject = null;

        Map<String, Object> expResult = null;
        Map<String, Object> result = dataStore.createData(jsonSchema, validatedDataObject);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readData method, of class DataStore.
     */
    @Test
    public void testReadData() {
        System.out.println("readData");
        String jsonSchema = "";
        String idValue = "";

        Map<String, Object> expResult = null;
        Map<String, Object> result = dataStore.readData(jsonSchema, idValue);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteData method, of class DataStore.
     */
    @Test
    public void testDeleteData_String_String() {
        System.out.println("deleteData");
        String jsonSchema = "";
        String idValue = "";

        boolean expResult = false;
        boolean result = dataStore.deleteData(jsonSchema, idValue);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteData method, of class DataStore.
     */
    @Test
    public void testDeleteData_String_Query() {
        System.out.println("deleteData");
        String jsonSchema = "";
        Query query = null;

        boolean expResult = false;
        boolean result = dataStore.deleteData(jsonSchema, query);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateData method, of class DataStore.
     */
    @Test
    public void testUpdateData() {
        System.out.println("updateData");
        String jsonSchema = "";
        Map<String, Object> validatedDataObject = null;

        boolean expResult = false;
        boolean result = dataStore.updateData(jsonSchema, validatedDataObject);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listData method, of class DataStore.
     */
    @Test
    public void testListData_String() {
        System.out.println("listData");
        String jsonSchema = "";

        List<Map<String, Object>> expResult = null;
        List<Map<String, Object>> result = dataStore.listData(jsonSchema);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listData method, of class DataStore.
     */
    @Test
    public void testListData_String_Query() {
        System.out.println("listData");
        String jsonSchema = "";
        Query query = null;

        List<Map<String, Object>> expResult = null;
        List<Map<String, Object>> result = dataStore.listData(jsonSchema, query);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
