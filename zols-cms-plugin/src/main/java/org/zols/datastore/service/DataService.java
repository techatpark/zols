/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.service;

import java.util.List;
import java.util.Map;
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.zols.datastore.jsonschema.JSONSchema;
import org.zols.datatore.exception.DataStoreException;

/**
 *
 * @author sathish_ku
 */
@Service
public class DataService {

    @Autowired
    private DataStore dataStore;

    public String getIdField(String schemaName) throws DataStoreException {
        return JSONSchema.jsonSchema(dataStore.getSchema(schemaName)).idField();
    }

    @Secured("ROLE_ADMIN")
    public Map<String, Object> create(String schemaName, Map<String, Object> jsonData) throws DataStoreException {
        return dataStore.create(schemaName, jsonData);
    }

    public Map<String, Object> read(String schemaName, String name) throws DataStoreException {
        return dataStore.read(schemaName, name);
    }

    @Secured("ROLE_ADMIN")
    public boolean update(String schemaName, Map<String, Object> jsonData) throws DataStoreException {
        return dataStore.update(schemaName, jsonData);
    }

    @Secured("ROLE_ADMIN")
    public boolean delete(String schemaName, String name) throws DataStoreException {
        return dataStore.delete(schemaName, name);
    }

    public List<Map<String, Object>> list(String schemaName) throws DataStoreException {
        return dataStore.list(schemaName);
    }

}
