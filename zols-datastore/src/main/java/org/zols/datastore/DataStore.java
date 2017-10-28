package org.zols.datastore;

import org.zols.datastore.query.Page;
import java.util.*;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.zols.datastore.persistence.BrowsableDataStorePersistence;
import org.zols.datastore.persistence.DataStorePersistence;
import org.zols.datastore.query.AggregatedResults;
import org.zols.datastore.query.Filter;
import org.zols.datastore.query.Query;
import org.zols.datatore.exception.ConstraintViolationException;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;

/**
 * Data Store is used to Store Static and Dynamic Objects using JSON Schema
 *
 * @author Sathish Kumar Thiyagarajan
 */
public class DataStore {

    private final SchemaManager schemaManager;
    private final DataStorePersistence dataStorePersistence;

    public DataStore() {
        schemaManager = new SchemaManager(this);
        dataStorePersistence = null;
    }

    public DataStore(DataStorePersistence dataStorePersistence) {
        schemaManager = new SchemaManager(this);
        this.dataStorePersistence = dataStorePersistence;
    }

    public Map<String, Object> create(String schemaId, Map<String, Object> dataMap) throws DataStoreException {
        return create(schemaId, dataMap, null);
    }

    public Map<String, Object> create(String schemaId, Map<String, Object> dataMap, Locale locale) throws DataStoreException {
        Map<String, Object> createdDataAsMap = null;

        if (dataMap != null) {
            JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
            if (jsonSchema != null) {
                Set violations = jsonSchema.validate(dataMap);
                if (violations.isEmpty()) {

                    if (locale == null) {
                        createdDataAsMap = this.create(jsonSchema, dataMap);

                    } else {
                        createdDataAsMap = jsonSchema.delocalizeData(this.create(jsonSchema, jsonSchema.localizeData(dataMap, locale, true)), locale);

                    }

                } else {
                    throw new ConstraintViolationException(dataMap, violations);
                }
            }

        }
        return createdDataAsMap;
    }

    public Optional<Map<String, Object>> read(String schemaId, String idValue, Locale locale) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return (jsonSchema == null)
                ? Optional.empty() : Optional.ofNullable(read(jsonSchema, idValue, locale));

    }

    public Optional<Map<String, Object>> read(String schemaId, String idValue) throws DataStoreException {
        return read(schemaId, idValue, null);
    }

    public Map<String, Object> update(String schemaId, String idValue, Map<String, Object> dataMap) throws DataStoreException {
        return update(schemaId, idValue, dataMap, null);
    }

    public Map<String, Object> update(String schemaId, String idValue, Map<String, Object> dataMap, Locale locale) throws DataStoreException {
        Map<String, Object> updatedDataAsMap = null;

        if (dataMap != null) {
            JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
            if (jsonSchema != null) {
                Set violations = jsonSchema.validate(dataMap);
                if (violations.isEmpty()) {

                    boolean updated;
                    if (locale == null) {
                        updated = updatePartial(jsonSchema, dataMap);
                    } else {
                        updated = updatePartial(jsonSchema, jsonSchema.localizeData(dataMap, locale));
                    }

                    if (updated) {
                        updatedDataAsMap = read(schemaId, idValue, locale).get();
                    }

                } else {
                    throw new ConstraintViolationException(dataMap, violations);
                }
            }

        }
        return updatedDataAsMap;
    }

    public void updatePartial(String schemaId, Map<String, Object> dataMap, String idValue) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        if (jsonSchema != null) {
            this.update(jsonSchema, idValue, dataMap);
        }
    }

    public boolean delete(String schemaId) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema == null ? false : delete(jsonSchema);
    }

    public boolean delete(String schemaId, String idValue) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema == null ? false : delete(jsonSchema, idValue);
    }

    public boolean delete(String schemaId, Query query)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema == null ? false : delete(jsonSchema, query);
    }

    public List<Map<String, Object>> list(String schemaId, Query query, Locale locale) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        if (jsonSchema != null) {
            List<Map<String, Object>> maps = list(jsonSchema, query);
            return maps == null ? null : maps.parallelStream().map(dataAsMap
                    -> jsonSchema.delocalizeData(dataAsMap, locale)
            ).collect(toList());
        }

        return null;
    }

    public List<Map<String, Object>> list(String schemaId) throws DataStoreException {

        return list(schemaId, null, (Locale) null);
    }

    public List<Map<String, Object>> list(String schemaId, Locale locale) throws DataStoreException {
        return list(schemaId, null, locale);
    }

    public List<Map<String, Object>> list(String schemaId, Query query) throws DataStoreException {
        return list(schemaId, query, null);
    }

    public Page<Map<String, Object>> list(String schemaId, Integer pageNumber, Integer pageSize) throws DataStoreException {
        return list(schemaId, null, null, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(String schemaId, Locale locale, Integer pageNumber, Integer pageSize) throws DataStoreException {

        return list(schemaId, null, locale, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(String schemaId, Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        return list(schemaId, query, null, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(String schemaId, Query query, Locale locale, Integer pageNumber, Integer pageSize) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        Page<Map<String, Object>> page = list(jsonSchema, query, pageNumber, pageSize);

        if (page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(), page.getTotal(), page.getContent().parallelStream().map(dataAsMap
                    -> jsonSchema.delocalizeData(dataAsMap, locale)
            ).collect(toList()));
        }
        return null;
    }

    private Query getTypeFilteredQuery(JsonSchema jsonSchema) throws DataStoreException {
        return getTypeFilteredQuery(jsonSchema, null);
    }

    private Query getTypeFilteredQuery(JsonSchema jsonSchema, Query query) throws DataStoreException {
        if (query == null) {
            query = new Query();
        }

        List<String> implementations = schemaManager.listExtenstionTypes(jsonSchema.getId());
        if (implementations == null || implementations.isEmpty()) {
            query.addFilter(new Filter("$type", Filter.Operator.EQUALS, jsonSchema.getId()));
        } else {
            implementations.add(jsonSchema.getId());
            query.addFilter(new Filter("$type", Filter.Operator.EXISTS_IN, implementations));
        }
        return query;
    }

    public SchemaManager getSchemaManager() {
        return schemaManager;
    }

    public <T> ObjectManager<T> getObjectManager(Class<T> clazz) {
        return new ObjectManager<>(clazz, this);
    }

    Map<String, Object> create(JsonSchema jsonSchema, Map<String, Object> dataAsMap) throws DataStoreException {
        dataAsMap.put("$type", jsonSchema.getId());
        return dataStorePersistence.create(jsonSchema, dataAsMap);
    }

    Map<String, Object> read(JsonSchema jsonSchema, String idValue) throws DataStoreException {
        return dataStorePersistence.read(jsonSchema, idValue);
    }

    Map<String, Object> read(JsonSchema jsonSchema, String idValue, Locale locale) throws DataStoreException {
        return jsonSchema.delocalizeData(dataStorePersistence.read(jsonSchema, idValue), locale);
    }

    boolean update(JsonSchema jsonSchema, String id, Map<String, Object> dataAsMap) throws DataStoreException {
        dataAsMap.put("$type", jsonSchema.getId());
        return dataStorePersistence.update(jsonSchema, id, dataAsMap);
    }

    boolean delete(JsonSchema jsonSchema) throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema, getTypeFilteredQuery(jsonSchema));
    }

    boolean delete(JsonSchema jsonSchema, Query query) throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema, query);
    }

    boolean delete(JsonSchema jsonSchema, String idValue) throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema, idValue);
    }

    boolean updatePartial(JsonSchema jsonSchema, Map<String, Object> dataAsMap) throws DataStoreException {
        dataAsMap.put("$type", jsonSchema.getId());
        return dataStorePersistence.updatePartially(jsonSchema, dataAsMap);
    }

    List<Map<String, Object>> list(JsonSchema jsonSchema,
            Query query) throws DataStoreException {
        List<Map<String, Object>> dataAsMaps = dataStorePersistence.list(jsonSchema, getTypeFilteredQuery(jsonSchema, query));

        return dataAsMaps;
    }

    Page<Map<String, Object>> list(JsonSchema jsonSchema,
            Query query,
            Integer pageNumber,
            Integer pageSize) throws DataStoreException {
        return dataStorePersistence.list(jsonSchema, getTypeFilteredQuery(jsonSchema, query), pageNumber, pageSize);
    }

    List<Map<String, Object>> list(JsonSchema jsonSchema) throws DataStoreException {
        return list(jsonSchema, null);
    }

    public AggregatedResults browse(String schemaId, String keyword, Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        if (dataStorePersistence instanceof BrowsableDataStorePersistence) {
            JsonSchema schema = schemaManager.getJsonSchema(schemaId);
            return ((BrowsableDataStorePersistence) dataStorePersistence).browse(schema, keyword, getTypeFilteredQuery(schema, query), pageNumber, pageSize);
        } else {
            throw new UnsupportedOperationException("now a BrowsableDataStorePersistence");
        }
    }

}
