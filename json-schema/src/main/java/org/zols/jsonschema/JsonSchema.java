/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class represents the JSON Schema (Ref: http://json-schema.org/) 
 * and provides utilities like localization and traversing
 *
 * 
 */
public abstract class JsonSchema {

    protected final static String LOCALE_SEPARATOR = "_";

    protected final Map<String, Object> schemaMap;

    protected final Function<String, Map<String, Object>> schemaSupplier;

    public JsonSchema(String schemaId, Function<String, Map<String, Object>> jsonSchemaSupplier) {
        this.schemaMap = jsonSchemaSupplier.apply(schemaId);
        this.schemaSupplier = jsonSchemaSupplier;
    }
    
    /**
     * Gets the list of parents from bottom to top
     * @return parents
     */
    public List<JsonSchema> getParents() {
        
        List<JsonSchema> parents = new ArrayList<>();
        Optional<JsonSchema> parent = getParent();
        if(parent.isPresent()) {
            parents.add(parent.get());
            parents.addAll(parent.get().getParents());
        }
        return parents;
    }
    
    /**
     * Gets Parent of the Schema if exists
     * @return 
     */
    public Optional<JsonSchema> getParent() {
        String reference ;
        if((reference = (String) schemaMap.get("$ref") )!= null) {
            return Optional.of(getJsonSchema(reference));
        }
        return Optional.empty();
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
                    JsonSchema propertyJsconSchema = getJsonSchema(propertySchemaId);
                    propertyJsconSchema.delocalizeData((Map<String, Object>) propertyValue, locale);
                } else if ((propertyValue = property.getValue()) instanceof List) {
                    String propertySchemaId = getSchemaOfProperty(property.getKey());
                    JsonSchema propertyJsconSchema = getJsonSchema(propertySchemaId);
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
                    JsonSchema propertyJsconSchema = getJsonSchema(propertySchemaId);
                    propertyJsconSchema.localizeData((Map<String, Object>) propertyValue, locale);
                } else if ((propertyValue = property.getValue()) instanceof List) {
                    String propertySchemaId = getSchemaOfProperty(property.getKey());
                    JsonSchema propertyJsconSchema = getJsonSchema(propertySchemaId);
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
    
    private JsonSchema getJsonSchema(String schemaId) {
        try {
            return this.getClass().getDeclaredConstructor(String.class,Function.class).newInstance(schemaId,this.schemaSupplier);
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

    @Override
    public String toString() {
        return schemaMap.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public abstract String getId() ;
    public abstract String getTitle() ;
    public abstract String getDescription() ;

    
    public abstract void validate(Map<String, Object> jsonData) ;
}
