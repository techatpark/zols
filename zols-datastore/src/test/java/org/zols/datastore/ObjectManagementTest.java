/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.Locale;
import org.zols.datastore.model.Employee;
import org.zols.datastore.util.DataStoreProvider;
import org.zols.datatore.exception.DataStoreException;

public class ObjectManagementTest {

    private final DataStore dataStore;
    private final ObjectManager<Employee> employeeManager;

    public ObjectManagementTest() {
        dataStore = DataStoreProvider.getDataStore();
        employeeManager = dataStore.getObjectManager(Employee.class);
    }

    /*
    @Before
    public void beforeTest() throws DataStoreException {
        Employee employee = new Employee();
        employee.setName("Sathish");
        employee.setCity("Madurai");
        employee.setSalary(10000);
        employee.setIsContractor(Boolean.TRUE);
        employeeManager.create(employee);
        
        Employee employeeLocalized = new Employee();
        employeeLocalized.setName("SathishLocal");
        employeeLocalized.setCity("மதுரை");
        employeeLocalized.setSalary(10000);
        employeeLocalized.setIsContractor(Boolean.TRUE);
        employeeManager.create(employeeLocalized,Locale.CHINESE);
    }

    @After
    public void afterTest() throws DataStoreException {
        employeeManager.delete("Sathish");
    }

    @Test
    public void testCreateObject() throws DataStoreException {
        assertTrue("Creating Simple Object", employeeManager.read("Sathish").isPresent());
    }

    @Test
    public void testUpdateObject() throws DataStoreException {
        Employee employee = employeeManager.read("Sathish").get();
        employee.setSalary(2000);
        employee = employeeManager.update(employee, "Sathish");
        assertEquals("Updating Simple Object", 2000, employee.getSalary());
    }
    
    @Test
    public void testUpdateLocalizedObject() throws DataStoreException {
        Employee employeeLocalized = employeeManager.read("SathishLocal").get();
        employeeLocalized.setCity("Madurai");
        assertEquals("Updated Simple Localized Object", "Madurai", employeeManager.update(employeeLocalized, "SathishLocal").getCity());
        assertEquals("Updated Simple Localized Object", "மதுரை", employeeManager.read("SathishLocal",Locale.CHINESE).get().getCity());
        
    }

    @Test
    public void testDeleteObject() throws DataStoreException {
        employeeManager.delete("Sathish");
        assertFalse("Deleting Simple Object", employeeManager.read("Sathish").isPresent());
    }

    @Test
    public void testDeleteAllObject() throws DataStoreException {
        employeeManager.delete();
        assertFalse("Deleting Simple Objects", employeeManager.read("Sathish").isPresent());
    }

    
    @Test
    public void testListObject() throws DataStoreException {
        assertEquals("Listing Simple Object", 2, employeeManager.list().size());
    }
     */
}
