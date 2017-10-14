/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documents.service;


import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zols.datastore.DataStore;
import org.zols.datastore.query.Query;
import org.zols.datastore.service.DataService;
import org.zols.datastore.web.util.AggregatedResults;
import org.zols.datastore.web.util.ElasticSearchUtil;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;

@Service
public class BrowseService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BrowseService.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private DataStore dataStore;

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    public Page<Map<String, Object>> searchSchema(String schemaId,
            String keyword,
            Query query,
            Pageable pageable) throws DataStoreException {
        return dataService.list(schemaId, query, pageable,null);
    }

    public AggregatedResults browseSchema(String schemaId,
            String keyword,
            Query query,
            Pageable pageable) throws DataStoreException {
        AggregatedResults aggregatedResults = null;
        JsonSchema schema = dataStore.getSchemaManager().getJsonSchema(schemaId);
        if (schema != null) {
            Map<String, Object> browseQuery = new HashMap<>();
            browseQuery.put("keyword", keyword);
            aggregatedResults = elasticSearchUtil.aggregatedSearch(schema.getJSONPropertyName(schema.getRoot().getId()),
                    (keyword == null) ? "browse_schema" : "browse_schema_with_keyword",
                    browseQuery, pageable, dataStore.getTypeFilteredQuery(schema, query));
        }
        return aggregatedResults;
    }

}
