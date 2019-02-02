/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.persistence.mongo;

import com.mongodb.client.MongoDatabase;
import java.util.List;
import java.util.Map;
import org.zols.datastore.persistence.DataStorePersistence;
import org.zols.datastore.query.Page;
import org.zols.datastore.query.Query;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;

/**
 *
 * @author sathish
 */
public class MongoDatastore implements DataStorePersistence {

    private final MongoDatabase mongoDatabase;

    public MongoDatastore(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public Map<String, Object> create(JsonSchema jsonSchema, Map<String, Object> jsonData) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> read(JsonSchema jsonSchema, String idValue) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean delete(JsonSchema jsonSchema, String idValue) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean delete(JsonSchema jsonSchema, Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean update(JsonSchema jsonSchema, String idValue, Map<String, Object> jsonData) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updatePartially(JsonSchema jsonSchema, String idValue, Map<String, Object> jsonData) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Map<String, Object>> list(JsonSchema jsonSchema, Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Page<Map<String, Object>> list(JsonSchema jsonSchema, Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drop() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
