/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.jsonschema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import static org.zols.datastore.jsonschema.JSONSchema.jsonSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonData;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchema;

/**
 *
 * @author wz07
 */
public class JsonSchemaTest {

    @Test
    public void testSimpleValidation() {
        assertTrue("Successful Simple JSON Schema validation",
                jsonSchema(sampleJsonSchema("simple")).validate(sampleJsonData("simple")));
    }
    
    @Test
    public void testSimpleValidationWithReusableSchema() {
        assertTrue("Successful Reusable JSON Schema validation",
                jsonSchema(sampleJsonSchema("reusable")).validate(sampleJsonData("reusable")));
    }
    
    @Test
    public void testSimpleValidationWithExtendedSchema() {
        assertTrue("Successful Extended JSON Schema validation",
                jsonSchema(sampleJsonSchema("car")).validate(sampleJsonData("car")));
    }
    
    
}
