/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
   DataManagementTest.class,
    ObjectManagementTest.class,
    SchemaManagementTest.class
})
public class DataStoreSuiteTest {
    
}
