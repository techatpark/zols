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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

import static org.zols.datastore.jsonschema.JSONSchema.jsonSchema;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchemaForSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonText;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchemaText;
import org.zols.datastore.model.Employee;

/**
 *
 * @author wz07
 */
public class JsonSchemaTest {

    @Test
    public void testJSONSchemaFromClassWithValidData() {
        JSONSchema jSONSchema = jsonSchema(Employee.class);
        assertNull("JSON Schema Generation from Class with Valid Data",
                jSONSchema.validate(sampleJsonText("employee")));
    }

    @Test
    public void testJSONSchemaFromClassWithInvalidData() {
        JSONSchema jSONSchema = jsonSchema(Employee.class);
        assertNotNull("JSON Schema Generation from Class with Valid Data",
                jSONSchema.validate(sampleJsonText("employee_invalid")));
    }

    @Test
    public void testSimpleSchemaValidation() {
        Map<String, Object> jsonSchema = sampleJsonSchema("vechicle");
        jsonSchema.remove("id");
        assertNull("JSON Schema validation",
                jsonSchemaForSchema().validate(jsonSchema));
    }

    @Test
    public void testSimpleInvalidSchemaValidation() {
        assertNotNull("Invalid JSON Schema validation",
                jsonSchemaForSchema().validate(sampleJsonSchemaText("vechicle")));
    }

    @Test
    public void testDataValidation() {
        assertNull("JSON Schema Data validation",
                jsonSchema(sampleJsonSchemaText("vechicle")).validate(sampleJsonText("vechicle")));
    }

    @Test
    public void testSimpleInvalidDataValidation() throws ScriptException, IOException, URISyntaxException, NoSuchMethodException {
        assertNotNull("JSON Schema Invalid Data validation",
                jsonSchema(sampleJsonSchemaText("vechicle")).validate(sampleJsonText("car_invalid")));

    }

    @Test
    public void testCompositeSchemaWithMultiLevelInheritance() {
//        assertNotNull("Composite JSON Schema Invalid Data validation",
//                jsonSchema(sampleJsonSchemaText("sportscar_composite")).validate(sampleJsonText("sportscar_invalid")));
    }

}
