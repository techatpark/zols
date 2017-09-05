/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author sathish
 */
public class JsonSchema {

    private final Schema schema;

    private final Map<String, Object> schemaMap;

    private final Function<String, String> jsonSchemaSupplier;

    private final static String LOCALE_SEPARATOR = "_";

    public JsonSchema(InputStream inputStream, Function<String, String> jsonSchemaSupplier) {

        JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
        this.jsonSchemaSupplier = jsonSchemaSupplier;
        schemaMap = rawSchema.toMap();
        schema = SchemaLoader.load(rawSchema, (String schameRef) -> new ByteArrayInputStream(jsonSchemaSupplier.apply(schameRef).getBytes(StandardCharsets.UTF_8)));
    }

    public JsonSchema(String schemaId, Function<String, String> jsonSchemaSupplier) {

        JSONObject rawSchema = new JSONObject(new JSONTokener(jsonSchemaSupplier.apply(schemaId)));
        this.jsonSchemaSupplier = jsonSchemaSupplier;
        schemaMap = rawSchema.toMap();
        schema = SchemaLoader.load(rawSchema, (String schameRef) -> new ByteArrayInputStream(jsonSchemaSupplier.apply(schameRef).getBytes(StandardCharsets.UTF_8)));
    }

    public void validate(Map<String, Object> jsonData) {
        schema.validate(new JSONObject(jsonData));
    }

    public Map<String, Object> delocalizeData(Map<String, Object> jsonData, Locale locale) {

        List<String> localizedKeys = (List<String>) schemaMap.get("localized");

        if (localizedKeys != null && !localizedKeys.isEmpty()) {

            Map<String, Object> localizedValues = new HashMap<>();
            for (Iterator<Entry<String, Object>> it = jsonData.entrySet().iterator(); it.hasNext();) {
                Entry<String, Object> property = it.next();
                Object propertyValue;
                if (property.getKey().contains(LOCALE_SEPARATOR)) {
                    if (property.getKey().endsWith(LOCALE_SEPARATOR + locale.getLanguage())) {
                        localizedValues.put(property.getKey().substring(0, property.getKey().lastIndexOf(LOCALE_SEPARATOR)), property.getValue());
                    }
                    it.remove();
                } else if ((propertyValue = property.getValue()) instanceof Map) {
                    String propertySchemaId = getSchemaOfProperty(property.getKey());
                    JsonSchema propertyJsconSchema = new JsonSchema(propertySchemaId, jsonSchemaSupplier);
                    propertyJsconSchema.delocalizeData((Map<String, Object>) propertyValue, locale);
                } else if ((propertyValue = property.getValue()) instanceof List) {
                    String propertySchemaId = getSchemaOfProperty(property.getKey());
                    JsonSchema propertyJsconSchema = new JsonSchema(propertySchemaId, jsonSchemaSupplier);
                    List<Object> list = (List) propertyValue;
                    list.parallelStream().forEach(value -> {
                        if (value instanceof Map) {
                            propertyJsconSchema.delocalizeData((Map<String, Object>) value, locale);
                        }
                    });
                }
            }

            jsonData.putAll(localizedValues);
        }
        return jsonData;
    }

    public Map<String, Object> localizeData(Map<String, Object> jsonData, Locale locale) {

        List<String> localizedKeys = (List<String>) schemaMap.get("localized");

        if (localizedKeys != null && !localizedKeys.isEmpty()) {

            Map<String, Object> localizedValues = new HashMap<>();
            for (Iterator<Entry<String, Object>> it = jsonData.entrySet().iterator(); it.hasNext();) {
                Entry<String, Object> property = it.next();
                Object propertyValue;
                 if ((propertyValue = property.getValue()) instanceof Map) {
                    String propertySchemaId = getSchemaOfProperty(property.getKey());
                    JsonSchema propertyJsconSchema = new JsonSchema(propertySchemaId, jsonSchemaSupplier);
                    propertyJsconSchema.localizeData((Map<String, Object>) propertyValue, locale);
                } else if ((propertyValue = property.getValue()) instanceof List) {
                    String propertySchemaId = getSchemaOfProperty(property.getKey());
                    JsonSchema propertyJsconSchema = new JsonSchema(propertySchemaId, jsonSchemaSupplier);
                    List<Object> list = (List) propertyValue;
                    list.parallelStream().forEach(value -> {
                        if (value instanceof Map) {
                            propertyJsconSchema.localizeData((Map<String, Object>) value, locale);
                        }
                    });
                }else if (property.getKey().contains(LOCALE_SEPARATOR)) {
                    
                    it.remove();
                }
                
                if (localizedKeys.contains(property.getKey())) {
                    localizedValues.put(property.getKey() + LOCALE_SEPARATOR + locale.getLanguage(), property.getValue());
                    it.remove();
                } 
            }

            jsonData.putAll(localizedValues);
        }
        return jsonData;
    }

    private String getSchemaOfProperty(String propertyName) {

        String schemaId = null;
        Map<String, Object> map = getTypeOfProperty(propertyName);
        if (map != null) {
            schemaId = (String) map.get("$ref");
            // Might be array item. So check inside items property
            if (schemaId == null && (map = (Map<String, Object>) map.get("items")) != null) {
                schemaId = (String) map.get("$ref");
            }
        }

        return schemaId;
    }

    private Map<String, Object> getTypeOfProperty(String propertyName) {

        Map<String, Object> m = (Map<String, Object>) schemaMap.get("properties");
        if (m != null) {
            return (Map<String, Object>) m.get(propertyName);
        }

        return null;
    }

}
