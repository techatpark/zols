/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.jsonschema;

import java.io.IOException;
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
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.validation.ConstraintViolation;
import static org.zols.datastore.util.JsonUtil.asMap;
import static org.zols.datastore.util.JsonUtil.asString;

/**
 *
 * @author wz07
 */
public class JSONSchema {

    private static final String ID_FIELD = "idField";
    private static JSONSchema _JSONSCHEMA_FOR_SCHEMA = null;

    public static JSONSchema jsonSchema(String jsonSchema) {
        return new JSONSchema(jsonSchema);
    }

    public static JSONSchema jsonSchema(Map<String, Object> jsonSchema) {
        return new JSONSchema(asString(jsonSchema));
    }

    public static JSONSchema jsonSchemaForSchema() {
        if (_JSONSCHEMA_FOR_SCHEMA == null) {
            try {
                _JSONSCHEMA_FOR_SCHEMA = jsonSchema(new String(Files.readAllBytes(Paths
                        .get(ClassLoader.getSystemResource("org/zols/datastore/jsonschema/schema.json").toURI()))));
            } catch (IOException | URISyntaxException ex) {
                Logger.getLogger(JSONSchema.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            _scriptEngine.eval(new String(Files.readAllBytes(Paths
                    .get(ClassLoader.getSystemResource("org/zols/datastore/jsonschema/tv4.js").toURI()))));
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
     * @param jsonData
     * @return null if valid
     */
    public Set<ConstraintViolation> validate(String jsonData) {
        Set<ConstraintViolation> constraintViolations = null;
        try {
            ScriptEngine scriptEngine = scriptEngine();
            Object TV4 = scriptEngine.eval("tv4");
            Invocable inv = (Invocable) scriptEngine;
            Object schemaToJavaScript
                    = inv.invokeMethod(JSON, "parse", jsonSchemaAsTxt);
            Object dataToJavaScript
                    = inv.invokeMethod(JSON, "parse", jsonData);
            Object validation = inv.invokeMethod(TV4, "validateMultiple", dataToJavaScript, schemaToJavaScript);

            Map<String, Object> validationResult = asMap(inv.invokeMethod(JSON, "stringify", validation).toString());

            if (!(boolean) validationResult.get("valid")) {
                Collection errors = (Collection) validationResult.get("errors");
                constraintViolations = new HashSet<>(errors.size());
                for (Object error : errors) {
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
     * gets Idfield from given JSON schema
     *
     * @param jsonSchema
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
        Object id = null;
        Map<String, Object> jsonSchemaAsMap = asMap(jsonSchemaAsTxt);
        if (jsonSchemaAsMap != null && (id = jsonSchemaAsMap.get("id")) != null) {
            return id.toString();
        }
        return null;
    }

}
