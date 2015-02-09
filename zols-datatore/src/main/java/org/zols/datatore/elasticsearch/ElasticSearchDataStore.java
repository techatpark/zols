/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datatore.elasticsearch;

import java.util.List;
import java.util.Map;
import org.zols.datastore.query.Query;
import org.zols.datatore.DataStore;
import org.zols.datatore.exception.DataStoreException;

/**
 *
 * Elastic Search Implementation of DataStore
 * @author Raji
 */
public class ElasticSearchDataStore extends DataStore {

    @Override
    protected Map<String, Object> createData(String jsonSchema, Map<String, Object> validatedDataObject) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Map<String, Object> readData(String jsonSchema, String idValue) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean deleteData(String jsonSchema, String idValue) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean deleteData(String jsonSchema, Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean updateData(String jsonSchema, Map<String, Object> validatedDataObject) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<Map<String, Object>> listData(String jsonSchema) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<Map<String, Object>> listData(String jsonSchema, Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Get Elastic Search Index Name
     * @return 
     */
    private String indexName() {
        return "zols";
    }
    
    /**
     * Get Elastic Search Type Name
     * @param jsonSchema
     * @return 
     */
    private String typeName(String jsonSchema) {
        return getId(jsonSchema);
    }
    
}
