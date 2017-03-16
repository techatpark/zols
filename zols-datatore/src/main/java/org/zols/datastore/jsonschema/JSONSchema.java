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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
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

    private static final String NAME = "name";
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

        if (clazz.isAnnotationPresent(Entity.class)) {
            Entity entity = (Entity) clazz.getAnnotation(Entity.class);
            jsonSchemaAsMap.put(NAME, entity.name());
        } else {
            jsonSchemaAsMap.put(NAME, jsonSchemaAsMap.get("id"));
        }

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
            Map<String, Object> schema = asMap(getContentFromClasspath("/org/zols/datastore/jsonschema/schema.json"));
            schema.put(NAME, "schema");
            _JSONSCHEMA_FOR_SCHEMA = jsonSchema(schema);
        }

        return _JSONSCHEMA_FOR_SCHEMA;
    }

    private final String jsonSchemaAsTxt;
    private final Map<String, Object> jsonSchema;
    private final String idField;
    private final String baseType;
    private final List<String> hierarchy;

    private JSONSchema(String jsonSchema) {
        this.jsonSchemaAsTxt = jsonSchema;
        this.jsonSchema = asMap(jsonSchema);
        this.idField = getIdField();
        this.hierarchy = getHierarchy();
        this.baseType = this.hierarchy.get(this.hierarchy.size()-1);
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
    public Set<ConstraintViolation<Object>> validate(Map<String, Object> jsonData) {
        return validate(asString(jsonData));
    }

    /**
     * validates using JSON Schema
     *
     * @param jsonData
     * @return null if valid
     */
    public Set<ConstraintViolation<Object>> validate(String jsonData) {
        Set<ConstraintViolation<Object>> constraintViolations = null;
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
    private String getIdField() {
        Object Object;
        Map<String, Object> jsonSchemaAsMap = asMap(jsonSchemaAsTxt);
        if (jsonSchemaAsMap != null && (Object = jsonSchemaAsMap.get(ID_FIELD)) != null) {
            return Object.toString();
        }
        return "name";
    }

    private List<String> getHierarchy() {
        List<String> hierarchyList = new ArrayList();
        Map<String, Object> jsonSchemaAsMap = asMap(jsonSchemaAsTxt);
        hierarchyList.add(jsonSchemaAsMap.get(NAME).toString());

        Object baseTypeObject;
        if ((baseTypeObject = jsonSchemaAsMap.get("$ref")) != null) {
            // This is super type. look is there anything higher
            baseTypeObject = baseTypeObject.toString().replaceAll("#/definitions/", "");
            Map<String, Object> difinitions = (Map<String, Object>) jsonSchemaAsMap.get("definitions");
            if (difinitions != null) {
                hierarchyList.addAll(jsonSchema((Map<String, Object>) difinitions.get(baseTypeObject)).hierarchy());
            }

        }
        return hierarchyList;
    }

    public String baseType() {
        return baseType;
    }

    List<String> hierarchy() {
        return hierarchy;
    }

    public String idField() {
        return idField;
    }
    
    public Map<String, Object> getJsonSchema(String typeName) {
        Map<String, Map<String, Object>> consolidatedDefinitions = getConsolidatedDefinitions();

        return consolidatedDefinitions.get(typeName);
    }
    public Map<String, Map<String, Object>> getConsolidatedDefinitions() {
        return getConsolidatedDefinitions(this.jsonSchema);
    }
    private Map<String, Map<String, Object>> getConsolidatedDefinitions(Map<String, Object> jsonSchema) {
        final Map<String, Map<String, Object>> consolidatedDefinitions =  (Map<String, Map<String, Object>>) jsonSchema.get("definitions");
        if(consolidatedDefinitions!= null) {
            consolidatedDefinitions
                    .values()
                    .stream()
                    .filter(schema->schema.get("definitions")!=null)
                    .forEach(schema->consolidatedDefinitions.putAll(getConsolidatedDefinitions(schema)));
        }
        return consolidatedDefinitions;
    }
    
    public Map<String, Object> getConsolidatedProperties() {
        return getConsolidatedProperties(this.jsonSchema);
    }

    private Map<String, Object> getConsolidatedProperties(Map<String, Object> jsonSchema) {

        Map<String, Object> consolidatedProperties = (Map<String, Object>) jsonSchema.get("properties");

        //If there is a super type e.g Car is Base Type, Vehicle is super type
        Object reference = jsonSchema.get("$ref");
        if (reference != null) {
            Map<String, Object> superJsonSchema = (Map<String, Object>) ((Map<String, Object>) jsonSchema.get("definitions")).get(reference.toString().replaceAll("#/definitions/", ""));
            consolidatedProperties.putAll(getConsolidatedProperties(superJsonSchema));
        }

        return consolidatedProperties;
    }

    public Map<String, Object> getLocalizedProperties() {
        return getLocalizedProperties(this.jsonSchema);
    }
    
    private Map<String, Object> getLocalizedProperties(Map<String, Object> jsonSchema) {
        Map<String, Object> localizedProperties = new HashMap<>();
        List<String> localized = (List<String>) jsonSchema.get("localized");
        Map<String, Object> properties = (Map<String, Object>) jsonSchema.get("properties");
        if (localized != null) {
            localized
                    .parallelStream()
                    .forEach(propertyName -> localizedProperties.put(propertyName, (Map<String, Object>) properties.get(propertyName)));
        }

        //If there is a super type e.g Car is Base Type, Vehicle is super type
        Object reference = jsonSchema.get("$ref");
        if (reference != null) {
            Map<String, Object> superJsonSchema = (Map<String, Object>) ((Map<String, Object>) jsonSchema.get("definitions")).get(reference.toString().replaceAll("#/definitions/", ""));
            localizedProperties.putAll(getLocalizedProperties(superJsonSchema));
        }

        return localizedProperties;
    }

}
