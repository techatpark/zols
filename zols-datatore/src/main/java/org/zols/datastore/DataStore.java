package org.zols.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import javax.validation.ConstraintViolation;
import org.zols.datastore.jsonschema.JSONSchema;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchemaForSchema;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchema;
import org.zols.datastore.query.Query;
import static org.zols.datastore.util.JsonUtil.asMap;
import static org.zols.datastore.util.JsonUtil.asObject;
import org.zols.datatore.exception.DataStoreException;

/**
 * Data Store is used to Store Static and Dynamic Objects using JSON Schema
 *
 * @author Sathish Kumar Thiyagarajan
 */
public abstract class DataStore {

    public <T> T create(T object) throws DataStoreException {
        T createdObject = null;
        if (object != null) {
            createdObject = (T) asObject(object.getClass(),
                    create(jsonSchema(object.getClass()), asMap(object)));
        }
        return createdObject;
    }

    public <T> T read(Class<T> clazz, String idValue) throws DataStoreException {
        return (T) asObject(clazz, read(jsonSchema(clazz), idValue));
    }

    public <T> T update(T object, String idValue) throws DataStoreException {
        T updatedObject = null;
        if (object != null) {
            boolean updated = update(jsonSchema(object.getClass()), asMap(object));
            if (updated) {
                updatedObject = (T) read(object.getClass(), idValue);
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
        JSONSchema jSONSchema = jsonSchema(getSchema(schemaId));
        if (jSONSchema.validate(jsonData) == null) {
            return create(jSONSchema, jsonData);
        }
        return null;
    }

    public Map<String, Object> read(String schemaId, String name)
            throws DataStoreException {
        return read(jsonSchema(getSchema(schemaId)), name);
    }

    public boolean update(String schemaId, Map<String, Object> jsonData)
            throws DataStoreException {
        JSONSchema jSONSchema = jsonSchema(getSchema(schemaId));
        if (jSONSchema.validate(jsonData) == null) {
            return update(jSONSchema, jsonData);
        }
        return false;
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
        Object idField = enlargedSchema.remove("idField");
        Object id = enlargedSchema.remove("id");
        if (jsonSchemaForSchema().validate(enlargedSchema) == null) {
            return create(jsonSchemaForSchema(), asMap(jsonSchemaTxt));
        }
        return null;
    }

    public boolean updateSchema(String jsonSchemaTxt)
            throws DataStoreException {
        Map<String, Object> enlargedSchema = getEnlargedSchema(asMap(jsonSchemaTxt));
        Object idField = enlargedSchema.remove("idField");
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

    public Set<ConstraintViolation> validate(String schemaId,
            Map<String, Object> jsonData)
            throws DataStoreException {
        return jsonSchema(getEnlargedSchema(getSchema(schemaId))).validate(jsonData);
    }

    private Map<String, Object> getEnlargedSchema(Map<String, Object> schema)
            throws DataStoreException {
        if (schema != null) {
            Map<String, Object> definitions = new HashMap<>();
            walkSchemaTree(schema, definitions);

            if (definitions.size() > 0) {
                schema.put("definitions", definitions);
            }

            List<Map<String, Object>> idFieldSchemas = new ArrayList<>();
            fillIdFields(idFieldSchemas, schema);

            for (Map<String, Object> idFieldSchema : idFieldSchemas) {
                schema.put("idField", idFieldSchema.remove("idField"));
                schema.put("base", idFieldSchema.get("id"));
            }
            schema = getOrderedSchema(schema);

        }
        return schema;
    }

    private void fillIdFields(List<Map<String, Object>> idFieldSchemas,
            Map<String, Object> schema) {
        for (Map.Entry<String, Object> schemaEntry : schema.entrySet()) {
            if (schemaEntry.getKey().equals("idField")) {
                idFieldSchemas.add(schema);
            } else if (schemaEntry.getValue() instanceof Map) {
                fillIdFields(idFieldSchemas, (Map) schemaEntry.getValue());
            }
        }
    }

    private Map<String, Object> fillDefinitions(String schemaId, Map<String, Object> definitions)
            throws DataStoreException {
        Map<String, Object> schema = getSchema(schemaId);
        if (schema != null) {
            walkSchemaTree(schema, definitions);
            schema = getOrderedSchema(schema);
        }
        return schema;
    }

    private LinkedHashMap<String, Object> getOrderedSchema(Map<String, Object> schema) {
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>(schema.size());
        Object id = schema.remove("id");
        if (schema.get("$schema") != null) {
            linkedHashMap.put("$schema", schema.get("$schema"));
        }
        if (schema.get("definitions") != null) {
            linkedHashMap.put("definitions", schema.get("definitions"));
        }
        schema.entrySet().stream().forEach((schemaEntry) -> {
            linkedHashMap.putIfAbsent(schemaEntry.getKey(), schemaEntry.getValue());
        });
        //linkedHashMap.put("id", id);
        return linkedHashMap;
    }

    private void walkSchemaTree(Map<String, Object> schema,
            Map<String, Object> definitions) throws DataStoreException {
        String schemaId;
        for (Map.Entry<String, Object> schemaEntry : schema.entrySet()) {
            if (schemaEntry.getKey().equals("$ref")) {
                schemaId = schemaEntry.getValue().toString();
                definitions.put(schemaId, fillDefinitions(schemaEntry.getValue().toString(), definitions));
                schemaEntry.setValue("#/definitions/" + schemaId);
            } //            else if (schemaEntry.getKey().equals("idField")) {
            //                definitions.put("idField", schemaEntry.getValue());
            //                
            //            } 
            else if (schemaEntry.getValue() instanceof Map) {
                walkSchemaTree((Map<String, Object>) schemaEntry.getValue(), definitions);
            }
        }
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
     * @param query query to consider
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract List<Map<String, Object>> list(JSONSchema jsonSchema, Query query)
            throws DataStoreException;

    /**
     * Drops the DataStore
     *
     * @throws DataStoreException
     */
    protected abstract void drop() throws DataStoreException;

}
