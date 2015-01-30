/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.validator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Id;
import javax.script.Invocable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * This class is a wrapper for Java Script based JSON Schema validator tv4
 *
 * @author wz07
 */
public class TV4 {

    private ObjectMapper mapper;

    private ScriptEngine engine;
    private Object json;
    private static final String ID_FIELD = "idField";

    public TV4() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");
        try {
            json = engine.eval("JSON");
            loadJavaScriptFile("/tv4.js");
            loadJavaScriptFile("/tv4Wrapper.js");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean validate(Object data, Class clazz) throws ScriptException, NoSuchMethodException, JsonProcessingException, IOException {
        return validate(data, getJsonSchema(clazz));
    }

    public Boolean validate(Object data, String schema) throws ScriptException, NoSuchMethodException, JsonProcessingException {
        System.out.println("Schema \n--------- \n " + schema);
        Invocable inv = (Invocable) engine;
        Object schemaToJavaScript
                = inv.invokeMethod(json, "parse", schema);
        Object dataToJavaScript
                = inv.invokeMethod(json, "parse", mapper.writeValueAsString(data));

        System.out.println("Data \n--------- \n " + mapper.writeValueAsString(data));

        Object validation = inv.invokeFunction("validate", dataToJavaScript, schemaToJavaScript);

        Object result
                = inv.invokeMethod(json, "stringify", validation);
        return Boolean.valueOf(result.toString());
    }

    /**
     * Prepares JSON Schema for a given Java Type
     *
     * @param clazz - Class for which we need JSON Schema
     * @return JSON Schema as JSON text
     * @throws java.io.IOException
     */
    public String getJsonSchema(Class clazz) throws IOException {
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        mapper.acceptJsonFormatVisitor(mapper.constructType(clazz), visitor);
        JsonSchema jsonSchema = visitor.finalSchema();
        return addIdField(clazz, getValueAsMap(jsonSchema));
    }

    public Map<String, Object> getValueAsMap(String object) throws IOException {
        return mapper.readValue(object, Map.class);
    }

    public Map<String, Object> getValueAsMap(Object object) {
        return mapper.convertValue(object, Map.class);
    }

    public String getValueAsString(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public <T> T getObjectOfType(Class<T> type, Map<String, Object> dataMap) {
        return mapper.convertValue(dataMap, type);
    }

    private String addIdField(Class type, Map<String, Object> jsonSchemaAsMap) throws JsonProcessingException {
        jsonSchemaAsMap.put("id", type.getName());
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                jsonSchemaAsMap.put(ID_FIELD, field.getName());
            }
        }
        return mapper.writeValueAsString(jsonSchemaAsMap);
    }

    /**
     * gets Idfield from given JSON schema
     *
     * @param jsonSchema
     * @return
     */
    public String getIdField(String jsonSchema) throws IOException {
        Object idField = null;
        Map<String, Object> jsonSchemaAsMap = getValueAsMap(jsonSchema);
        if (jsonSchemaAsMap != null && (idField = jsonSchemaAsMap.get(ID_FIELD)) != null) {
            return idField.toString();
        }
        return null;
    }

    /**
     * gets Idfield from given JSON schema
     *
     * @param jsonSchema
     * @return
     */
    public String getId(String jsonSchema) {
        try {
            Object id = null;
            Map<String, Object> jsonSchemaAsMap = getValueAsMap(jsonSchema);
            if (jsonSchemaAsMap != null && (id = jsonSchemaAsMap.get("id")) != null) {
                return id.toString();
            }

        } catch (IOException ex) {
            Logger.getLogger(TV4.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * This method will load Javascript from class path
     *
     * @param path - path of Javascript file
     */
    private void loadJavaScriptFile(String path) throws ScriptException {
        InputStream stream = TV4.class.getResourceAsStream(path);
        InputStreamReader reader = new InputStreamReader(stream);
        engine.eval(reader);
        try {
            stream.close();
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(TV4.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
