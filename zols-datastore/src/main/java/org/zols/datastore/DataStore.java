package org.zols.datastore;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import org.zols.datastore.query.Page;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.zols.datastore.persistence.BrowsableDataStorePersistence;
import org.zols.datastore.persistence.DataStorePersistence;
import org.zols.datastore.query.AggregatedResults;
import org.zols.datastore.query.MapQuery;
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
    final DataStorePersistence dataStorePersistence;

    public DataStore() {
        dataStorePersistence = null;
        schemaManager = new SchemaManager(null);
    }

    public DataStore(DataStorePersistence dataStorePersistence) {
        this.dataStorePersistence = dataStorePersistence;
        schemaManager = new SchemaManager(dataStorePersistence);
        dataStorePersistence.onIntialize(this);
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

    public Optional<Map<String, Object>> read(String schemaId, Locale locale, SimpleEntry... idValues) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return (jsonSchema == null)
                ? Optional.empty() : Optional.ofNullable(read(jsonSchema, locale, idValues));

    }

    public Optional<Map<String, Object>> read(String schemaId, SimpleEntry... idValues) throws DataStoreException {
        return read(schemaId, null, idValues);
    }

    public Map<String, Object> update(String schemaId, Map<String, Object> dataMap, SimpleEntry... idValues) throws DataStoreException {
        return update(schemaId, dataMap, null, idValues);
    }

    public Map<String, Object> update(String schemaId, Map<String, Object> dataMap, Locale locale, SimpleEntry... idValues) throws DataStoreException {
        Map<String, Object> updatedDataAsMap = null;

        if (dataMap != null) {
            JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
            if (jsonSchema != null) {
                Set violations = jsonSchema.validate(dataMap);
                if (violations.isEmpty()) {

                    boolean updated;
                    if (locale == null) {
                        updated = updatePartial(jsonSchema, dataMap, idValues);
                    } else {
                        updated = updatePartial(jsonSchema, jsonSchema.localizeData(dataMap, locale), idValues);
                    }

                    if (updated) {
                        updatedDataAsMap = read(schemaId, locale, idValues).get();
                    }

                } else {
                    throw new ConstraintViolationException(dataMap, violations);
                }
            }

        }
        return updatedDataAsMap;
    }

    public void updatePartial(String schemaId, Map<String, Object> dataMap, SimpleEntry... idValues) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        if (jsonSchema != null) {
            this.update(jsonSchema, dataMap, idValues);
        }
    }

    public boolean delete(String schemaId) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema == null ? false : delete(jsonSchema);
    }

    public boolean delete(String schemaId, SimpleEntry... idValues) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema == null ? false : delete(jsonSchema, idValues);
    }

    public boolean delete(String schemaId, Condition<MapQuery> query)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema == null ? false : delete(jsonSchema, query);
    }

    public List<Map<String, Object>> list(String schemaId, Condition<MapQuery> query, Locale locale) throws DataStoreException {
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

    public List<Map<String, Object>> list(String schemaId, Condition<MapQuery> query) throws DataStoreException {
        return list(schemaId, query, null);
    }

    public Page<Map<String, Object>> list(String schemaId, Integer pageNumber, Integer pageSize) throws DataStoreException {
        return list(schemaId, null, null, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(String schemaId, Locale locale, Integer pageNumber, Integer pageSize) throws DataStoreException {

        return list(schemaId, null, locale, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(String schemaId, Condition<MapQuery> query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        return list(schemaId, query, null, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(String schemaId, Condition<MapQuery> query, Locale locale, Integer pageNumber, Integer pageSize) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        Page<Map<String, Object>> page = list(jsonSchema, query, pageNumber, pageSize);

        if (page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(), page.getTotal(), page.getContent().parallelStream().map(dataAsMap
                    -> jsonSchema.delocalizeData(dataAsMap, locale)
            ).collect(toList()));
        }
        return null;
    }

    public List<String> getImplementationsOf(JsonSchema jsonSchema) throws DataStoreException{
        return schemaManager.listExtenstionTypes(jsonSchema.getId());
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

    Map<String, Object> read(JsonSchema jsonSchema, SimpleEntry... idValues) throws DataStoreException {
        return dataStorePersistence.read(jsonSchema, idValues);
    }

    Map<String, Object> read(JsonSchema jsonSchema, Locale locale, SimpleEntry... idValues) throws DataStoreException {
        return jsonSchema.delocalizeData(dataStorePersistence.read(jsonSchema, idValues), locale);
    }

    boolean update(JsonSchema jsonSchema, Map<String, Object> dataAsMap, SimpleEntry... idValues) throws DataStoreException {
        dataAsMap.put("$type", jsonSchema.getId());
        return dataStorePersistence.update(jsonSchema, dataAsMap, idValues);
    }

    boolean delete(JsonSchema jsonSchema) throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema, (Condition<MapQuery>) null);
    }

    boolean delete(JsonSchema jsonSchema, Condition<MapQuery> query) throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema, query);
    }

    boolean delete(JsonSchema jsonSchema, SimpleEntry... idValues) throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema, idValues);
    }

    boolean updatePartial(JsonSchema jsonSchema, Map<String, Object> dataAsMap, SimpleEntry... idValues) throws DataStoreException {
        dataAsMap.put("$type", jsonSchema.getId());
        return dataStorePersistence.updatePartially(jsonSchema, dataAsMap, idValues);
    }

    List<Map<String, Object>> list(JsonSchema jsonSchema,
            Condition<MapQuery> query) throws DataStoreException {
        List<Map<String, Object>> dataAsMaps = dataStorePersistence.list(jsonSchema,  query);

        return dataAsMaps;
    }

    Page<Map<String, Object>> list(JsonSchema jsonSchema,
            Condition<MapQuery> query,
            Integer pageNumber,
            Integer pageSize) throws DataStoreException {
        return dataStorePersistence.list(jsonSchema,  query, pageNumber, pageSize);
    }

    List<Map<String, Object>> list(JsonSchema jsonSchema) throws DataStoreException {
        return list(jsonSchema, null);
    }

    public AggregatedResults browse(String schemaId, String keyword, Condition<MapQuery> query, Locale locale, Integer pageNumber, Integer pageSize) throws DataStoreException {
        if (dataStorePersistence instanceof BrowsableDataStorePersistence) {
            JsonSchema schema = schemaManager.getJsonSchema(schemaId);
            AggregatedResults aggregatedResults = ((BrowsableDataStorePersistence) dataStorePersistence).browse(schema, keyword,  query, pageNumber, pageSize);
            if (aggregatedResults != null) {
                Page<Map<String, Object>> page = aggregatedResults.getPage();
                aggregatedResults.setPage(new Page(page.getPageNumber(), page.getPageSize(), page.getTotal(), page.getContent().parallelStream().map(dataAsMap -> schema.delocalizeData(dataAsMap, locale)
                ).collect(toList())));
            }

            return aggregatedResults;
        } else {
            throw new UnsupportedOperationException("not a BrowsableDataStorePersistence");
        }
    }

}
