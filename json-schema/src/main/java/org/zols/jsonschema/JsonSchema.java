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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class represents the JSON Schema (Ref: http://json-schema.org/) and
 * provides utilities like localization and traversing
 *
 *
 */
public abstract class JsonSchema {

    protected final static String LOCALE_SEPARATOR = "_";

    protected final Map<String, Object> schemaMap;

    protected final Function<String, Map<String, Object>> schemaSupplier;

    private final JsonSchema parent;
    private final List<JsonSchema> parents;

    private final Map<String, Map<String, Object>> properties;

    public JsonSchema(String schemaId, Function<String, Map<String, Object>> jsonSchemaSupplier) {
        this.schemaMap = jsonSchemaSupplier.apply(schemaId);
        this.schemaSupplier = jsonSchemaSupplier;

        //Calculate and store Parent related attributes
        parents = new ArrayList<>();
        properties = new HashMap<>();
        String reference;

        if (schemaMap.get("properties") != null) {
            properties.putAll((Map<String, Map<String, Object>>) schemaMap.get("properties"));
        }

        if ((reference = (String) schemaMap.get("$ref")) != null) {
            parent = getJsonSchema(reference);

            if (parent != null) {
                // Add Parent
                parents.add(parent);
                // Add Super Parents
                parents.addAll(parent.getParents());

                parents.forEach(p -> properties.putAll(p.getProperties()));

            }
        } else {
            parent = null;
        }

    }

    /**
     * Gets the list of parents from bottom to top
     *
     * @return parents
     */
    public List<JsonSchema> getParents() {
        return parents;
    }

    /**
     * Gets Parent of the Schema if exists
     *
     * @return
     */
    public JsonSchema getParent() {
        return parent;
    }

    /**
     * Get Consolidated Properties of Schema and it's parents. Returns empty map
     * if none exists
     *
     * @return
     */
    public Map<String, Map<String, Object>> getProperties() {

        return properties;
    }

    /**
     * Getting localized property names. Returns empty list if none exists
     *
     * @return
     */
    public List<String> getLocalizedProperties() {
        List<String> localizedKeys = (List<String>) schemaMap.get("localized");
        if (localizedKeys == null) {
            localizedKeys = new ArrayList<>();

            for (JsonSchema p : parents) {
                localizedKeys.addAll(p.getLocalizedProperties());
            }

        }
        return localizedKeys;
    }

    /**
     * Delocalize the given json data
     *
     * @param jsonData
     * @param locale
     * @return
     */
    public Map<String, Object> delocalizeData(Map<String, Object> jsonData, Locale locale) {

        List<String> localizedKeys = getLocalizedProperties();

        if (!localizedKeys.isEmpty()) {

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

                    JsonSchema propertyJsconSchema = getSchemaOf(property.getKey());
                    propertyJsconSchema.delocalizeData((Map<String, Object>) propertyValue, locale);
                } else if ((propertyValue = property.getValue()) instanceof List) {

                    JsonSchema propertyJsconSchema = getSchemaOf(property.getKey());
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

    /**
     * Localize the given data. all localized fields property will be appended
     * with locale separator. if property name is title it will be renamed as
     * title_zh for chineese locale
     *
     * @param jsonData
     * @param locale
     * @return
     */
    public Map<String, Object> localizeData(Map<String, Object> jsonData, Locale locale) {

        List<String> localizedKeys = getLocalizedProperties();

        if (!localizedKeys.isEmpty()) {

            Map<String, Object> localizedValues = new HashMap<>();
            for (Iterator<Map.Entry<String, Object>> it = jsonData.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Object> property = it.next();
                Object propertyValue;
                if ((propertyValue = property.getValue()) instanceof Map) {
                    JsonSchema propertyJsconSchema = getSchemaOf(property.getKey());
                    propertyJsconSchema.localizeData((Map<String, Object>) propertyValue, locale);
                } else if ((propertyValue = property.getValue()) instanceof List) {
                    JsonSchema propertyJsconSchema = getSchemaOf(property.getKey());
                    List<Object> list = (List) propertyValue;
                    list.parallelStream().forEach(value -> {
                        if (value instanceof Map) {
                            propertyJsconSchema.localizeData((Map<String, Object>) value, locale);
                        }
                    });
                } else if (property.getKey().contains(LOCALE_SEPARATOR)) {

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
            return this.getClass().getDeclaredConstructor(String.class, Function.class).newInstance(schemaId, this.schemaSupplier);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(JsonSchema.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * get property for a given name, returns null if not exists
     *
     * @param propertyName
     * @return
     */
    public Map<String, Object> getPropertyOf(String propertyName) {
        return properties.get(propertyName);
    }

    public JsonSchema getSchemaOf(String propertyName) {

        String schemaId = null;
        Map<String, Object> map = getPropertyOf(propertyName);
        if (map != null) {
            schemaId = (String) map.get("$ref");
            // Might be array item. So check inside items property
            if (schemaId == null && (map = (Map<String, Object>) map.get("items")) != null) {
                schemaId = (String) map.get("$ref");
            }
        }

        return getJsonSchema(schemaId);
    }

    @Override
    public String toString() {
        return schemaMap.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    public abstract String getId();

    public abstract String getTitle();

    public abstract String getDescription();

    public abstract void validate(Map<String, Object> jsonData);
}
