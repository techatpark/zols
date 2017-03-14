package org.zols.datastore;

import java.util.HashMap;
import java.util.List;
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
import org.zols.datastore.query.Query;
import static org.zols.datastore.util.JsonUtil.asMap;
import static org.zols.datastore.util.JsonUtil.asObject;
import static org.zols.datastore.util.MapUtil.removeNestedElements;
import org.zols.datatore.exception.ConstraintViolationException;
import org.zols.datatore.exception.DataStoreException;

/**
 * Data Store is used to Store Static and Dynamic Objects using JSON Schema
 *
 * @author Sathish Kumar Thiyagarajan
 */
public abstract class DataStore {

    private Validator validator;

    public DataStore() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public <T> T create(T object) throws DataStoreException {
        T createdObject = null;
        if (object != null) {
            Set<ConstraintViolation<Object>> violations = validator.validate(object);
            if (violations.isEmpty()) {
                createdObject = (T) asObject(object.getClass(),
                        create(jsonSchema(object.getClass()), asMap(object)));
            } else {
                throw new ConstraintViolationException(object, violations);
            }

        }
        return createdObject;
    }

    public <T> T read(Class<T> clazz, String idValue) throws DataStoreException {
        return (T) asObject(clazz, read(jsonSchema(clazz), idValue));
    }

    public <T> T update(T object, String idValue) throws DataStoreException {
        T updatedObject = null;
        if (object != null) {
            Set<ConstraintViolation<Object>> violations = validator.validate(object);
            if (violations.isEmpty()) {
                boolean updated = update(jsonSchema(object.getClass()), asMap(object));
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
            objects = maps.stream().map(map -> asObject(clazz, map)).collect(toList());
        }
        return objects;
    }

    public <T> List<T> list(Class<T> clazz, Query query) throws DataStoreException {
        List<T> objects = null;
        List<Map<String, Object>> maps = list(jsonSchema(clazz), query);
        if (maps != null) {
            objects = maps.stream().map(map -> asObject(clazz, map)).collect(toList());
        }
        return objects;
    }

    public <T> Page<T> list(Class<T> clazz, Integer pageNumber, Integer pageSize) throws DataStoreException {
        Page<Map<String, Object>> page = list(jsonSchema(clazz), pageNumber, pageSize);
        if (page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(), page.getTotal(), page.getContent().stream().map(map -> asObject(clazz, map)).collect(toList()));
        }
        return null;
    }

    public <T> Page<T> list(Class<T> clazz, Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        Page<Map<String, Object>> page = list(jsonSchema(clazz), query, pageNumber, pageSize);
        if (page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(), page.getTotal(), page.getContent().stream().map(map -> asObject(clazz, map)).collect(toList()));
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

        Map<String, Object> enlargedSchema = getEnlargedSchema(schemaId);
        enlargedSchema.put("id", schemaId);
        JSONSchema enlargedJsonSchema = jsonSchema(enlargedSchema);
        Set<ConstraintViolation<Object>> violations = enlargedJsonSchema.validate(jsonData);
        if (violations == null) {
            return create(enlargedJsonSchema, jsonData);
        } else {
            throw new ConstraintViolationException(jsonData, violations);
        }
    }

    public Map<String, Object> read(String schemaId, String name)
            throws DataStoreException {
        return read(jsonSchema(getSchema(schemaId)), name);
    }

    public boolean update(String schemaId, Map<String, Object> jsonData)
            throws DataStoreException {
        JSONSchema enlargedJsonSchema = jsonSchema(getEnlargedSchema(schemaId));
        Set<ConstraintViolation<Object>> violations = enlargedJsonSchema.validate(jsonData);
        if (violations == null) {
            return update(enlargedJsonSchema, jsonData);
        } else {
            throw new ConstraintViolationException(jsonData, violations);
        }
    }

    public boolean updatePartial(String schemaId, String idValue, Map<String, Object> partialJsonData)
            throws DataStoreException {
        JSONSchema enlargedJsonSchema = jsonSchema(getEnlargedSchema(schemaId));
        Map<String, Object> jsonData = read(enlargedJsonSchema, idValue);
        jsonData.putAll(partialJsonData);
        Set<ConstraintViolation<Object>> violations = enlargedJsonSchema.validate(jsonData);
        if (violations == null) {
            return update(enlargedJsonSchema, jsonData);
        } else {
            throw new ConstraintViolationException(jsonData, violations);
        }
    }

    public boolean delete(String schemaId)
            throws DataStoreException {
        return delete(jsonSchema(getSchema(schemaId)));
    }

    public boolean delete(String schemaId, String name)
            throws DataStoreException {
        return delete(jsonSchema(getSchema(schemaId)), name);
    }

    public boolean delete(String schemaId, Query query)
            throws DataStoreException {
        return delete(jsonSchema(getSchema(schemaId)), query);
    }

    public List<Map<String, Object>> list(String schemaId)
            throws DataStoreException {
        return list(jsonSchema(getSchema(schemaId)));
    }

    public List<Map<String, Object>> list(String schemaId, Query query)
            throws DataStoreException {
        return list(jsonSchema(getSchema(schemaId)), query);
    }

    public Page<Map<String, Object>> list(String schemaId, Integer pageNumber, Integer pageSize)
            throws DataStoreException {
        return list(jsonSchema(getSchema(schemaId)), pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(String schemaId, Query query, Integer pageNumber, Integer pageSize)
            throws DataStoreException {
        return list(jsonSchema(getSchema(schemaId)), query, pageNumber, pageSize);
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
        Map<String, Object> enlargedSchema = getEnlargedSchema(asMap(jsonSchemaTxt));
        removeNestedElements(enlargedSchema, "id", "idField");
        if (jsonSchemaForSchema().validate(enlargedSchema) == null) {
            return create(jsonSchemaForSchema(), asMap(jsonSchemaTxt));
        }
        return null;
    }

    public boolean updateSchema(String jsonSchemaTxt)
            throws DataStoreException {
        Map<String, Object> enlargedSchema = getEnlargedSchema(asMap(jsonSchemaTxt));
        removeNestedElements(enlargedSchema, "id", "idField");
        if (jsonSchemaForSchema().validate(enlargedSchema) == null) {
            return update(jsonSchemaForSchema(), asMap(jsonSchemaTxt));
        }
        return false;
    }

    public Map<String, Object> getEnlargedSchema(String schemaId)
            throws DataStoreException {
        return getEnlargedSchema(read(jsonSchemaForSchema(), schemaId));
    }

    public Map<String, Object> getSchema(String schemaId)
            throws DataStoreException {
        return read(jsonSchemaForSchema(), schemaId);
    }

    public Set<ConstraintViolation<Object>> validate(String schemaId,
            Map<String, Object> jsonData)
            throws DataStoreException {
        return jsonSchema(getEnlargedSchema(getSchema(schemaId))).validate(jsonData);
    }

    public Map<String, Object> getEnlargedSchema(Map<String, Object> schema)
            throws DataStoreException {
        if (schema != null) {
            Map<String, Object> definitions = getDefinitions(schema);

            if (!definitions.isEmpty()) {
                definitions.entrySet().forEach((definition) -> {
                    try {
                        definition.setValue(getEnlargedSchema(definition.getKey()));
                    } catch (DataStoreException ex) {
                        Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                schema.put("definitions", definitions);
            }
        }
        return schema;
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
                    Object refValue ;
                    if ( ( refValue = propertyEntry.getValue().get("$ref")) != null) {
                        definitions.put(refValue.toString(), null);
                        propertyEntry.getValue().put("$ref","#/definitions/" + refValue.toString());
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
     * @return status of the delete operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract boolean delete(JSONSchema jsonSchema)
            throws DataStoreException;

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
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract List<Map<String, Object>> list(JSONSchema jsonSchema)
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
