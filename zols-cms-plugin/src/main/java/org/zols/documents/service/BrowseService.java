/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documents.service;

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
import org.zols.datatore.exception.DataStoreException;

@Service
public class BrowseService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BrowseService.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private DataStore dataStore;

   
    public Page<Map<String, Object>> searchSchema(String schemaId,
            String keyword,
            Query query,
            Pageable pageable) throws DataStoreException {
        return dataService.list(schemaId, query, pageable, null);
    }

    public SpringAggregatedResults browseSchema(String schemaId,
            String keyword,
            Query query,
            Pageable pageable) throws DataStoreException {
        return new SpringAggregatedResults(dataStore.browse(schemaId, keyword, query, pageable.getPageNumber(), pageable.getPageSize()),pageable);
    }
    
    

}

