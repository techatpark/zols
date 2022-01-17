package org.zols.datastore;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import org.zols.datastore.persistence.BrowsableDataStorePersistence;
import org.zols.datastore.persistence.DataStorePersistence;
import org.zols.datastore.query.AggregatedResults;
import org.zols.datastore.query.MapQuery;
import org.zols.datastore.query.Page;
import org.zols.jsonschema.JsonSchema;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Data Store is used to Store Static and Dynamic Objects using JSON Schema
 *
 * @author Sathish Kumar Thiyagarajan
 */
public class DataStore {

    final DataStorePersistence dataStorePersistence;
    private final SchemaManager schemaManager;

    public DataStore() {
        dataStorePersistence = null;
        schemaManager = new SchemaManager(null);
    }

    public DataStore(final DataStorePersistence dataStorePersistence) {
        this.dataStorePersistence = dataStorePersistence;
        schemaManager = new SchemaManager(dataStorePersistence);
        dataStorePersistence.onIntialize(this);
    }

    public Map<String, Object> create(final String schemaId,
                                      final Map<String, Object> dataMap)
            throws DataStoreException {
        return create(schemaId, dataMap, null);
    }

    public Map<String, Object> create(final String schemaId,
                                      final Map<String, Object> dataMap,
                                      final Locale locale)
            throws DataStoreException {
        Map<String, Object> createdDataAsMap = null;

        if (dataMap != null) {
            JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
            if (jsonSchema != null) {
                Set violations = jsonSchema.validate(dataMap);
                if (violations.isEmpty()) {

                    if (locale == null) {
                        createdDataAsMap = this.create(jsonSchema, dataMap);

                    } else {
                        createdDataAsMap = jsonSchema.delocalizeData(
                                this.create(jsonSchema,
                                        jsonSchema.localizeData(dataMap,
                                                locale, true)), locale);

                    }

                } else {
                    throw new ConstraintViolationException(dataMap,
                            violations);
                }
            }

        }
        return createdDataAsMap;
    }

    public Optional<Map<String, Object>> read(final String schemaId, final Locale locale,
                                              final SimpleEntry... idValues)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return (jsonSchema == null)
                ? Optional.empty() :
                Optional.ofNullable(read(jsonSchema, locale, idValues));

    }

    public Optional<Map<String, Object>> read(final String schemaId,
                                              final SimpleEntry... idValues)
            throws DataStoreException {
        return read(schemaId, null, idValues);
    }

    public Map<String, Object> update(final String schemaId,
                                      final Map<String, Object> dataMap,
                                      final SimpleEntry... idValues)
            throws DataStoreException {
        return update(schemaId, dataMap, null, idValues);
    }

    public Map<String, Object> update(final String schemaId,
                                      final Map<String, Object> dataMap,
                                      final Locale locale, final SimpleEntry... idValues)
            throws DataStoreException {
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
                        updated = updatePartial(jsonSchema,
                                jsonSchema.localizeData(dataMap, locale),
                                idValues);
                    }

                    if (updated) {
                        updatedDataAsMap =
                                read(schemaId, locale, idValues).get();
                    }

                } else {
                    throw new ConstraintViolationException(dataMap,
                            violations);
                }
            }

        }
        return updatedDataAsMap;
    }

    public void updatePartial(final String schemaId, final Map<String, Object> dataMap,
                              final SimpleEntry... idValues)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        if (jsonSchema != null) {
            this.update(jsonSchema, dataMap, idValues);
        }
    }

    public boolean delete(final String schemaId) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema != null && delete(jsonSchema);
    }

    public boolean delete(final String schemaId, final SimpleEntry... idValues)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema != null && delete(jsonSchema, idValues);
    }

    public boolean delete(final String schemaId, final Condition<MapQuery> query)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema != null && delete(jsonSchema, query);
    }

    public List<Map<String, Object>> list(final String schemaId,
                                          final Condition<MapQuery> query,
                                          final Locale locale)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        if (jsonSchema != null) {
            List<Map<String, Object>> maps = list(jsonSchema, query);
            return maps == null ? null : maps.parallelStream().map(dataAsMap
                    -> jsonSchema.delocalizeData(dataAsMap, locale)
            ).collect(toList());
        }

        return null;
    }

    public List<Map<String, Object>> list(final String schemaId)
            throws DataStoreException {

        return list(schemaId, null, (Locale) null);
    }

    public List<Map<String, Object>> list(final String schemaId, final Locale locale)
            throws DataStoreException {
        return list(schemaId, null, locale);
    }

    public List<Map<String, Object>> list(final String schemaId,
                                          final Condition<MapQuery> query)
            throws DataStoreException {
        return list(schemaId, query, null);
    }

    public Page<Map<String, Object>> list(final String schemaId, final Integer pageNumber,
                                          final Integer pageSize)
            throws DataStoreException {
        return list(schemaId, null, null, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(final String schemaId, final Locale locale,
                                          final Integer pageNumber, final Integer pageSize)
            throws DataStoreException {

        return list(schemaId, null, locale, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(final String schemaId,
                                          final Condition<MapQuery> query,
                                          final Integer pageNumber, final Integer pageSize)
            throws DataStoreException {
        return list(schemaId, query, null, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(final String schemaId,
                                          final Condition<MapQuery> query,
                                          final Locale locale, final Integer pageNumber,
                                          final Integer pageSize)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        Page<Map<String, Object>> page =
                list(jsonSchema, query, pageNumber, pageSize);

        if (page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(),
                    page.getTotal(),
                    page.getContent().parallelStream().map(dataAsMap
                            -> jsonSchema.delocalizeData(dataAsMap, locale)
                    ).collect(toList()));
        }
        return null;
    }

    public List<String> getImplementationsOf(final JsonSchema jsonSchema)
            throws DataStoreException {
        return schemaManager.listExtenstionTypes(jsonSchema.getId());
    }

    public SchemaManager getSchemaManager() {
        return schemaManager;
    }

    public <T> ObjectManager<T> getObjectManager(final Class<T> clazz) {
        return new ObjectManager<>(clazz, this);
    }

    Map<String, Object> create(final JsonSchema jsonSchema,
                               final Map<String, Object> dataAsMap)
            throws DataStoreException {
        dataAsMap.put("$type", jsonSchema.getId());
        return dataStorePersistence.create(jsonSchema, dataAsMap);
    }

    Map<String, Object> read(final JsonSchema jsonSchema, final SimpleEntry... idValues)
            throws DataStoreException {
        return dataStorePersistence.read(jsonSchema, idValues);
    }

    Map<String, Object> read(final JsonSchema jsonSchema, final Locale locale,
                             final SimpleEntry... idValues)
            throws DataStoreException {
        return jsonSchema.delocalizeData(
                dataStorePersistence.read(jsonSchema, idValues), locale);
    }

    boolean update(final JsonSchema jsonSchema, final Map<String, Object> dataAsMap,
                   final SimpleEntry... idValues) throws DataStoreException {
        dataAsMap.put("$type", jsonSchema.getId());
        return dataStorePersistence.update(jsonSchema, dataAsMap, idValues);
    }

    boolean delete(final JsonSchema jsonSchema) throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema,
                (Condition<MapQuery>) null);
    }

    boolean delete(final JsonSchema jsonSchema, final Condition<MapQuery> query)
            throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema, query);
    }

    boolean delete(final JsonSchema jsonSchema, final SimpleEntry... idValues)
            throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema, idValues);
    }

    boolean updatePartial(final JsonSchema jsonSchema, final Map<String, Object> dataAsMap,
                          final SimpleEntry... idValues) throws DataStoreException {
        dataAsMap.put("$type", jsonSchema.getId());
        return dataStorePersistence.updatePartially(jsonSchema, dataAsMap,
                idValues);
    }

    List<Map<String, Object>> list(final JsonSchema jsonSchema,
                                   final Condition<MapQuery> query)
            throws DataStoreException {
        List<Map<String, Object>> dataAsMaps =
                dataStorePersistence.list(jsonSchema, query);

        return dataAsMaps;
    }

    Page<Map<String, Object>> list(final JsonSchema jsonSchema,
                                   final Condition<MapQuery> query,
                                   final Integer pageNumber,
                                   final Integer pageSize)
            throws DataStoreException {
        return dataStorePersistence.list(jsonSchema, query, pageNumber,
                pageSize);
    }

    List<Map<String, Object>> list(final JsonSchema jsonSchema)
            throws DataStoreException {
        return list(jsonSchema, null);
    }

    public AggregatedResults browse(final String schemaId, final String keyword,
                                    final Condition<MapQuery> query, final Locale locale,
                                    final Integer pageNumber, final Integer pageSize)
            throws DataStoreException {
        if (dataStorePersistence instanceof BrowsableDataStorePersistence) {
            JsonSchema schema = schemaManager.getJsonSchema(schemaId);
            AggregatedResults aggregatedResults =
                    ((BrowsableDataStorePersistence) dataStorePersistence).browse(
                            schema, keyword, query, pageNumber, pageSize);
            if (aggregatedResults != null) {
                Page<Map<String, Object>> page = aggregatedResults.getPage();
                aggregatedResults.setPage(
                        new Page(page.getPageNumber(), page.getPageSize(),
                                page.getTotal(),
                                page.getContent().parallelStream()
                                        .map(dataAsMap -> schema.delocalizeData(
                                                dataAsMap, locale)
                                        ).collect(toList())));
            }

            return aggregatedResults;
        } else {
            throw new UnsupportedOperationException(
                    "not a BrowsableDataStorePersistence");
        }
    }

}
