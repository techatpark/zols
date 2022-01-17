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
 * Data Store is used to Store Static and Dynamic Objects using JSON Schema.
 *
 * @author Sathish Kumar Thiyagarajan
 */
public class DataStore {

    /**
     * dataStorePersistence.
     */
    private final DataStorePersistence dataStorePersistence;
    /**
     * schemaManager.
     */
    private final SchemaManager schemaManager;

    /**
     * DataStore.
     */
    public DataStore() {
        dataStorePersistence = null;
        schemaManager = new SchemaManager(null);
    }

    /**
     * this is the constructor.
     * @param anDataStorePersistence dataStorePersistence
     */
    public DataStore(final DataStorePersistence anDataStorePersistence) {
        this.dataStorePersistence = anDataStorePersistence;
        schemaManager = new SchemaManager(anDataStorePersistence);
        anDataStorePersistence.onIntialize(this);
    }

    /**
     * creates the schema.
     * @param schemaId    the schemaId
     * @param dataMap  the dataMap
     * @return data
     */
    public Map<String, Object> create(final String schemaId,
                                      final Map<String, Object> dataMap)
            throws DataStoreException {
        return create(schemaId, dataMap, null);
    }

    /**
     * creates the schema.
     * @param schemaId    the schemaId
     * @param dataMap  the dataMap
     * @param locale the locale
     * @return data
     */
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

    /**
     * reads the schema.
     * @param schemaId    the schemaId
     * @param locale the locale
     * @param idValues the idValues
     * @return data
     */
    public Optional<Map<String, Object>> read(final String schemaId,
                                              final Locale locale,
                                              final SimpleEntry... idValues)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return (jsonSchema == null)
                ? Optional.empty()
                : Optional.ofNullable(read(jsonSchema, locale, idValues));

    }

    /**
     * reads the schema.
     * @param schemaId    the schemaId
     * @param idValues   the idValues
     * @return data
     */
    public Optional<Map<String, Object>> read(final String schemaId,
                                              final SimpleEntry... idValues)
            throws DataStoreException {
        return read(schemaId, null, idValues);
    }

    /**
     * updates the schema.
     * @param schemaId    the schemaId
     * @param dataMap  the dataMap
     * @param idValues the idValues
     * @return data
     */
    public Map<String, Object> update(final String schemaId,
                                      final Map<String, Object> dataMap,
                                      final SimpleEntry... idValues)
            throws DataStoreException {
        return update(schemaId, dataMap, null, idValues);
    }

    /**
     * updates the schema.
     * @param schemaId    the schemaId
     * @param dataMap  the dataMap
     * @param locale the locale
     * @param idValues the idValues
     * @return data
     */
    public Map<String, Object> update(final String schemaId,
                                      final Map<String, Object> dataMap,
                                      final Locale locale,
                                      final SimpleEntry... idValues)
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

    /**
     * updates the schema.
     * @param schemaId    the schemaId
     * @param dataMap  the dataMap
     * @param idValues the idValues
     */
    public void updatePartial(final String schemaId,
                              final Map<String, Object> dataMap,
                              final SimpleEntry... idValues)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        if (jsonSchema != null) {
            this.update(jsonSchema, dataMap, idValues);
        }
    }

    /**
     * deletes the schema.
     * @param schemaId    the schemaId
     * @return data
     */
    public boolean delete(final String schemaId) throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema != null && delete(jsonSchema);
    }

    /**
     * delete the schema.
     * @param schemaId    the schemaId
     * @param idValues the idValues
     * @return data
     */
    public boolean delete(final String schemaId, final SimpleEntry... idValues)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema != null && delete(jsonSchema, idValues);
    }

    /**
     * delete the schema.
     * @param schemaId    the schemaId
     * @param query the query
     * @return data
     */
    public boolean delete(final String schemaId,
                          final Condition<MapQuery> query)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return jsonSchema != null && delete(jsonSchema, query);
    }

    /**
     * list the schema.
     * @param schemaId    the schemaId
     * @param query the query
     * @param locale the locale
     * @return data
     */
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

    /**
     * list the schema with id.
     * @param schemaId    the schemaId
     * @return data
     */
    public List<Map<String, Object>> list(final String schemaId)
            throws DataStoreException {

        return list(schemaId, null, (Locale) null);
    }

    /**
     * list the schema.
     * @param schemaId    the schemaId
     * @param locale the locale
     * @return data
     */
    public List<Map<String, Object>> list(final String schemaId,
                                          final Locale locale)
            throws DataStoreException {
        return list(schemaId, null, locale);
    }

    /**
     * list the schema.
     * @param schemaId    the schemaId
     * @param query  the query
     * @return data
     */
    public List<Map<String, Object>> list(final String schemaId,
                                          final Condition<MapQuery> query)
            throws DataStoreException {
        return list(schemaId, query, null);
    }

    /**
     * list of page in the schema.
     * @param schemaId    the schemaId
     * @param pageNumber  the pageNumber
     * @param pageSize the pageSize
     * @return data
     */
    public Page<Map<String, Object>> list(final String schemaId,
                                          final Integer pageNumber,
                                          final Integer pageSize)
            throws DataStoreException {
        return list(schemaId, null, null, pageNumber, pageSize);
    }

    /**
     * list of page in the schema.
     * @param schemaId    the schemaId
     * @param pageNumber  the pageNumber
     * @param pageSize the pageSize
     * @param locale the locale
     * @return data
     */
    public Page<Map<String, Object>> list(final String schemaId,
                                          final Locale locale,
                                          final Integer pageNumber,
                                          final Integer pageSize)
            throws DataStoreException {

        return list(schemaId, null, locale, pageNumber, pageSize);
    }

    /**
     * list the schema.
     * @param schemaId    the schemaId
     * @param query the query
     * @param pageNumber  the pageNumber
     * @param pageSize the pageSize
     * @return data
     */
    public Page<Map<String, Object>> list(final String schemaId,
                                          final Condition<MapQuery> query,
                                          final Integer pageNumber,
                                          final Integer pageSize)
            throws DataStoreException {
        return list(schemaId, query, null, pageNumber, pageSize);
    }

    /**
     * list the schema.
     * @param schemaId    the schemaId
     * @param query the query
     * @param pageNumber  the pageNumber
     * @param locale the locale
     * @param pageSize the pageSize
     * @return data
     */
    public Page<Map<String, Object>> list(final String schemaId,
                                          final Condition<MapQuery> query,
                                          final Locale locale,
                                          final Integer pageNumber,
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

    /**
     * Implements the schema.
     * @param jsonSchema    the jsonSchema
     * @return data
     */
    public List<String> getImplementationsOf(final JsonSchema jsonSchema)
            throws DataStoreException {
        return schemaManager.listExtenstionTypes(jsonSchema.getId());
    }

    /**
     * gets the schema Manager.
     * @return schemaManager
     */
    public SchemaManager getSchemaManager() {
        return schemaManager;
    }

    /**
     * gets the ObjectManager.
     * @param <T>
     * @param clazz the clazz
     * @return ObjectManager
     */
    public <T> ObjectManager<T> getObjectManager(final Class<T> clazz) {
        return new ObjectManager<>(clazz, this);
    }

    /**
     * reads the schema.
     * @param jsonSchema    the jsonSchema
     * @param dataAsMap the dataAsMap
     * @return data
     */
    Map<String, Object> create(final JsonSchema jsonSchema,
                               final Map<String, Object> dataAsMap)
            throws DataStoreException {
        dataAsMap.put("$type", jsonSchema.getId());
        return dataStorePersistence.create(jsonSchema, dataAsMap);
    }

    /**
     * reads the schema.
     * @param jsonSchema    the jsonSchema
     * @param idValues  the idValues
     * @return data
     */
    Map<String, Object> read(final JsonSchema jsonSchema,
                                 final SimpleEntry... idValues)
            throws DataStoreException {
        return dataStorePersistence.read(jsonSchema, idValues);
    }

    /**
     * reads the schema.
     * @param jsonSchema    the jsonSchema
     * @param locale the locale
     * @param idValues  the idValues
     * @return data
     */
    Map<String, Object> read(final JsonSchema jsonSchema, final Locale locale,
                             final SimpleEntry... idValues)
            throws DataStoreException {
        return jsonSchema.delocalizeData(
                dataStorePersistence.read(jsonSchema, idValues), locale);
    }

    /**
     * updates the schema.
     * @param jsonSchema    the jsonSchema
     * @param dataAsMap the dataAsMap
     * @param idValues  the idValues
     * @return data
     */
    boolean update(final JsonSchema jsonSchema,
                   final Map<String, Object> dataAsMap,
                   final SimpleEntry... idValues) throws DataStoreException {
        dataAsMap.put("$type", jsonSchema.getId());
        return dataStorePersistence.update(jsonSchema, dataAsMap, idValues);
    }

    /**
     * deletes the schema.
     * @param jsonSchema    the jsonSchema
     * @return data
     */
    boolean delete(final JsonSchema jsonSchema) throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema,
                (Condition<MapQuery>) null);
    }

    /**
     * deletes the schema.
     * @param jsonSchema    the jsonSchema
     * @param query the query
     * @return data
     */
    boolean delete(final JsonSchema jsonSchema, final Condition<MapQuery> query)
            throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema, query);
    }

    /**
     * deletes the schema.
     * @param jsonSchema    the jsonSchema
     * @param idValues  the idValues
     * @return data
     */
    boolean delete(final JsonSchema jsonSchema, final SimpleEntry... idValues)
            throws DataStoreException {
        return dataStorePersistence.delete(jsonSchema, idValues);
    }

    /**
     * updates the schema.
     * @param jsonSchema    the jsonSchema
     * @param dataAsMap the dataAsMap
     * @param idValues  the idValues
     * @return data
     */
    boolean updatePartial(final JsonSchema jsonSchema,
                          final Map<String, Object> dataAsMap,
                          final SimpleEntry... idValues)
                                       throws DataStoreException {
        dataAsMap.put("$type", jsonSchema.getId());
        return dataStorePersistence.updatePartially(jsonSchema, dataAsMap,
                idValues);
    }

    /**
     * list the schema.
     * @param jsonSchema    the jsonSchema
     * @param query the query
     * @return data
     */
    List<Map<String, Object>> list(final JsonSchema jsonSchema,
                                   final Condition<MapQuery> query)
            throws DataStoreException {
        List<Map<String, Object>> dataAsMaps =
                dataStorePersistence.list(jsonSchema, query);

        return dataAsMaps;
    }

    /**
     * list the schema.
     * @param jsonSchema    the jsonSchema
     * @param query the query
     * @param pageNumber  the pageNumber
     * @param pageSize the pageSize
     * @return data
     */
    Page<Map<String, Object>> list(final JsonSchema jsonSchema,
                                   final Condition<MapQuery> query,
                                   final Integer pageNumber,
                                   final Integer pageSize)
            throws DataStoreException {
        return dataStorePersistence.list(jsonSchema, query, pageNumber,
                pageSize);
    }

    /**
     * list the schema.
     * @param jsonSchema    the jsonSchema
     * @return data
     */
    List<Map<String, Object>> list(final JsonSchema jsonSchema)
            throws DataStoreException {
        return list(jsonSchema, null);
    }

    /**
     * browsw the schema.
     * @param schemaId    the schemaId
     * @param locale the locale
     * @param query the query
     * @param pageNumber  the pageNumber
     * @param pageSize the pageSize
     * @param keyword the keyword
     * @return data
     */
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
