/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.AbstractMap.SimpleEntry;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zols.datastore.elasticsearch.ElasticSearchDataStorePersistence;
import org.zols.datastore.model.Employee;

public class ObjectManagementTest {

    private final DataStore dataStore;
    private final ObjectManager<Employee> employeeManager;

    public ObjectManagementTest() {
        dataStore = new DataStore(new ElasticSearchDataStorePersistence());
        employeeManager = dataStore.getObjectManager(Employee.class);
    }

    @BeforeEach
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
        employeeManager.create(employeeLocalized, Locale.CHINESE);
    }

    @AfterEach
    public void afterTest() throws DataStoreException {
        employeeManager.delete(new SimpleEntry("name", "Sathish"));
    }

    @Test
    public void testCreateObject() throws DataStoreException {
        assertTrue(employeeManager.read(new SimpleEntry("name", "Sathish")).isPresent(), "Creating Simple Object");
    }

    @Test
    public void testUpdateObject() throws DataStoreException {
        Employee employee = employeeManager.read(new SimpleEntry("name", "Sathish")).get();
        employee.setSalary(2000);
        employee = employeeManager.update(employee, new SimpleEntry("name", "Sathish"));
        assertEquals(2000, employee.getSalary(), "Updating Simple Object");
    }

    /*
    @Test
    public void testUpdateLocalizedObject() throws DataStoreException {
        Employee employeeLocalized = employeeManager.read(new SimpleEntry("name", "SathishLocal")).get();
        employeeLocalized.setCity("Madurai");
        assertEquals("Madurai", employeeManager.update(employeeLocalized, new SimpleEntry("name", "SathishLocal")).getCity(), "Updated Simple Localized Object");
        assertEquals("Updated Simple Localized Object", "மதுரை", employeeManager.read(Locale.CHINESE, new SimpleEntry("name", "SathishLocal")).get().getCity());

    }
     */
    @Test
    public void testDeleteObject() throws DataStoreException {
        employeeManager.delete(new SimpleEntry("name", "Sathish"));
        assertFalse(employeeManager.read(new SimpleEntry("name", "Sathish")).isPresent(), "Deleting Simple Object");
    }

    @Test
    public void testDeleteAllObject() throws DataStoreException {
        employeeManager.delete();
        assertFalse(employeeManager.read(new SimpleEntry("name", "Sathish")).isPresent(), "Deleting Simple Objects");
    }

    @Test
    public void testListObject() throws DataStoreException {
        assertEquals(2, employeeManager.list().size(), "Listing Simple Object");
    }

}
