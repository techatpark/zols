/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.service;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import org.zols.datastore.DataStore;
import org.zols.datastore.DataStoreException;
import org.zols.datastore.query.MapQuery;
import org.zols.datastore.query.Page;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class DataService {

    /**
     * The DataStore.
     */
    private final DataStore dataStore;

    /**
     * this is the constructor.
     *
     * @param anDataStore an dataStore
     */
    public DataService(final DataStore anDataStore) {
        this.dataStore = anDataStore;
    }

    /**
     * getId Field.
     *
     * @param schemaId schemaId
     * @return schema list
     */
    public final String getIdField(final String schemaId)
            throws DataStoreException {
        String idPropertyName = null;
        List<String> idProps =
                dataStore.getSchemaManager().getJsonSchema(schemaId)
                        .getIdPropertyNames();
        if (!idProps.isEmpty()) {
            idPropertyName = idProps.get(0);
        }
        return idPropertyName;
    }

    /**
     * inserts data.
     *
     * @param schemaName the schemaName
     * @param jsonData   the jsonData
     * @param loc        the loc
     * @return schema
     */
    public Map<String, Object> create(final String schemaName,
                                      final Map<String, Object> jsonData,
                                      final Locale loc)
            throws DataStoreException {
        return dataStore.create(schemaName, jsonData, loc);
    }

    /**
     * reads from dataStore.
     *
     * @param schemaName the schemaName
     * @param idValues   the idValues
     * @param loc        the loc
     * @return schema
     */
    public Optional<Map<String, Object>> read(final String schemaName,
                                              final Locale loc,
                                              final SimpleEntry... idValues)
            throws DataStoreException {
        return dataStore.read(schemaName, loc, idValues);
    }

    /**
     * updates the schema.
     *
     * @param schemaId the schemaId
     * @param idValues the idValues
     * @param jsonData the jsonData
     * @param loc      the loc
     * @return schema
     */
    public Map<String, Object> update(final String schemaId,
                                      final Map<String, Object> jsonData,
                                      final Locale loc,
                                      final SimpleEntry... idValues)
            throws DataStoreException {
        return dataStore.update(schemaId, jsonData, loc, idValues);
    }

    /**
     * updates the schema.
     *
     * @param schemaName the schemaName
     * @param idValues   the idValues
     * @return data
     */
    public boolean delete(final String schemaName,
                          final SimpleEntry... idValues)
            throws DataStoreException {
        return dataStore.delete(schemaName, idValues);
    }

    /**
     * updates the schema.
     *
     * @param schemaName the schemaName
     * @param loc        the loc
     * @return schema
     */
    public List<Map<String, Object>> list(final String schemaName,
                                          final Locale loc)
            throws DataStoreException {
        return dataStore.list(schemaName, loc);
    }

    /**
     * updates the schema.
     *
     * @param schemaName the schemaName
     * @param pageSize   the pageSize
     * @param query      the query
     * @param pageNumber the pageNumber
     * @param loc        the loc
     * @return schema
     */
    public Page<Map<String, Object>> list(final String schemaName,
                                          final Condition<MapQuery> query,
                                          final Integer pageNumber,
                                          final Integer pageSize,
                                          final Locale loc)
            throws DataStoreException {

        return dataStore.list(schemaName, query, pageNumber, pageSize);
    }

    /**
     * updates the schema.
     *
     * @param schemaName  the schemaName
     * @param pageSize    the pageSize
     * @param queryString the queryString
     * @param pageNumber  the pageNumber
     * @param loc         the loc
     * @return schema
     */
    public Page<Map<String, Object>> list(final String schemaName,
                                          final String queryString,
                                          final Integer pageNumber,
                                          final Integer pageSize,
                                          final Locale loc)
            throws DataStoreException {
        Condition<MapQuery> query = null;
//        if (queryString != null) {
//            query = new Query();
//            query.addFilter(new Filter(Filter.Operator.FULL_TEXT_SEARCH,
//            queryString + "*"));
//        }
        return dataStore.list(schemaName, query, loc, pageNumber, pageSize);

    }

}
