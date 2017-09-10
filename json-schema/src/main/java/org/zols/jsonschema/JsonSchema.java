/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sathish
 */
public abstract class JsonSchema {

    protected final static String LOCALE_SEPARATOR = "_";

    protected final Map<String, Object> schemaMap;

    protected final Function<String, Map<String, Object>> schemaSupplier;

    public JsonSchema(String schemaId, Function<String, Map<String, Object>> jsonSchemaSupplier) {
        this.schemaMap = jsonSchemaSupplier.apply(schemaId);
        this.schemaSupplier = jsonSchemaSupplier;
    }
    
    public Map<String, Object> delocalizeData(Map<String, Object> jsonData, Locale locale) {

        List<String> localizedKeys = (List<String>) schemaMap.get("localized");

        if (localizedKeys != null && !localizedKeys.isEmpty()) {

            Map<String, Object> localizedValues = new HashMap<>();
            for (Iterator<Map.Entry<String, Object>> it = jsonData.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Object> property = it.next();
                Object propertyValue;
                if (property.getKey().contains(LOCALE_SEPARATOR)) {
                    if (property.getKey().endsWith(LOCALE_SEPARATOR + locale.getLanguage())) {
                        localizedValues.put(property.getKey().substring(0, property.getKey().lastIndexOf(LOCALE_SEPARATOR)), property.getValue());
                    }
                    it.remove();
                } else if ((propertyValue = property.getValue()) instanceof Map) {
                    String propertySchemaId = getSchemaOfProperty(property.getKey());
                    JsonSchema propertyJsconSchema = getJsonSchema(propertySchemaId, schemaSupplier);
                    propertyJsconSchema.delocalizeData((Map<String, Object>) propertyValue, locale);
                } else if ((propertyValue = property.getValue()) instanceof List) {
                    String propertySchemaId = getSchemaOfProperty(property.getKey());
                    JsonSchema propertyJsconSchema = getJsonSchema(propertySchemaId, schemaSupplier);
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
            for (Iterator<Map.Entry<String, Object>> it = jsonData.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Object> property = it.next();
                Object propertyValue;
                 if ((propertyValue = property.getValue()) instanceof Map) {
                    String propertySchemaId = getSchemaOfProperty(property.getKey());
                    JsonSchema propertyJsconSchema = getJsonSchema(propertySchemaId, schemaSupplier);
                    propertyJsconSchema.localizeData((Map<String, Object>) propertyValue, locale);
                } else if ((propertyValue = property.getValue()) instanceof List) {
                    String propertySchemaId = getSchemaOfProperty(property.getKey());
                    JsonSchema propertyJsconSchema = getJsonSchema(propertySchemaId, schemaSupplier);
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
    
    private JsonSchema getJsonSchema(String schemaId, Function<String, Map<String, Object>> jsonSchemaSupplier) {
        try {
            return this.getClass().getDeclaredConstructor(String.class,Function.class).newInstance(schemaId,jsonSchemaSupplier);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(JsonSchema.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public abstract void validate(Map<String, Object> toMap) ;
}
