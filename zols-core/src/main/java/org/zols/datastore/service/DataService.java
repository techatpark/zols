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

    private final DataStore dataStore;

    public DataService(final DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public String getIdField(final String schemaId) throws DataStoreException {
        String idPropertyName = null;
        List<String> idProps =
                dataStore.getSchemaManager().getJsonSchema(schemaId)
                        .getIdPropertyNames();
        if (!idProps.isEmpty()) {
            idPropertyName = idProps.get(0);
        }
        return idPropertyName;
    }

    public Map<String, Object> create(final String schemaName,
                                      final Map<String, Object> jsonData, final Locale loc)
            throws DataStoreException {
        return dataStore.create(schemaName, jsonData, loc);
    }

    public Optional<Map<String, Object>> read(final String schemaName, final Locale loc,
                                              final SimpleEntry... idValues)
            throws DataStoreException {
        return dataStore.read(schemaName, loc, idValues);
    }

    public Map<String, Object> update(final String schemaId,
                                      final Map<String, Object> jsonData, final Locale loc,
                                      final SimpleEntry... idValues)
            throws DataStoreException {
        return dataStore.update(schemaId, jsonData, loc, idValues);
    }

    public boolean delete(final String schemaName, final SimpleEntry... idValues)
            throws DataStoreException {
        return dataStore.delete(schemaName, idValues);
    }

    public List<Map<String, Object>> list(final String schemaName, final Locale loc)
            throws DataStoreException {
        return dataStore.list(schemaName, loc);
    }

    public Page<Map<String, Object>> list(final String schemaName,
                                          final Condition<MapQuery> query,
                                          final Integer pageNumber, final Integer pageSize,
                                          final Locale loc)
            throws DataStoreException {

        return dataStore.list(schemaName, query, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(final String schemaName,
                                          final String queryString,
                                          final Integer pageNumber, final Integer pageSize,
                                          final Locale loc)
            throws DataStoreException {
        Condition<MapQuery> query = null;
//        if (queryString != null) {
//            query = new Query();
//            query.addFilter(new Filter(Filter.Operator.FULL_TEXT_SEARCH, queryString + "*"));
//        }
        return dataStore.list(schemaName, query, loc, pageNumber, pageSize);

    }

}
