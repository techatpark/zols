/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;

/**
 * This Class represents the JSON Schema (Ref: http://json-schema.org/) and
 * provides utilities like localization and traversing
 *
 *
 */
public abstract class JsonSchema {

    protected final static String LOCALE_SEPARATOR = "^";

    protected final Map<String, Object> schemaMap;

    protected final Function<String, Map<String, Object>> schemaSupplier;

    private final JsonSchema parent;
    private final List<JsonSchema> parents;
    private final List<String> idPropertyNames;

    private final Map<String, Map<String, Object>> properties;

    public JsonSchema(String schemaId, Function<String, Map<String, Object>> schemaSupplier) {
        this(schemaSupplier.apply(schemaId), schemaSupplier);
    }

    public JsonSchema(Map<String, Object> schemaMap, Function<String, Map<String, Object>> schemaSupplier) {
        this.schemaMap = schemaMap;
        this.schemaSupplier = schemaSupplier;

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

        idPropertyNames = parent == null ? (List<String>) schemaMap.get("ids") : parent.getIdPropertyNames();

        if (idPropertyNames != null) {
            Collections.sort(idPropertyNames);
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
     * Gets Root of the Schema if exists, else returns itself
     *
     * @return
     */
    public JsonSchema getRoot() {
        return parents.isEmpty() ? this : parents.get(parents.size() - 1);
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
     * get the id properties from the Json Schema in asending order
     *
     * @return
     */
    public List<String> getIdPropertyNames() {
        return idPropertyNames;
    }

    /**
     * Get the id values from the json data
     *
     * @param jsonData
     * @return
     */
    public Object[] getIdValues(final Map<String, Object> jsonData) {
        Object[] idValues = null;
        if (idPropertyNames != null) {
            int idsCount = idPropertyNames.size();
            idValues = new Object[idsCount];
            for (int i = 0; i < idsCount; i++) {
                idValues[i] = jsonData.get(idPropertyNames.get(i));
            }
            return idValues;
        }
        return idValues;
    }

    /**
     * Getting localized property names. Returns empty list if none exists
     *
     * @return
     */
    public List<String> getLocalizedPropertyNames() {
        List<String> localizedKeys = (List<String>) schemaMap.get("localized");
        if (localizedKeys == null) {
            localizedKeys = new ArrayList<>();
            for (JsonSchema p : parents) {
                localizedKeys.addAll(p.getLocalizedPropertyNames());
            }
        }
        return localizedKeys;
    }

    public Map<String, Object> getCompositeSchema() {
        // This is going to contain one more item (definitions).
        Map<String, Object> compositeSchema = new HashMap<>(schemaMap);

        Map<String, Map<String, Object>> definitions = getDefinitions(compositeSchema);
        if (!definitions.isEmpty()) {
            compositeSchema.put("definitions", definitions);
        }

        return compositeSchema;
    }

    /**
     * gets valid json property name to put inside definitions of json schema
     *
     * @param referenceUrl
     * @return
     */
    public String getJSONPropertyName(String referenceUrl) {
        return referenceUrl.replaceAll("http", "").replaceAll(":", "").replaceAll("/", "").replaceAll("\\.", "").replaceAll("#", "");
    }

    private Map<String, Map<String, Object>> getDefinitions(Map<String, Object> schemaAsMap) {
        Map<String, Map<String, Object>> definitions = new HashMap();
        schemaAsMap.entrySet().forEach((schemaEntry) -> {
            if (schemaEntry.getKey().equals("$ref")) {
                String referencePath = schemaEntry.getValue().toString();
                // If not JSON Pointer
                if (!referencePath.startsWith("#/")) {
                    Map<String, Object> parentScemaMap = schemaSupplier.apply(referencePath);
                    String jsonPathName = getJSONPropertyName(referencePath);
                    definitions.put(jsonPathName, parentScemaMap);
                    definitions.putAll(getDefinitions(parentScemaMap));
                    schemaEntry.setValue("#/definitions/" + jsonPathName);
                }

            } else if (schemaEntry.getKey().equals("properties")) {
                Map<String, Map<String, Object>> props = (Map<String, Map<String, Object>>) schemaEntry.getValue();

                props.entrySet().forEach(propertyEntry -> {
                    String referencePath = (String) propertyEntry.getValue().get("$ref");
                    if (referencePath == null) {
                        Map<String, Object> itemsMap = (Map<String, Object>) propertyEntry.getValue().get("items");
                        if (itemsMap != null) {
                            referencePath = (String) itemsMap.get("$ref");
                            if (referencePath != null) {
                                Map<String, Object> propScemaMap = schemaSupplier.apply(referencePath);
                                String jsonPathName = getJSONPropertyName(referencePath);
                                definitions.put(jsonPathName, propScemaMap);
                                definitions.putAll(getDefinitions(propScemaMap));
                                itemsMap.put("$ref", "#/definitions/" + jsonPathName);
                            }
                        }

                    } else if (!referencePath.startsWith("#/")) {
                        Map<String, Object> propScemaMap = schemaSupplier.apply(referencePath);
                        String jsonPathName = getJSONPropertyName(referencePath);
                        definitions.put(jsonPathName, propScemaMap);
                        definitions.putAll(getDefinitions(propScemaMap));
                        propertyEntry.getValue().put("$ref", "#/definitions/" + jsonPathName);
                    }
                });

            }
        });
        return definitions;
    }

    /**
     * Delocalize the given json data
     *
     * @param jsonData
     * @param locale
     * @return
     */
    public Map<String, Object> delocalizeData(final Map<String, Object> jsonData, final Locale locale) {

        if (jsonData != null) {
            Map<String, Object> delocalizeJsonData;
            if (locale != null) {
                delocalizeJsonData = new HashMap<>(jsonData.size());
                List<String> localizedKeys = getLocalizedPropertyNames();

                jsonData.entrySet().forEach((entry) -> {

                    final String key = entry.getKey();
                    final Object value = entry.getValue();

                    if (localizedKeys.contains(key)) {
                        String localizedKey = key + LOCALE_SEPARATOR + locale.getLanguage();
                        Object localizedValue = jsonData.get(localizedKey);
                        if (localizedValue == null) {
                            delocalizeJsonData.put(key, value);
                        } else {
                            delocalizeJsonData.put(key, localizedValue);
                        }
                    } else {
                        // if localized ignore
                        if (!key.contains(LOCALE_SEPARATOR)) {
                            delocalizeJsonData.put(key, value);
                        } else {
                            delocalizeJsonData.put(key.substring(0, key.lastIndexOf(LOCALE_SEPARATOR)), value);
                        }

                    }

                    if (value instanceof Map) {
                        Map<String, Object> nestedObjectMap = (Map<String, Object>) value;
                        delocalizeJsonData.put(key, getSchemaOf(key).delocalizeData(nestedObjectMap, locale));

                    } else if (value instanceof List) {
                        List nestedList = (List) value;
                        List newList = new ArrayList(nestedList.size());
                        for (Object object : nestedList) {
                            if (object instanceof Map) {
                                Map<String, Object> nestedObjectMap = (Map<String, Object>) object;
                                newList.add(getSchemaOf(key).delocalizeData(nestedObjectMap, locale));

                            } else {
                                newList.add(object);
                            }
                        }
                        delocalizeJsonData.put(key, newList);

                    }

                });
                return delocalizeJsonData;
            } else {
                return defaultLocaleData(jsonData);
            }
        }

        return null;
    }

    /**
     * Delocalize the given json data
     *
     * @param jsonData
     * @param locale
     * @return
     */
    public Map<String, Object> defaultLocaleData(final Map<String, Object> jsonData) {

        if (jsonData != null) {
            Map<String, Object> delocalizeJsonData;

            delocalizeJsonData = new HashMap<>(jsonData.size());

            jsonData.entrySet().forEach((entry) -> {

                final String key = entry.getKey();
                final Object value = entry.getValue();

                if (!key.contains(LOCALE_SEPARATOR)) {
                    delocalizeJsonData.put(key, value);
                }

                if (value instanceof Map) {
                    Map<String, Object> nestedObjectMap = (Map<String, Object>) value;
                    delocalizeJsonData.put(key, getSchemaOf(key).defaultLocaleData(nestedObjectMap));

                } else if (value instanceof List) {
                    List nestedList = (List) value;
                    List newList = new ArrayList(nestedList.size());
                    for (Object object : nestedList) {
                        if (object instanceof Map) {
                            Map<String, Object> nestedObjectMap = (Map<String, Object>) object;
                            newList.add(getSchemaOf(key).defaultLocaleData(nestedObjectMap));

                        } else {
                            newList.add(object);
                        }
                    }
                    delocalizeJsonData.put(key, newList);

                }

            });
            return delocalizeJsonData;

        }

        return null;
    }

    public Map<String, Object> localizeData(Map<String, Object> jsonData, Locale locale) {
        return localizeData(jsonData, locale, Boolean.FALSE);
    }

    /**
     * Localize the given data and copy to new Map. all localized fields
     * property will be appended with locale separator. if property name is
     * title it will be renamed as title_zh for chineese locale
     *
     * @param jsonData
     * @param locale
     * @param keepDefault
     * @return
     */
    public Map<String, Object> localizeData(Map<String, Object> jsonData, Locale locale, Boolean keepDefault) {

        Map<String, Object> localizedJsonData = new HashMap<>(jsonData.size());
        List<String> localizedKeys = getLocalizedPropertyNames();

        jsonData.entrySet().forEach((entry) -> {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (localizedKeys.contains(key)) {
                localizedJsonData.put(key + LOCALE_SEPARATOR + locale.getLanguage(), value);
                if (keepDefault) {
                    localizedJsonData.put(key, value);
                }
            } else {
                localizedJsonData.put(key, value);
            }

            if (value instanceof Map) {
                Map<String, Object> nestedObjectMap = (Map<String, Object>) value;
                localizedJsonData.put(key, getSchemaOf(key).localizeData(nestedObjectMap, locale, keepDefault));
            } else if (value instanceof List) {
                List nestedList = (List) value;
                List newList = new ArrayList(nestedList.size());
                for (Object object : nestedList) {
                    if (object instanceof Map) {
                        Map<String, Object> nestedObjectMap = (Map<String, Object>) object;
                        newList.add(getSchemaOf(key).localizeData(nestedObjectMap, locale, keepDefault));

                    } else {
                        newList.add(object);
                    }
                }
                localizedJsonData.put(key, newList);

            }

        });

        return localizedJsonData;
    }

    /**
     * get the value from Json Schema supplier
     *
     * @param schemaId
     * @return
     */
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
        return asString();
    }

    /**
     * Gets id of Json Schema
     *
     * @return
     */
    public String getId() {
        return (String) schemaMap.get("$id");
    }

    /**
     * Gets title of Json Schema
     *
     * @return
     */
    public String getTitle() {
        return (String) schemaMap.get("title");
    }

    /**
     * Gets description of Json Schema
     *
     * @return
     */
    public String getDescription() {
        return (String) schemaMap.get("description");
    }

    /**
     * Gets Schema as a JSON String
     *
     * @return
     */
    protected abstract String asString();

    public abstract Set<ConstraintViolation> validate(Map<String, Object> jsonData);

}
