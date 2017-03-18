package org.zols.datastore;

import java.util.HashMap;
import java.util.*;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.zols.datastore.jsonschema.JSONSchema;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchemaForSchema;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchema;
import org.zols.datastore.query.Filter;
import static org.zols.datastore.query.Filter.Operator.NOT_EQUALS;
import org.zols.datastore.query.Query;
import org.zols.datastore.util.JsonUtil;
import static org.zols.datastore.util.JsonUtil.asMap;
import static org.zols.datastore.util.JsonUtil.asString;
import org.zols.datatore.exception.ConstraintViolationException;
import org.zols.datatore.exception.DataStoreException;

/**
 * Data Store is used to Store Static and Dynamic Objects using JSON Schema
 *
 * @author Sathish Kumar Thiyagarajan
 */
public abstract class DataStore {

    private final Validator validator;

    public DataStore() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public <T> T create(T object) throws DataStoreException {
        T createdObject = null;
        if (object != null) {
            Set<ConstraintViolation<Object>> violations = validator.validate(object);
            if (violations.isEmpty()) {
                JSONSchema jsonSchema = jsonSchema(object.getClass());
                createdObject = (T) asJsonDataObject(object.getClass(),
                        create(jsonSchema, jsonSchema.getImmutableJSONData(object)));
            } else {
                throw new ConstraintViolationException(object, violations);
            }

        }
        return createdObject;
    }

    public static <T> T asJsonDataObject(Class<T> clazz, Map<String, Object> map) {
        if (map != null) {
            map.remove("$type");
        }
        return JsonUtil.asObject(clazz, map);
    }

    public <T> T read(Class<T> clazz, String idValue) throws DataStoreException {
        return (T) asJsonDataObject(clazz, read(jsonSchema(clazz), idValue));
    }

    public <T> T update(T object, String idValue) throws DataStoreException {
        T updatedObject = null;
        if (object != null) {
            Set<ConstraintViolation<Object>> violations = validator.validate(object);
            if (violations.isEmpty()) {
                JSONSchema jsonSchema = jsonSchema(object.getClass());
                boolean updated = update(jsonSchema, jsonSchema.getImmutableJSONData(object));
                if (updated) {
                    updatedObject = (T) read(object.getClass(), idValue);
                }
            } else {
                throw new ConstraintViolationException(object, violations);
            }
        }
        return updatedObject;
    }

    public boolean delete(Class clazz) throws DataStoreException {
        return delete(jsonSchema(clazz));
    }

    public boolean delete(Class clazz, String idValue) throws DataStoreException {
        return delete(jsonSchema(clazz), idValue);
    }

    public boolean delete(Class clazz, Query query)
            throws DataStoreException {
        return delete(jsonSchema(clazz), query);
    }

    public <T> List<T> list(Class<T> clazz) throws DataStoreException {
        List<T> objects = null;
        List<Map<String, Object>> maps = list(jsonSchema(clazz));
        if (maps != null) {
            objects = maps.stream().map(map -> asJsonDataObject(clazz, map)).collect(toList());
        }
        return objects;
    }

    public <T> List<T> list(Class<T> clazz, Query query) throws DataStoreException {
        List<T> objects = null;
        List<Map<String, Object>> maps = list(jsonSchema(clazz), query);
        if (maps != null) {
            objects = maps.stream().map(map -> asJsonDataObject(clazz, map)).collect(toList());
        }
        return objects;
    }

    public <T> Page<T> list(Class<T> clazz, Integer pageNumber, Integer pageSize) throws DataStoreException {
        Page<Map<String, Object>> page = list(jsonSchema(clazz), pageNumber, pageSize);
        if (page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(), page.getTotal(), page.getContent().stream().map(map -> asJsonDataObject(clazz, map)).collect(toList()));
        }
        return null;
    }

    public <T> Page<T> list(Class<T> clazz, Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        Page<Map<String, Object>> page = list(jsonSchema(clazz), query, pageNumber, pageSize);
        if (page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(), page.getTotal(), page.getContent().stream().map(map -> asJsonDataObject(clazz, map)).collect(toList()));
        }
        return null;
    }

    /**
     * Data Related
     */
    /**
     *
     * @param schemaId
     * @param jsonData
     * @return
     * @throws DataStoreException
     */
    public Map<String, Object> create(String schemaId, Map<String, Object> jsonData)
            throws DataStoreException {

        Map<String, Object> rawJsonSchema = getRawJsonSchema(schemaId);

        JSONSchema enlargedJsonSchema = jsonSchema(rawJsonSchema);
        Set<ConstraintViolation<Object>> violations = enlargedJsonSchema.validate(jsonData);
        if (violations == null) {
            return create(enlargedJsonSchema, enlargedJsonSchema.getImmutableJSONData(jsonData));
        } else {
            throw new ConstraintViolationException(jsonData, violations);
        }
    }

    public Map<String, Object> read(String schemaId, String name)
            throws DataStoreException {
        return read(jsonSchema(getRawJsonSchema(schemaId)), name);
    }

    public boolean update(String schemaId, Map<String, Object> jsonData)
            throws DataStoreException {
        JSONSchema enlargedJsonSchema = jsonSchema(getRawJsonSchema(schemaId));
        Set<ConstraintViolation<Object>> violations = enlargedJsonSchema.validate(jsonData);
        if (violations == null) {
            return update(enlargedJsonSchema, enlargedJsonSchema.getImmutableJSONData(jsonData));
        } else {
            throw new ConstraintViolationException(jsonData, violations);
        }
    }

    public boolean updatePartial(String schemaId, String idValue, Map<String, Object> partialJsonData)
            throws DataStoreException {
        JSONSchema enlargedJsonSchema = jsonSchema(getRawJsonSchema(schemaId));
        Map<String, Object> jsonData = read(enlargedJsonSchema, idValue);
        jsonData.putAll(partialJsonData);
        Set<ConstraintViolation<Object>> violations = enlargedJsonSchema.validate(jsonData);
        if (violations == null) {
            return update(enlargedJsonSchema, enlargedJsonSchema.getImmutableJSONData(jsonData));
        } else {
            throw new ConstraintViolationException(jsonData, violations);
        }
    }

    public boolean delete(String schemaId)
            throws DataStoreException {
        return delete(jsonSchema(getRawJsonSchema(schemaId)));
    }

    public boolean delete(String schemaId, String name)
            throws DataStoreException {
        return delete(jsonSchema(getRawJsonSchema(schemaId)), name);
    }

    public boolean delete(String schemaId, Query query)
            throws DataStoreException {
        return delete(jsonSchema(getRawJsonSchema(schemaId)), query);
    }

    public List<Map<String, Object>> list(String schemaId)
            throws DataStoreException {
        return list(jsonSchema(getRawJsonSchema(schemaId)));
    }

    public List<Map<String, Object>> list(String schemaId, Query query)
            throws DataStoreException {
        return list(jsonSchema(getRawJsonSchema(schemaId)), query);
    }

    public Page<Map<String, Object>> list(String schemaId, Integer pageNumber, Integer pageSize)
            throws DataStoreException {
        return list(jsonSchema(getRawJsonSchema(schemaId)), pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(String schemaId, Query query, Integer pageNumber, Integer pageSize)
            throws DataStoreException {
        return list(jsonSchema(getRawJsonSchema(schemaId)), query, pageNumber, pageSize);
    }

    /**
     * *
     * Schema Related
     */
    /**
     *
     * @param jsonSchemaTxt
     * @return
     * @throws DataStoreException
     */
    public Map<String, Object> createSchema(String jsonSchemaTxt)
            throws DataStoreException {
        Map<String, Object> schema = asMap(jsonSchemaTxt);
        Map<String, Object> rawJsonSchema = getRawJsonSchema(schema);
        JSONSchema jsonSchema = jsonSchemaForSchema();
        Set<ConstraintViolation<Object>> violations = jsonSchema.validate(rawJsonSchema);
        if (violations == null) {
            return create(jsonSchemaForSchema(), jsonSchema.getImmutableJSONData(schema));
        } else {
            throw new ConstraintViolationException(schema, violations);
        }
    }

    public boolean updateSchema(String jsonSchemaTxt)
            throws DataStoreException {
        Map<String, Object> schema = asMap(jsonSchemaTxt);
        Map<String, Object> rawJsonSchema = getRawJsonSchema(asMap(jsonSchemaTxt));
        JSONSchema jsonSchema = jsonSchemaForSchema();
        Set<ConstraintViolation<Object>> violations = jsonSchema.validate(rawJsonSchema);

        if (violations == null) {
            return update(jsonSchema, jsonSchema.getImmutableJSONData(asMap(jsonSchemaTxt)));
        } else {
            throw new ConstraintViolationException(schema, violations);
        }
    }

    public Map<String, Object> getRawJsonSchema(String schemaId)
            throws DataStoreException {
        return getRawJsonSchema(read(jsonSchemaForSchema(), schemaId));
    }

    public Map<String, Object> getSchema(String schemaId)
            throws DataStoreException {
        return read(jsonSchemaForSchema(), schemaId);
    }

    public Set<ConstraintViolation<Object>> validate(String schemaId,
            Map<String, Object> jsonData)
            throws DataStoreException {
        return jsonSchema(getRawJsonSchema(getSchema(schemaId))).validate(jsonData);
    }

    public Map<String, Object> getRawJsonSchema(final Map<String, Object> schema)
            throws DataStoreException {
        Map<String, Object> clonedSchema = asMap(asString(schema));
        if (clonedSchema != null) {
            clonedSchema.remove("$type");
            Map<String, Object> definitions = getDefinitions(clonedSchema);

            if (!definitions.isEmpty()) {
                definitions.entrySet().forEach((definition) -> {
                    try {
                        definition.setValue(getRawJsonSchema(getSchema(definition.getKey())));
                    } catch (DataStoreException ex) {
                        Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                clonedSchema.put("definitions", definitions);

                clonedSchema.put("definitions", jsonSchema(clonedSchema).getConsolidatedDefinitions());

            }
        }
        return clonedSchema;
    }

    private Map<String, Object> getDefinitions(Map<String, Object> schema) {
        Map<String, Object> definitions = new HashMap();
        schema.entrySet().forEach((schemaEntry) -> {
            if (schemaEntry.getKey().equals("$ref")) {
                definitions.put(schemaEntry.getValue().toString(), null);
                schemaEntry.setValue("#/definitions/" + schemaEntry.getValue());

            } else if (schemaEntry.getKey().equals("properties")) {
                Map<String, Map<String, Object>> properties = (Map<String, Map<String, Object>>) schemaEntry.getValue();

                properties.entrySet().forEach(propertyEntry -> {
                    Object refValue;
                    if ((refValue = propertyEntry.getValue().get("$ref")) != null) {
                        definitions.put(refValue.toString(), null);
                        propertyEntry.getValue().put("$ref", "#/definitions/" + refValue.toString());
                    }
                });

            }
        });
        return definitions;
    }

    public Boolean deleteSchema(String schemaId) throws DataStoreException {
        return delete(jsonSchemaForSchema(), schemaId);
    }

    public List<Map<String, Object>> listSchema() throws DataStoreException {
        return list(jsonSchemaForSchema());
    }

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected List<Map<String, Object>> list(JSONSchema jsonSchema)
            throws DataStoreException {
        return list(jsonSchema, getTypeFilteredQuery(jsonSchema));
    }

    private Query getTypeFilteredQuery(JSONSchema jsonSchema) {
        Query query = new Query();
        List<String> superTypes = jsonSchema.superTypes();
        if (!superTypes.isEmpty()) {
            superTypes.forEach(type -> query.addFilter(new Filter("$type", NOT_EQUALS, type)));
        }
        return query;
    }
    

    private boolean delete(JSONSchema jsonSchema) throws DataStoreException {
        return delete(jsonSchema, getTypeFilteredQuery(jsonSchema));
    }

    /**
     * ALL ABSTRACT METHODS WILL COME HERE
     */
    /**
     *
     * @param validatedDataObject validated object
     * @param jsonSchema schema of dynamic data
     * @return dynamic data as map
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract Map<String, Object> create(
            JSONSchema jsonSchema,
            Map<String, Object> validatedDataObject)
            throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param idValue dynamic object name
     * @return dynamic data as map
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract Map<String, Object> read(
            JSONSchema jsonSchema,
            String idValue) throws DataStoreException;



    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param idValue dynamic object name
     * @return status of the delete operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract boolean delete(JSONSchema jsonSchema,
            String idValue) throws DataStoreException;

    /**
     *
     * @param jsonSchema
     * @param query
     * @return
     * @throws DataStoreException
     */
    protected abstract boolean delete(JSONSchema jsonSchema, Query query)
            throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param validatedData validated Object
     * @return status of the update operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract boolean update(JSONSchema jsonSchema,
            Map<String, Object> validatedData)
            throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param pageNumber
     * @param pageSize
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract Page<Map<String, Object>> list(JSONSchema jsonSchema,
            Integer pageNumber,
            Integer pageSize)
            throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param query query to consider
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract List<Map<String, Object>> list(JSONSchema jsonSchema,
            Query query)
            throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param query query to consider
     * @param pageNumber
     * @param pageSize
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract Page<Map<String, Object>> list(JSONSchema jsonSchema,
            Query query,
            Integer pageNumber,
            Integer pageSize)
            throws DataStoreException;

    /**
     * Drops the DataStore
     *
     * @throws DataStoreException
     */
    protected abstract void drop() throws DataStoreException;

}
