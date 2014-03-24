/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import org.zols.datastore.config.DataStoreConfiguration;
import java.beans.IntrospectionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test cases that test a Static Entity.
 * Dynamic Entities are those for which there is a POJO available at compilation time.(For E.g Entity.java or Link.java)
 */



/**
 *
 * @author Sathish Kumar Thiyagarajan
 */
@ContextConfiguration(classes = {DataStoreConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class DynamicEntityTest {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DynamicEntityTest.class);
    
    @Test
    public void testBasicCreateBean() throws IntrospectionException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        
    }
}
