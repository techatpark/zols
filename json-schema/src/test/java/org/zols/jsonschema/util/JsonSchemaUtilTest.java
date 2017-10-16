/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema.util;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.zols.jsonschema.util.TestUtil.getTestSchema;

/**
 *
 * @author sathish
 */
public class JsonSchemaUtilTest {
    

    /**
     * Test of validateSchema method, of class JsonSchemaUtil.
     */
    @Test
    public void testValidateSchema() {

        assertTrue("Checking valid schema", JsonSchemaUtil.validateSchema(getTestSchema("product")).isEmpty());
        assertTrue("Checking invalid schema", !JsonSchemaUtil.validateSchema(getTestSchema("product_invalid")).isEmpty());
    }

    
    
}
