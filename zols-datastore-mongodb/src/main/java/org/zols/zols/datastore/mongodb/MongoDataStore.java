/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.zols.datastore.mongodb;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.types.ObjectId;
import org.zols.datastore.DataStore;
import org.zols.datastore.query.Filter;
import org.zols.datastore.query.Query;
import static org.zols.datastore.query.Filter.Operator.*;

/**
 * Data Store implementation using MongoDB
 * @author sathish_ku
 */
public class MongoDataStore extends DataStore {

    private static final String ID_FIELD = "_id";

    private DB db;

    /**
     * intialize with default MongoDB configuration
     */
    public MongoDataStore() {
        try {
            MongoClient mongoClient = new MongoClient();
            db = mongoClient.getDB("zols");
        } catch (UnknownHostException ex) {
            Logger.getLogger(MongoDataStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected Map<String, Object> createData(JsonSchema jsonSchema, Map<String, Object> dataObject) {
        String idField = getIdField(jsonSchema);
        Object idFieldValue = dataObject.remove(idField);
        if (idFieldValue != null) {
            dataObject.put(ID_FIELD, idFieldValue.toString());
        }
        DBObject dBObject = new BasicDBObject(dataObject);
        db.getCollection(jsonSchema.getId()).insert(dBObject);
        dataObject.remove(ID_FIELD);
        dataObject.put(idField, dBObject.get(ID_FIELD).toString());
        return dataObject;
    }

    @Override
    protected Map<String, Object> readData(JsonSchema jsonSchema, String idValue) {        
        Object idFieldValue = ObjectId.isValid(idValue) ? new ObjectId(idValue) : idValue;        
        DBObject query = new BasicDBObject(ID_FIELD, idFieldValue);
        return getDataMap(db.getCollection(jsonSchema.getId()).findOne(query), getIdField(jsonSchema));
    }

    @Override
    protected boolean deleteData(JsonSchema jsonSchema, String idValue) {
        Object idFieldValue = ObjectId.isValid(idValue) ? new ObjectId(idValue) : idValue;        
        DBObject query = new BasicDBObject(ID_FIELD, idFieldValue);
        db.getCollection(jsonSchema.getId()).remove(query);
        return true;
    }

    @Override
    protected boolean updateData(JsonSchema jsonSchema, Map<String, Object> validatedDataObject) {
        String idField = getIdField(jsonSchema);
        String idValue = validatedDataObject.get(idField).toString();
        Object idFieldValue = ObjectId.isValid(idValue) ? new ObjectId(idValue) : idValue; 
        validatedDataObject.remove(idField);
        DBObject query = new BasicDBObject(ID_FIELD, idFieldValue);
        DBObject dBObject = new BasicDBObject(validatedDataObject);
        db.getCollection(jsonSchema.getId()).findAndModify(query, (dBObject));
        return true;
    }

    @Override
    protected List<Map<String, Object>> listData(JsonSchema jsonSchema) {
        String idField = getIdField(jsonSchema);
        List<DBObject> dBObjects = db.getCollection(jsonSchema.getId()).find().toArray();
        if (dBObjects != null) {
            List<Map<String, Object>> listOfMap = new ArrayList<>(dBObjects.size());
            for (DBObject dBObject : dBObjects) {
                listOfMap.add(getDataMap(dBObject, idField));
            }
            return listOfMap;
        }
        return null;
    }
    
    @Override
    protected boolean deleteData(JsonSchema jsonSchema, Query query) {
        String idField = getIdField(jsonSchema);
        db.getCollection(jsonSchema.getId()).remove(getQueryObject(idField,query));        
        return true;
    }

    @Override
    protected List<Map<String, Object>> listData(JsonSchema jsonSchema, Query query) {
        String idField = getIdField(jsonSchema);
        List<DBObject> dBObjects = db.getCollection(jsonSchema.getId()).find(getQueryObject(idField,query)).toArray();
        if (dBObjects != null) {
            List<Map<String, Object>> listOfMap = new ArrayList<>(dBObjects.size());
            for (DBObject dBObject : dBObjects) {
                listOfMap.add(getDataMap(dBObject, idField));
            }
            return listOfMap;
        }
        return null;
    }

    private Map<String, Object> getDataMap(DBObject dBObject, String idFieldName) {
        Map<String, Object> dataMap = dBObject.toMap();
        dataMap.put(idFieldName, dataMap.remove(ID_FIELD).toString());
        return dataMap;
    }

    private DBObject getQueryObject(String idField,Query query) {
        BasicDBObject queryObject = new BasicDBObject();
        String filterName ;
        List<Filter> filters = query.getFilters();
        for (Filter filter : filters) {
            // Process ID Field
            filterName = filter.getName();
            if(idField.equals(filterName)) {
                filterName = ID_FIELD;
            }
            switch (filter.getOperator()) {
                case EQUALS:
                    queryObject.append(filterName, filter.getValue());
                    break;
                case IS_NULL:
                    queryObject.append(filterName, new BasicDBObject("$exists",false));
                    break;
            }

        }

        return queryObject;
    }

    

}
