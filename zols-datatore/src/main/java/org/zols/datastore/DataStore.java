package org.zols.datastore;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.zols.datastore.jsonschema.JSONSchema;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchemaForSchema;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchema;
import static org.zols.datastore.util.JsonUtil.asMap;
import org.zols.datatore.exception.DataStoreException;

/**
 * Data Store is used to Store Static and Dynamic Objects using JSON Schema
 *
 * @author Sathish Kumar Thiyagarajan
 */
public abstract class DataStore {

    public Map<String, Object> createSchema(
            String jsonSchemaTxt)
            throws DataStoreException {
        if (jsonSchemaForSchema().validate(getEnlargedSchema(asMap(jsonSchemaTxt))) == null) {
            return create(jsonSchemaForSchema(), asMap(jsonSchemaTxt));
        }
        return null;
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
            schema = getOrderedSchema(schema);
        }
        return schema;
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
        schema.remove("id");
        linkedHashMap.put("$schema", schema.get("$schema"));
        if (schema.get("definitions") != null) {
            linkedHashMap.put("definitions", schema.get("definitions"));
        }
        for (Map.Entry<String, Object> schemaEntry : schema.entrySet()) {
            linkedHashMap.putIfAbsent(schemaEntry.getKey(), schemaEntry.getValue());
        }
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
            } else if (schemaEntry.getValue() instanceof Map) {
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
     * @param idValue dynamic object name
     * @return status of the delete operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract boolean delete(JSONSchema jsonSchema,
            String idValue) throws DataStoreException;

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
     * Drops the DataStore
     *
     * @throws DataStoreException
     */
    protected abstract void drop() throws DataStoreException;

}
