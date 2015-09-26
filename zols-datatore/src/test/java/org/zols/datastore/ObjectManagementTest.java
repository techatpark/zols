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
import org.zols.datastore.model.Employee;
import static org.zols.datastore.util.TestUtil.testDataStore;
import org.zols.datatore.exception.DataStoreException;

public class ObjectManagementTest {

    private final DataStore dataStore;

    public ObjectManagementTest() {
        dataStore = testDataStore();
    }

    @Before
    public void beforeTest() throws DataStoreException {
        Employee employee = new Employee();
        employee.setName("Sathish");
        employee.setSalary(10000);
        employee.setIsContractor(Boolean.TRUE);
        dataStore.create(employee);
    }

    @After
    public void afterTest() throws DataStoreException {
        dataStore.delete(Employee.class, "Sathish");
    }

    @Test
    public void testCreateObject() throws DataStoreException {
        Assert.assertNotNull("Creating Simple Object", dataStore.read(Employee.class, "Sathish"));
    }

    @Test
    public void testUpdateObject() throws DataStoreException {
        Employee employee = dataStore.read(Employee.class, "Sathish");
        employee.setSalary(2000);
        dataStore.update(employee, "Sathish");
        employee = dataStore.read(Employee.class, "Sathish");
        Assert.assertEquals("Updating Simple Object", 2000, employee.getSalary());
    }
    
    @Test
    public void testDeleteObject() throws DataStoreException {
        dataStore.delete(Employee.class, "Sathish");
        Assert.assertNull("Deleting Simple Object", dataStore.read(Employee.class, "Sathish"));
    }
    
    @Test
    public void testDeleteAllObject() throws DataStoreException {
        dataStore.delete(Employee.class);
        Assert.assertNull("Deleting Simple Objects", dataStore.read(Employee.class, "Sathish"));
    }
    
    @Test
    public void testListObject() throws DataStoreException {        
        Assert.assertEquals("Listing Simple Object", 1, dataStore.list(Employee.class).size());
    }
}
