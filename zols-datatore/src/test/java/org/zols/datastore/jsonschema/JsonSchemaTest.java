/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.jsonschema;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import javax.script.ScriptException;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

import static org.zols.datastore.jsonschema.JSONSchema.jsonSchema;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchemaForSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchemaText;
import org.zols.datastore.model.Employee;

public class JsonSchemaTest {

    @Test
    public void testJSONSchemaFromClassWithValidData() {
        JSONSchema jSONSchema = jsonSchema(Employee.class);
        assertNull("JSON Schema Generation from Class with Valid Data",
                jSONSchema.validate(sampleJson("employee")));
    }

    @Test
    public void testJSONSchemaFromClassWithInvalidData() {
        JSONSchema jSONSchema = jsonSchema(Employee.class);
        assertNotNull("JSON Schema Generation from Class with Valid Data",
                jSONSchema.validate(sampleJson("employee_invalid")));
    }

    @Test
    public void testSimpleSchemaValidation() {
        Map<String, Object> jsonSchema = sampleJsonSchema("vechicle");

        assertNull("JSON Schema validation",
                jsonSchemaForSchema().validate(jsonSchema));
    }

    @Test
    public void testSimpleInvalidSchemaValidation() {
        assertNotNull("Invalid JSON Schema validation",
                jsonSchemaForSchema().validate(sampleJsonSchema("vechicle_invalid")));
    }

    @Test
    public void testDataValidation() {
        assertNull("JSON Schema Data validation",
                jsonSchema(sampleJsonSchemaText("vechicle")).validate(sampleJson("vechicle")));
    }

    @Test
    public void testSimpleInvalidDataValidation() throws ScriptException, IOException, URISyntaxException, NoSuchMethodException {
        assertNotNull("JSON Schema Invalid Data validation",
                jsonSchema(sampleJsonSchemaText("vechicle")).validate(sampleJson("car_invalid")));

    }
    
    @Test
    public void testJSONSchemaBaseType() {
        JSONSchema jSONSchema = jsonSchema(sampleJsonSchemaText("raw/teacher"));
        Assert.assertEquals("JSON Schema Base Type","person",
                jSONSchema.baseType());
    }
    
    @Test
    public void testJSONSchemaHierarchy() {
        JSONSchema jSONSchema = jsonSchema(sampleJsonSchemaText("raw/headmaster"));
        Assert.assertEquals("JSON Schema Hierarchy",3,
                jSONSchema.hierarchy().size());
    }

    @Test
    public void testCompositeSchemaWithMultiLevelInheritance() {
//       assertNotNull("Composite JSON Schema Invalid Data validation",
//                jsonSchema(sampleJsonSchemaText("sportscar_composite")).validate(sampleJson("sportscar_invalid")));
    }

}
