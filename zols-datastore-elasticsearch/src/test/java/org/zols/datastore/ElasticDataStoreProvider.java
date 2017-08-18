/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import org.zols.datastore.elasticsearch.ElasticSearchDataStore;
import org.zols.datastore.util.DataStoreProvider;

/**
 *
 * @author sathish
 */
public class ElasticDataStoreProvider implements DataStoreProvider {

    private static ElasticSearchDataStore _dataStore;

    @Override
    public DataStore buildDataStore() {
        if (_dataStore == null) {
            _dataStore = new ElasticSearchDataStore();
        }
        return _dataStore;
    }

}
