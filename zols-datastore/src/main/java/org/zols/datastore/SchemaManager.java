/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import org.zols.datastore.persistence.DataStorePersistence;
import org.zols.datastore.query.MapQuery;
import org.zols.jsonschema.JsonSchema;
import org.zols.jsonschema.everit.EveritJsonSchema;

import javax.validation.ConstraintViolation;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;
import static org.zols.jsonschema.util.JsonSchemaUtil.jsonSchemaForSchema;

/**
 * @author sathish
 */
public final class SchemaManager {

    /**
     * The dataStore persistence.
     */
    private final DataStorePersistence dataStorePersistence;

    /**
     * The jsonSchema.
     */
    private final JsonSchema jsonSchemaForSchema;

    /**
     * Instantiates a new schema manager.
     *
     * @param anDataStorePersistence         an dataStore persistence
     */
    public SchemaManager(final DataStorePersistence anDataStorePersistence) {
        this.dataStorePersistence = anDataStorePersistence;
        jsonSchemaForSchema = jsonSchemaForSchema();
    }

    /**
     * Creates a new JsonSchema with given Object.
     *
     * @param schemaMap Object to be Create
     * @return created JsonSchema object
     */
    public Map<String, Object> create(final Map<String, Object> schemaMap)
            throws DataStoreException {
        Set<ConstraintViolation> violations =
                jsonSchemaForSchema.validate(schemaMap);
        if (violations.isEmpty()) {
            Map<String, Object> schemaMapCreated =
                    dataStorePersistence.create(jsonSchemaForSchema,
                            schemaMap);
            dataStorePersistence.onNewSchema(
                    getJsonSchema(schemaMap.get("$id").toString()));
            return schemaMapCreated;
        } else {
            throw new ConstraintViolationException(schemaMap, violations);
        }
    }

    /**
     * Get the JsonSchema with given String.
     *
     * @param schemaId String to be Search
     * @return searched JsonSchema
     * @throws DataStoreException
     */
    public Map<String, Object> get(final String schemaId)
            throws DataStoreException {
        return dataStorePersistence.read(jsonSchemaForSchema,
                new SimpleEntry("$id", schemaId));
    }

    /**
     * Update a JsonSchema with given Object.
     *
     * @param schemaId  id of the schema
     * @param schemaMap Object to be update
     * @return status of the Update Operation
     */
    public boolean update(final String schemaId,
                          final Map<String, Object> schemaMap)
            throws DataStoreException {
        Set<ConstraintViolation> violations =
                jsonSchemaForSchema.validate(schemaMap);
        if (violations.isEmpty()) {
            JsonSchema oldSchemaMap = this.getJsonSchema(schemaId);
            boolean updated =
                    dataStorePersistence.update(jsonSchemaForSchema, schemaMap,
                            new SimpleEntry("$id", schemaId));
            if (updated) {
                dataStorePersistence.onUpdateSchema(oldSchemaMap,
                        this.getJsonSchema(schemaId));
            }
            return updated;
        } else {
            throw new ConstraintViolationException(schemaMap, violations);
        }
    }

    /**
     * Delete a JsonSchema with given String.
     *
     * @param schemaId String to be delete
     * @return status of the Delete Operation
     */
    public Boolean delete(final String schemaId) throws DataStoreException {
        boolean isDeleted;
        JsonSchema jsonSchema = getJsonSchema(schemaId);
        isDeleted = dataStorePersistence.delete(jsonSchemaForSchema,
                new SimpleEntry("$id", schemaId));
        if (isDeleted) {
            dataStorePersistence.onDeleteSchema(jsonSchema);
        }
        return isDeleted;
    }

    /**
     * Get the JsonSchema with given String.
     *
     * @param schemaId String to be Search
     * @return searched JsonSchema
     * @throws DataStoreException
     */
    public Map<String, Object> getCompositeSchema(final String schemaId)
            throws DataStoreException {
        JsonSchema jsonSchema = getJsonSchema(schemaId);
        return jsonSchema == null ? null : jsonSchema.getCompositeSchema();
    }

    /**
     * Set the JsonSchema with given String.
     *
     * @param schemaId String to be Set
     * @param jsonData the json data
     * @return searched JsonSchema
     * @throws DataStoreException
     */
    public Set<ConstraintViolation> validate(final String schemaId,
                                             final Map<String, Object> jsonData)
            throws DataStoreException {
        JsonSchema jsonSchema = getJsonSchema(schemaId);
        return jsonSchema == null ? null : jsonSchema.validate(jsonData);
    }

    /**
     * List all JsonSchemas.
     *
     * @param schemaId id of the schema
     * @throws DataStoreException
     * @return list of schema
     */
    public List<Map<String, Object>> listExtenstions(final String schemaId)
            throws DataStoreException {
        List<Map<String, Object>> list = listChildren(schemaId);
        if (list != null && !list.isEmpty()) {
            List<Map<String, Object>> childrenOfChidrens = new ArrayList();
            list.forEach(schema -> {
                try {
                    List<Map<String, Object>> children =
                            listExtenstions(schema.get("$id").toString());
                    if (children != null) {
                        childrenOfChidrens.addAll(children);
                    }

                } catch (DataStoreException ex) {
                    Logger.getLogger(DataStore.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            });
            list.addAll(childrenOfChidrens);
        }
        return list;
    }

    /**
     * List all Extension Types.
     *
     * @param schemaId id of the schema
     * @throws DataStoreException
     * @return list of schema
     */
    public List<String> listExtenstionTypes(final String schemaId)
            throws DataStoreException {
        List<Map<String, Object>> list = listExtenstions(schemaId);

        if (list != null) {
            return list.stream().map(schema -> schema.get("$id").toString())
                    .collect(toList());
        }

        return null;
    }

    /**
     * List all children in a string.
     *
     * @param schemaId id of the schema
     * @throws DataStoreException
     * @return list of schema
     */
    public List<Map<String, Object>> listChildren(final String schemaId)
            throws DataStoreException {
        if (jsonSchemaForSchema.getId().equals(schemaId)) {
            return null;
        }
        return dataStorePersistence.list(jsonSchemaForSchema,
                new MapQuery().string("$ref").eq(schemaId));
    }

    /**
     * List all lists.
     *
     * @param condition of the schema
     * @return list of schema
     * @throws DataStoreException
     */
    public List<Map<String, Object>> list(final Condition<MapQuery> condition)
            throws DataStoreException {
        return dataStorePersistence.list(jsonSchemaForSchema, condition);
    }

    /**
     * List all JsonSchemas lists.
     * @throws DataStoreException
     * @return list of schema
     */
    public List<Map<String, Object>> list() throws DataStoreException {
        return dataStorePersistence.list(jsonSchemaForSchema,
                (Condition<MapQuery>) null);
    }

    /**
     * Get the JsonSchema with given String.
     *
     * @param schemaId String to be Search
     * @return searched JsonSchema
     * @throws DataStoreException
     */
    public JsonSchema getJsonSchema(final String schemaId) {
        return new EveritJsonSchema(schemaId, this::supplySchema);
    }

    /**
     * Get the supply schema with given String.
     *
     * @param schemaId String to be Search
     * @return searched JsonSchema
     * @throws DataStoreException
     */
    private Map<String, Object> supplySchema(final String schemaId) {
        try {
            return get(schemaId);

        } catch (DataStoreException ex) {
            Logger.getLogger(DataStore.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
