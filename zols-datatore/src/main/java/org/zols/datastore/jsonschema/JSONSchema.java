/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.jsonschema;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Id;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.validation.ConstraintViolation;
import static org.zols.datastore.util.JsonUtil.asList;
import static org.zols.datastore.util.JsonUtil.asMap;
import static org.zols.datastore.util.JsonUtil.asString;

/**
 *
 * @author wz07
 */
public class JSONSchema {

    private static final String ID_FIELD = "idField";
    private static JSONSchema _JSONSCHEMA_FOR_SCHEMA = null;

    public static JSONSchema jsonSchema(Class clazz) {
        ObjectMapper m = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        try {
            m.acceptJsonFormatVisitor(m.constructType(clazz), visitor);
        } catch (JsonMappingException ex) {
            Logger.getLogger(JSONSchema.class.getName()).log(Level.SEVERE, null, ex);
        }

        Map<String, Object> jsonSchemaAsMap = asMap(visitor.finalSchema());

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                jsonSchemaAsMap.put(ID_FIELD, field.getName());
            }
        }
        return new JSONSchema(asString(jsonSchemaAsMap));
    }

    public static JSONSchema jsonSchema(String jsonSchema) {
        return new JSONSchema(jsonSchema);
    }

    public static JSONSchema jsonSchema(Map<String, Object> jsonSchema) {
        return new JSONSchema(asString(jsonSchema));
    }

    private static String getContentFromClasspath(String resourcePath) {
        InputStream inputStream = JSONSchema.class.getResourceAsStream(resourcePath);
        java.util.Scanner scanner = new java.util.Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        String theString = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return theString;
    }

    public static JSONSchema jsonSchemaForSchema() {
        if (_JSONSCHEMA_FOR_SCHEMA == null) {
            _JSONSCHEMA_FOR_SCHEMA = jsonSchema(getContentFromClasspath("/org/zols/datastore/jsonschema/schema.json"));
        }

        return _JSONSCHEMA_FOR_SCHEMA;
    }

    private final String jsonSchemaAsTxt;

    private JSONSchema(String jsonSchema) {
        this.jsonSchemaAsTxt = jsonSchema;
    }

    private static ScriptEngine _scriptEngine;
    private static Object JSON;

    private ScriptEngine scriptEngine() throws ScriptException, IOException, URISyntaxException {
        if (_scriptEngine == null) {
            _scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
            _scriptEngine.eval(getContentFromClasspath("/org/zols/datastore/jsonschema/jsen.js"));
            _scriptEngine.eval(getContentFromClasspath("/org/zols/datastore/jsonschema/validator.js"));
            JSON = _scriptEngine.eval("JSON");
        }
        return _scriptEngine;
    }

    // Validate the Data against Schema
    public Set<ConstraintViolation> validate(Map<String, Object> jsonData) {
        return validate(asString(jsonData));
    }

    /**
     * validates using JSON Schema
     *
     * @param jsonData
     * @return null if valid
     */
    public Set<ConstraintViolation> validate(String jsonData) {
        Set<ConstraintViolation> constraintViolations = null;
        try {
            ScriptEngine scriptEngine = scriptEngine();
            Invocable inv = (Invocable) scriptEngine;
            Object schema
                    = inv.invokeMethod(JSON, "parse", jsonSchemaAsTxt);
            Object data
                    = inv.invokeMethod(JSON, "parse", jsonData);

            Object errors = inv.invokeFunction("validateJsonSchema", schema, data);

            if (errors != null) {
                Collection errorsAsList = asList(inv.invokeMethod(JSON, "stringify", errors).toString());
                constraintViolations = new HashSet<>(errorsAsList.size());
                for (Object error : errorsAsList) {
                    constraintViolations.add(new JsonSchemaConstraintViolation((Map<String, Object>) error));
                }

            }

        } catch (ScriptException | IOException | URISyntaxException | NoSuchMethodException e) {
            constraintViolations = new HashSet<>(1);
            Map<String, Object> error = new HashMap<>(1);
            error.put("message", "unknown error");
            constraintViolations.add(new JsonSchemaConstraintViolation((Map<String, Object>) error));
        }
        return constraintViolations;
    }

    /**
     * gets Id Field from given JSON schema
     *
     * @return
     */
    public String idField() {
        Object idField;
        Map<String, Object> jsonSchemaAsMap = asMap(jsonSchemaAsTxt);
        if (jsonSchemaAsMap != null && (idField = jsonSchemaAsMap.get(ID_FIELD)) != null) {
            return idField.toString();
        }
        return "id";
    }

    public String id() {
        Object id;
        Map<String, Object> jsonSchemaAsMap = asMap(jsonSchemaAsTxt);
        if (jsonSchemaAsMap != null) {
            if ((id = jsonSchemaAsMap.get("base")) != null
                    || (id = jsonSchemaAsMap.get("id")) != null) {
                return id.toString();
            }
        }
        return null;
    }

}
