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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
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
    
    public static JSONSchema jsonSchema(Map<String,Object> jsonSchema) {
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
    public boolean validate(Map<String, Object> jsonData) {
        return validate(asString(jsonData));

    }

    public boolean validate(String jsonData) {
        try {
            ScriptEngine scriptEngine = scriptEngine();
            Object TV4 = scriptEngine.eval("tv4");
            Invocable inv = (Invocable) scriptEngine;
            Object schemaToJavaScript
                    = inv.invokeMethod(JSON, "parse", jsonSchemaAsTxt);
            Object dataToJavaScript
                    = inv.invokeMethod(JSON, "parse", jsonData);
            Object validation = inv.invokeMethod(TV4, "validateResult", dataToJavaScript, schemaToJavaScript);

            Map<String, Object> validationResult = asMap(inv.invokeMethod(JSON, "stringify", validation).toString());

            return Boolean.valueOf(validationResult.get("valid").toString());
        } catch (ScriptException | IOException | URISyntaxException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
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
