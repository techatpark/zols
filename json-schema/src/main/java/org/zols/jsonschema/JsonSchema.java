/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import javax.validation.ConstraintViolation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.zols.jsonschema.util.JsonUtil.cloneMap;

/**
 * This Class represents the JSON Schema (Ref: http://json-schema.org/) and
 * provides utilities like localization and traversing.
 */
public abstract class JsonSchema {

    /**
     * The constant LOCALE_SEPARATOR.
     */
    protected final static String LOCALE_SEPARATOR = "^";

    /**
     * The Schema map.
     */
    protected final Map<String, Object> schemaMap;

    /**
     * The Schema supplier.
     */
    protected final Function<String, Map<String, Object>> schemaSupplier;

    /**
     * The jsonschema.
     */
    private final JsonSchema parent;
    /**
     * list of jsonschema.
     */
    private final List<JsonSchema> parents;

    /**
     * Property Names (Sorted).
     */
    private final List<String> idPropertyNames;

    /**
     * properties.
     */
    private final Map<String, Map<String, Object>> properties;

    /**
     * Instantiates a new Json schema.
     *
     * @param schemaId         the schema id
     * @param anschemaSupplier the schema supplier
     */
    public JsonSchema(final String schemaId,
                      final Function<String, Map<String, Object>>
                              anschemaSupplier) {

        this(anschemaSupplier.apply(schemaId), anschemaSupplier);
    }

    /**
     * Instantiates a new Json schema.
     *
     * @param anschemaMap      the schema map
     * @param anschemaSupplier the schema supplier
     */
    public JsonSchema(final Map<String, Object> anschemaMap,
                      final Function<String, Map<String, Object>>
                              anschemaSupplier) {
        this.schemaMap = anschemaMap;
        this.schemaSupplier = anschemaSupplier;

        //Calculate and store Parent related attributes
        parents = new ArrayList<>();
        properties = new HashMap<>();
        String reference;

        if (schemaMap.get("properties") != null) {
            properties.putAll((Map<String, Map<String, Object>>) schemaMap.get(
                    "properties"));
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

        idPropertyNames =
                parent == null ? (List<String>) schemaMap.get("ids")
                        : parent.getIdPropertyNames();

        if (idPropertyNames != null) {
            Collections.sort(idPropertyNames);
        }
    }

    /**
     * Gets the list of parents from bottom to top.
     *
     * @return parents parents
     */
    public List<JsonSchema> getParents() {
        return parents;
    }

    /**
     * Gets Parent of the Schema if exists.
     *
     * @return parent
     */
    public JsonSchema getParent() {
        return parent;
    }

    /**
     * Is root boolean.
     *
     * @return the boolean
     */
    public boolean isRoot() {
        return getRoot() == this;
    }

    /**
     * Gets Root of the Schema if exists, else returns itself.
     *
     * @return root
     */
    public JsonSchema getRoot() {
        return parents.isEmpty() ? this : parents.get(parents.size() - 1);
    }

    /**
     * Get Consolidated Properties of Schema and it's parents. Returns empty map
     * if none exists
     *
     * @return properties
     */
    public Map<String, Map<String, Object>> getProperties() {

        return properties;
    }

    /**
     * get the id properties from the Json Schema in asending order.
     *
     * @return id property names
     */
    public List<String> getIdPropertyNames() {
        return idPropertyNames;
    }

    /**
     * Get id keys simple entry [ ].
     *
     * @param jsonData the json data
     * @return the simple entry [ ]
     */
    public SimpleEntry<String, Object>[] getIdKeys(
            final Map<String, Object> jsonData) {
        SimpleEntry<String, Object>[] idKeys = null;
        if (idPropertyNames != null) {
            int idsCount = idPropertyNames.size();
            idKeys = new SimpleEntry[idsCount];
            String propName;
            for (int i = 0; i < idsCount; i++) {
                propName = idPropertyNames.get(i);
                idKeys[i] = new SimpleEntry(propName, jsonData.get(propName));
            }
            return idKeys;
        }
        return idKeys;
    }

    /**
     * Gets id values as string.
     *
     * @param jsonData the json data
     * @return the id values as string
     */
    public String getIdValuesAsString(final Map<String, Object> jsonData) {
        Object[] idValues = getIdValues(jsonData);
        return getIdValuesAsString(idValues);
    }

    /**
     * Gets id values as string.
     *
     * @param idValuesasMap the id valuesas map
     * @return the id values as string
     */
    public String getIdValuesAsString(
            final AbstractMap.SimpleEntry<String, Object>... idValuesasMap) {
        Object[] idValues = getIdValues(idValuesasMap);
        return getIdValuesAsString(idValues);
    }

    /**
     * Gets id values as string.
     *
     * @param idValues the id values
     * @return the id values as string
     */
    public String getIdValuesAsString(final Object[] idValues) {
        return idValues == null ? null
                : Arrays.asList(idValues).stream().map(n -> n.toString())
                .collect(Collectors.joining("-"));
    }

    /**
     * Get Id Values using ipValue pairs.
     *
     * @param idValuesAsMap the id values as map
     * @return object [ ]
     */
    public Object[] getIdValues(
            final AbstractMap.SimpleEntry<String, Object>... idValuesAsMap) {
        Map<String, Object> jsonData = new HashMap<>();

        for (SimpleEntry<String, Object> simpleEntry : idValuesAsMap) {
            jsonData.put(simpleEntry.getKey(), simpleEntry.getValue());
        }
        return getIdValues(jsonData);
    }

    /**
     * Get the id values from the json data.
     *
     * @param jsonData the json data
     * @return object [ ]
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
     * @return localized property names
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

    /**
     * Gets graph ql schema.
     *
     * @return the graph ql schema
     */
    public String getGraphQLSchema() {
        Map<String, Object> cSchema = getCompositeSchema();

        StringBuilder stringBuilder =
                new StringBuilder(getGraphQLType(getId(), cSchema));

        Map<String, Map<String, Object>> definitions =
                (Map<String, Map<String, Object>>) cSchema.get("definitions");

        definitions.forEach((name, definition) -> {
            stringBuilder.append("\n")
                    .append(getGraphQLType(name, definition));
        });

        return stringBuilder.toString();
    }

    private String getGraphQLType(final String id, final Map<String,
            Object> schama) {
        StringBuilder stringBuilder = new StringBuilder("type ");
        stringBuilder.append(id).append(" {").append("\n");
        Map<String, Map<String, Object>> sproperties =
                (Map<String, Map<String, Object>>)
                        schama.get("properties");
        if (sproperties != null) {
            sproperties.forEach((name, property) -> {
                stringBuilder.append(name)
                        .append(": ")
                        .append(getGraphQLFieldType(property))
                        .append("\n");
            });
        }

        return stringBuilder.append("}").toString();
    }

    private String getGraphQLFieldType(final Map<String, Object> property) {
        String jsonSchemaType = (String) property.get("type");
        String graphQLType = (String) property.get("$ref");
        if (jsonSchemaType != null) {
            if (jsonSchemaType.equals("array")) {
                Map<String, Object> items =
                        (Map<String, Object>) property.get("items");

                if (items != null) {
                    jsonSchemaType = (String) items.get("type");
                    if (jsonSchemaType == null) {
                        graphQLType = (String) items.get("$ref");
                    } else {
                        graphQLType = getGraphQLFieldType(jsonSchemaType);
                    }
                }

                graphQLType = "[" + graphQLType + "]";

            } else {
                graphQLType = getGraphQLFieldType(jsonSchemaType);
            }
        }

        return graphQLType.replaceFirst("#/definitions/", "");
    }

    private String getGraphQLFieldType(final String jsonSchemaType) {

        switch (jsonSchemaType) {
            case "string":
                return "String";
            case "integer":
                return "Int";
            case "number":
                return "Float";
            case "boolean":
                return "Boolean";
        }
        // Custom Object.
        return null;
    }

    /**
     * Gets composite schema.
     *
     * @return the composite schema
     */
    public Map<String, Object> getCompositeSchema() {
        // This is going to contain one more item (definitions).
        Map<String, Object> compositeSchema = cloneMap(schemaMap);

        Map<String, Map<String, Object>> definitions =
                getDefinitions(compositeSchema);
        if (!definitions.isEmpty()) {
            compositeSchema.put("definitions", definitions);
        }

        return compositeSchema;
    }

    /**
     * gets valid json property name to put inside definitions of json
     * schema.
     *
     * @param referenceUrl the reference url
     * @return json property name
     */
    public String getJSONPropertyName(final String referenceUrl) {
        return referenceUrl.replaceAll("http", "").replaceAll(":", "")
                .replaceAll("/", "").replaceAll("\\.", "").replaceAll("#", "");
    }

    private Map<String, Map<String, Object>> getDefinitions(
            final Map<String, Object> schemaAsMap) {
        Map<String, Map<String, Object>> definitions = new HashMap();
        if (schemaAsMap != null) {
            schemaAsMap.entrySet().forEach((schemaEntry) -> {
                if (schemaEntry.getKey().equals("$ref")) {
                    String referencePath = schemaEntry.getValue().toString();
                    // If not JSON Pointer
                    if (!referencePath.startsWith("#/")) {
                        Map<String, Object> parentScemaMap =
                                schemaSupplier.apply(referencePath);
                        String jsonPathName =
                                getJSONPropertyName(referencePath);
                        definitions.put(jsonPathName, parentScemaMap);
                        definitions.putAll(getDefinitions(parentScemaMap));
                        schemaEntry.setValue("#/definitions/" + jsonPathName);
                    }

                } else if (schemaEntry.getKey().equals("properties")) {
                    Map<String, Map<String, Object>> props =
                            (Map<String, Map<String, Object>>)
                                    schemaEntry.getValue();

                    props.entrySet().forEach(propertyEntry -> {
                        String referencePath =
                                (String) propertyEntry.getValue().get("$ref");
                        if (referencePath == null) {
                            Map<String, Object> itemsMap =
                                    (Map<String, Object>)
                                            propertyEntry.getValue()
                                                    .get("items");
                            if (itemsMap != null) {
                                referencePath = (String) itemsMap.get("$ref");
                                if (referencePath != null) {
                                    Map<String, Object> propScemaMap =
                                            schemaSupplier.apply(
                                                    referencePath);
                                    String jsonPathName =
                                            getJSONPropertyName(referencePath);
                                    definitions.put(jsonPathName,
                                            propScemaMap);
                                    definitions.putAll(
                                            getDefinitions(propScemaMap));
                                    itemsMap.put("$ref",
                                            "#/definitions/" + jsonPathName);
                                }
                            }

                        } else if (!referencePath.startsWith("#/")) {
                            Map<String, Object> propScemaMap =
                                    schemaSupplier.apply(referencePath);
                            String jsonPathName =
                                    getJSONPropertyName(referencePath);
                            definitions.put(jsonPathName, propScemaMap);
                            definitions.putAll(getDefinitions(propScemaMap));
                            propertyEntry.getValue().put("$ref",
                                    "#/definitions/" + jsonPathName);
                        }
                    });

                }
            });
        }
        return definitions;
    }

    /**
     * Delocalize the given json data.
     *
     * @param jsonData the json data
     * @param locale   the locale
     * @return map
     */
    public Map<String, Object> delocalizeData(
            final Map<String, Object> jsonData, final Locale locale) {

        if (jsonData != null) {
            Map<String, Object> delocalizeJsonData;
            if (locale != null) {
                delocalizeJsonData = new HashMap<>(jsonData.size());
                List<String> localizedKeys = getLocalizedPropertyNames();

                jsonData.entrySet().forEach((entry) -> {

                    final String key = entry.getKey();
                    final Object value = entry.getValue();

                    if (localizedKeys.contains(key)) {
                        String localizedKey =
                                key + LOCALE_SEPARATOR + locale.getLanguage();
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
                        } else if (key.contains(
                                LOCALE_SEPARATOR + locale.getLanguage())) {
                            delocalizeJsonData.put(key.substring(0,
                                    key.lastIndexOf(LOCALE_SEPARATOR)), value);
                        }

                    }

                    if (value instanceof Map) {
                        Map<String, Object> nestedObjectMap =
                                (Map<String, Object>) value;
                        delocalizeJsonData.put(key,
                                getSchemaOf(key).delocalizeData(
                                        nestedObjectMap, locale));

                    } else if (value instanceof List) {
                        List nestedList = (List) value;
                        List newList = new ArrayList(nestedList.size());
                        for (Object object : nestedList) {
                            if (object instanceof Map) {
                                Map<String, Object> nestedObjectMap =
                                        (Map<String, Object>) object;
                                newList.add(getSchemaOf(key).delocalizeData(
                                        nestedObjectMap, locale));

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
     * Delocalize the given json data.
     *
     * @param jsonData the json data
     * @return map
     */
    public Map<String, Object> defaultLocaleData(
            final Map<String, Object> jsonData) {

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
                    Map<String, Object> nestedObjectMap =
                            (Map<String, Object>) value;
                    delocalizeJsonData.put(key,
                            getSchemaOf(key).defaultLocaleData(
                                    nestedObjectMap));

                } else if (value instanceof List) {
                    List nestedList = (List) value;
                    List newList = new ArrayList(nestedList.size());
                    for (Object object : nestedList) {
                        if (object instanceof Map) {
                            Map<String, Object> nestedObjectMap =
                                    (Map<String, Object>) object;
                            newList.add(getSchemaOf(key).defaultLocaleData(
                                    nestedObjectMap));

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

    /**
     * Localize data map.
     *
     * @param jsonData the json data
     * @param locale   the locale
     * @return the map
     */
    public Map<String, Object> localizeData(final Map<String, Object> jsonData,
                                            final Locale locale) {
        return localizeData(jsonData, locale, Boolean.FALSE);
    }

    /**
     * Localize the given data and copy to new Map. all localized fields
     * property will be appended with locale separator. if property name is
     * title it will be renamed as title_zh for chineese locale
     *
     * @param jsonData    the json data
     * @param locale      the locale
     * @param keepDefault the keep default
     * @return map
     */
    public Map<String, Object> localizeData(final Map<String, Object> jsonData,
                                            final Locale locale,
                                            final Boolean keepDefault) {

        Map<String, Object> localizedJsonData = new HashMap<>(jsonData.size());
        List<String> localizedKeys = getLocalizedPropertyNames();

        jsonData.entrySet().forEach((entry) -> {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (localizedKeys.contains(key)) {
                localizedJsonData.put(
                        key + LOCALE_SEPARATOR + locale.getLanguage(), value);
                if (keepDefault) {
                    localizedJsonData.put(key, value);
                }
            } else {
                localizedJsonData.put(key, value);
            }

            if (value instanceof Map) {
                Map<String, Object> nestedObjectMap =
                        (Map<String, Object>) value;
                localizedJsonData.put(key,
                        getSchemaOf(key).localizeData(nestedObjectMap, locale,
                                keepDefault));
            } else if (value instanceof List) {
                List nestedList = (List) value;
                List newList = new ArrayList(nestedList.size());
                for (Object object : nestedList) {
                    if (object instanceof Map) {
                        Map<String, Object> nestedObjectMap =
                                (Map<String, Object>) object;
                        newList.add(
                                getSchemaOf(key).localizeData(nestedObjectMap,
                                        locale, keepDefault));

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
     * get the value from Json Schema supplier.
     *
     * @param schemaId
     * @return JsonSchema
     */
    private JsonSchema getJsonSchema(final String schemaId) {
        try {
            return this.getClass()
                    .getDeclaredConstructor(String.class, Function.class)
                    .newInstance(schemaId, this.schemaSupplier);
        } catch (NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            Logger.getLogger(JsonSchema.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * get property for a given name, returns null if not exists.
     *
     * @param propertyName the property name
     * @return property of
     */
    public Map<String, Object> getPropertyOf(final String propertyName) {
        return properties.get(propertyName);
    }

    /**
     * Gets schema of.
     *
     * @param propertyName the property name
     * @return the schema of
     */
    public JsonSchema getSchemaOf(final String propertyName) {

        String schemaId = null;
        Map<String, Object> map = getPropertyOf(propertyName);
        if (map != null) {
            schemaId = (String) map.get("$ref");
            // Might be array item. So check inside items property
            if (schemaId == null
                    && (map = (Map<String, Object>) map.get("items")) != null) {
                schemaId = (String) map.get("$ref");
            }
        }

        return getJsonSchema(schemaId);
    }

    /**
     * toString method.
     *
     * @return string
     */
    @Override
    public String toString() {
        return asString();
    }

    /**
     * Gets id of Json Schema.
     *
     * @return id
     */
    public String getId() {
        return (String) schemaMap.get("$id");
    }

    /**
     * Gets title of Json Schema.
     *
     * @return title
     */
    public String getTitle() {
        return (String) schemaMap.get("title");
    }

    /**
     * Gets description of Json Schema.
     *
     * @return description
     */
    public String getDescription() {
        return (String) schemaMap.get("description");
    }

    /**
     * Gets Schema as a JSON String.
     *
     * @return string
     */
    protected abstract String asString();

    /**
     * Validate set.
     *
     * @param jsonData the json data
     * @return the set
     */
    public abstract Set<ConstraintViolation> validate(
            Map<String, Object> jsonData);

}
