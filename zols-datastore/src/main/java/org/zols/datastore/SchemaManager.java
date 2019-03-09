/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.validation.ConstraintViolation;
import org.zols.datastore.persistence.DataStorePersistence;
import org.zols.datastore.query.MapQuery;
import org.zols.datastore.query.Page;
import org.zols.datatore.exception.ConstraintViolationException;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;
import org.zols.jsonschema.everit.EveritJsonSchema;
import static org.zols.jsonschema.util.JsonSchemaUtil.jsonSchemaForSchema;

/**
 *
 * @author sathish
 */
public final class SchemaManager {

    private final DataStorePersistence dataStorePersistence;

    private final JsonSchema jsonSchemaForSchema;

    public SchemaManager(DataStorePersistence dataStorePersistence) {
        this.dataStorePersistence = dataStorePersistence;
        jsonSchemaForSchema = jsonSchemaForSchema();
    }

    public Map<String, Object> create(Map<String, Object> schemaMap)
            throws DataStoreException {
        Set<ConstraintViolation> violations = jsonSchemaForSchema.validate(schemaMap);
        if (violations.isEmpty()) {
            Map<String, Object> schemaMapCreated = dataStorePersistence.create(jsonSchemaForSchema, schemaMap);
            dataStorePersistence.onNewSchema(getJsonSchema(schemaMap.get("$id").toString()));
            return schemaMapCreated;
        } else {
            throw new ConstraintViolationException(schemaMap, violations);
        }
    }

    public Map<String, Object> get(String schemaId)
            throws DataStoreException {
        return dataStorePersistence.read(jsonSchemaForSchema, new SimpleEntry("$id", schemaId));
    }

    public boolean update(String schemaId, Map<String, Object> schemaMap)
            throws DataStoreException {
        Set<ConstraintViolation> violations = jsonSchemaForSchema.validate(schemaMap);
        if (violations.isEmpty()) {
            JsonSchema oldSchemaMap = this.getJsonSchema(schemaId);
            boolean updated = dataStorePersistence.update(jsonSchemaForSchema, schemaMap, new SimpleEntry("$id", schemaId));
            if (updated) {
                dataStorePersistence.onUpdateSchema(oldSchemaMap, this.getJsonSchema(schemaId));
            }
            return updated;
        } else {
            throw new ConstraintViolationException(schemaMap, violations);
        }
    }

    public Boolean delete(String schemaId) throws DataStoreException {
        boolean isDeleted;
        JsonSchema jsonSchema = getJsonSchema(schemaId);
        isDeleted = dataStorePersistence.delete(jsonSchemaForSchema, new SimpleEntry("$id", schemaId));
        if (isDeleted) {
            dataStorePersistence.onDeleteSchema(jsonSchema);
        }
        return isDeleted;
    }

    public Map<String, Object> getCompositeSchema(String schemaId)
            throws DataStoreException {
        JsonSchema jsonSchema = getJsonSchema(schemaId);
        return jsonSchema == null ? null : jsonSchema.getCompositeSchema();
    }

    public Set<ConstraintViolation> validate(String schemaId,
            Map<String, Object> jsonData)
            throws DataStoreException {
        JsonSchema jsonSchema = getJsonSchema(schemaId);
        return jsonSchema == null ? null : jsonSchema.validate(jsonData);
    }

    public List<Map<String, Object>> listExtenstions(String schemaId) throws DataStoreException {
        List<Map<String, Object>> list = listChildren(schemaId);
        if (list != null && !list.isEmpty()) {
            List<Map<String, Object>> childrenOfChidrens = new ArrayList();
            list.forEach(schema -> {
                try {
                    List<Map<String, Object>> children = listExtenstions(schema.get("$id").toString());
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

    public List<String> listExtenstionTypes(String schemaId) throws DataStoreException {
        List<Map<String, Object>> list = listExtenstions(schemaId);

        if (list != null) {
            return list.stream().map(schema -> schema.get("$id").toString()).collect(toList());
        }

        return null;
    }

    public List<Map<String, Object>> listChildren(String schemaId) throws DataStoreException {
        if (jsonSchemaForSchema.getId().equals(schemaId)) {
            return null;
        }
        return dataStorePersistence.list(jsonSchemaForSchema, new MapQuery().string("$ref").eq(schemaId));
    }

    public List<Map<String, Object>> list(Condition<MapQuery> condition) throws DataStoreException {
        return dataStorePersistence.list(jsonSchemaForSchema, condition);
    }

    public List<Map<String, Object>> list() throws DataStoreException {
        return dataStorePersistence.list(jsonSchemaForSchema, null);
    }

    public JsonSchema getJsonSchema(String schemaId) {
        return new EveritJsonSchema(schemaId, this::supplySchema);
    }

    private Map<String, Object> supplySchema(String schemaId) {
        try {
            return get(schemaId);

        } catch (DataStoreException ex) {
            Logger.getLogger(DataStore.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}