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
        assertTrue("Simple JSON Schema validation",
                jsonSchema(sampleJsonSchema("vechicle")).validate(sampleJsonData("vechicle")));
    }

    @Test
    public void testSchemaWithDefenitionValidation() {
        assertTrue("JSON Schema with Defenition validation",
                jsonSchema(sampleJsonSchema("car_with_defenitions")).validate(sampleJsonData("car")));
    }

    @Test
    public void testInvalidSchemaWithDefenitionValidation() {

        assertFalse("Invalid JSON Schema with Defenition validation",
                jsonSchema(sampleJsonSchema("car_with_defenitions")).validate(sampleJsonData("car_invalid")));
    }

}
