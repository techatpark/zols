/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.jsonschema;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import static org.zols.datastore.jsonschema.JSONSchemaProcessor.jsonSchema;
import static org.zols.datastore.jsonschema.JsonSchemaTestUtil.sampleJsonData;
import static org.zols.datastore.jsonschema.JsonSchemaTestUtil.sampleJsonSchema;

/**
 *
 * @author wz07
 */
public class JsonSchemeProcessorTest {

    @Test
    public void testSimpleValidation() {
        assertTrue("Successful Simple JSON Schema validation",
                jsonSchema(sampleJsonSchema("simple")).validate(sampleJsonData("simple")));
    }
    
    @Test
    public void testSimpleValidationWithExtendedSchema() {
        assertTrue("Successful Simple JSON Schema validation",
                jsonSchema(sampleJsonSchema("extended")).validate(sampleJsonData("extended")));
    }
}
