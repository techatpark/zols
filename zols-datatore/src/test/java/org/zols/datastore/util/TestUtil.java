/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.util;

import org.zols.datastore.elasticsearch.ElasticSearchDataStore;

/**
 *
 * @author sathish
 */
public class TestUtil {
    
    private static ElasticSearchDataStore _dataStore;

    public static ElasticSearchDataStore testDataStore() {
        if(_dataStore == null) {
            _dataStore = new ElasticSearchDataStore();
        }
        return _dataStore;
    }
    
    
}
