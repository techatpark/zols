/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.persistence;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import org.zols.datastore.query.Page;
import org.zols.datastore.query.Query;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;

public class HazelcastDataStorePersistence implements DataStorePersistence {

    private final HazelcastInstance hazelcastInstance;

    public HazelcastDataStorePersistence(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public Map<String, Object> create(JsonSchema jsonSchema, Map<String, Object> jsonData) throws DataStoreException {
        IMap map = getIMap(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(jsonData);
        map.putIfAbsent(ids, jsonData);
        return read(jsonSchema, jsonSchema.getIdKeys(jsonData));
    }

    @Override
    public Map<String, Object> read(JsonSchema jsonSchema, AbstractMap.SimpleEntry<String, Object>... idValues) throws DataStoreException {
        IMap map = getIMap(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);

        return (Map<String, Object>) map.get(ids);
    }

    @Override
    public boolean delete(JsonSchema jsonSchema, AbstractMap.SimpleEntry<String, Object>... idValues) throws DataStoreException {
        IMap map = getIMap(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);
        map.remove(ids);
        return true;
    }

    @Override
    public boolean delete(JsonSchema jsonSchema, Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean update(JsonSchema jsonSchema, Map<String, Object> jsonData, AbstractMap.SimpleEntry<String, Object>... idValues) throws DataStoreException {
        IMap map = getIMap(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);
        map.put(ids, jsonData);
        return true;
    }

    @Override
    public boolean updatePartially(JsonSchema jsonSchema, Map<String, Object> jsonPartialData, AbstractMap.SimpleEntry<String, Object>... idValues) throws DataStoreException {
        IMap map = getIMap(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);
        // Get Existing Data
        Map<String, Object> jsonData = read(jsonSchema, idValues);
        // Fill Partial Data
        jsonData.putAll(jsonPartialData);
        map.put(ids, jsonData);
        return true;
    }

    @Override
    public List<Map<String, Object>> list(JsonSchema jsonSchema, Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Page<Map<String, Object>> list(JsonSchema jsonSchema, Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private IMap<String, Map<String, Object>> getIMap(JsonSchema jsonSchema) {
        return hazelcastInstance.getMap(jsonSchema.getRoot().getId());
    }

}
