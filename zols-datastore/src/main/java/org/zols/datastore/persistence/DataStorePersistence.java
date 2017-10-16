/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.persistence;

import java.util.List;
import java.util.Map;
import org.zols.datastore.query.Page;
import org.zols.datastore.query.Query;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;

/**
 *
 * @author sathish
 */
public interface DataStorePersistence {

    /**
     *
     * @param validatedDataObject validated object
     * @param jsonSchema schema of dynamic data
     * @return dynamic data as map
     * @throws org.zols.datatore.exception.DataStoreException
     */
    abstract Map<String, Object> create(
            JsonSchema jsonSchema,
            Map<String, Object> validatedDataObject)
            throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param idValue dynamic object name
     * @return dynamic data as map
     * @throws org.zols.datatore.exception.DataStoreException
     */
    abstract Map<String, Object> read(
            JsonSchema jsonSchema,
            String idValue) throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param idValue dynamic object name
     * @return status of the delete operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    abstract boolean delete(JsonSchema jsonSchema,
            String idValue) throws DataStoreException;

    /**
     *
     * @param jsonSchema
     * @param query
     * @return
     * @throws DataStoreException
     */
    abstract boolean delete(JsonSchema jsonSchema, Query query)
            throws DataStoreException;

    abstract boolean update(JsonSchema jsonSchema, String idValue,
            Map<String, Object> validatedDataObject) throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param validatedData validated Object
     * @return status of the update operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    abstract boolean updatePartially(JsonSchema jsonSchema,
            Map<String, Object> validatedData)
            throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param query query to consider
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    abstract List<Map<String, Object>> list(JsonSchema jsonSchema,
            Query query)
            throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param query query to consider
     * @param pageNumber
     * @param pageSize
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    abstract Page<Map<String, Object>> list(JsonSchema jsonSchema,
            Query query,
            Integer pageNumber,
            Integer pageSize)
            throws DataStoreException;

    /**
     * Drops the DataStore
     *
     * @throws DataStoreException
     */
    abstract void drop() throws DataStoreException;
}
