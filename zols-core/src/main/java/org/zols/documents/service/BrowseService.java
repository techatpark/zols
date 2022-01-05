/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documents.service;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zols.datastore.DataStore;
import org.zols.datastore.query.AggregatedResults;
import org.zols.datastore.query.MapQuery;
import org.zols.datastore.query.Page;
import org.zols.datastore.service.DataService;
import org.zols.datastore.DataStoreException;


public class BrowseService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BrowseService.class);

    private final DataService dataService;

    private final DataStore dataStore;

    public BrowseService(DataService dataService, DataStore dataStore) {
        this.dataService = dataService;
        this.dataStore = dataStore;
    }

    public Page<Map<String, Object>> searchSchema(String schemaId,
            String keyword,
            Condition<MapQuery> query,
            Integer pageNumber, Integer pageSize) throws DataStoreException {
        return dataService.list(schemaId, query, pageNumber,pageSize, null);
    }

    public AggregatedResults browseSchema(String schemaId,
            String keyword,
            Condition<MapQuery> query, Locale locale,
            Integer pageNumber, Integer pageSize) throws DataStoreException {
        return dataStore.browse(schemaId, keyword, query, locale, pageNumber, pageSize);
    }

}
