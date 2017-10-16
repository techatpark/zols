/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.validation.ConstraintViolation;
import org.zols.datastore.query.Filter;
import static org.zols.datastore.query.Filter.Operator.EQUALS;
import org.zols.datastore.query.Query;
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

    private final DataStore dataStore;

    private final JsonSchema jsonSchemaForSchema;

    public SchemaManager(DataStore dataStore) {
        this.dataStore = dataStore;
        jsonSchemaForSchema = jsonSchemaForSchema();
    }

    public Map<String, Object> create(Map<String, Object> schemaMap)
            throws DataStoreException {
        Set<ConstraintViolation> violations = jsonSchemaForSchema.validate(schemaMap);
        if (violations.isEmpty()) {
            return dataStore.create(jsonSchemaForSchema, schemaMap);
        } else {
            throw new ConstraintViolationException(schemaMap, violations);
        }
    }

    public Map<String, Object> get(String schemaId)
            throws DataStoreException {
        return dataStore.read(jsonSchemaForSchema, schemaId);
    }

    public boolean update(String schemaId,Map<String, Object> schemaMap)
            throws DataStoreException {
        Set<ConstraintViolation> violations = jsonSchemaForSchema.validate(schemaMap);
        if (violations.isEmpty()) {
            return dataStore.update(jsonSchemaForSchema, schemaId,schemaMap);
        } else {
            throw new ConstraintViolationException(schemaMap, violations);
        }
    }

    public Boolean delete(String schemaId) throws DataStoreException {
        return dataStore.delete(jsonSchemaForSchema, schemaId);
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
        if(jsonSchemaForSchema.getId().equals(schemaId)) {
            return null;
        }
        Query query = new Query();
        query.addFilter(new Filter("$ref", EQUALS, schemaId));
        return dataStore.list(jsonSchemaForSchema, query);
    }

    public List<Map<String, Object>> list(Query query) throws DataStoreException {
        return dataStore.list(jsonSchemaForSchema, query);
    }

    public List<Map<String, Object>> list() throws DataStoreException {
        return dataStore.list(jsonSchemaForSchema);
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
