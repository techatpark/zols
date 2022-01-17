/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.persistence;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import com.github.rutledgepaulv.qbuilders.visitors.RSQLVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.zols.datastore.DataStore;
import org.zols.datastore.DataStoreException;
import org.zols.datastore.query.MapQuery;
import org.zols.datastore.query.Page;
import org.zols.jsonschema.JsonSchema;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

/**
 * @author sathish
 */
public interface DataStorePersistence {

    /**
     * Called when new datastore created with this DataStorePersistence.
     *
     * @param dataStore
     * @throws DataStoreException
     */
    void onIntialize(DataStore dataStore);

    /**
     * Called when new schema is created.
     *
     * @param jsonSchema
     * @throws DataStoreException
     */
    void onNewSchema(JsonSchema jsonSchema)
            throws DataStoreException;

    /**
     * Called when new schema is updated.
     *
     * @param oldSchema
     * @param newSchema
     * @throws DataStoreException
     */
    void onUpdateSchema(JsonSchema oldSchema, JsonSchema newSchema)
            throws DataStoreException;

    /**
     * Called when schema is deleted.
     *
     * @param jsonSchema
     * @throws DataStoreException
     */
    void onDeleteSchema(JsonSchema jsonSchema)
            throws DataStoreException;

    /**
     * @param jsonData   validated object
     * @param jsonSchema schema of dynamic data
     * @return dynamic data as map
     * @throws DataStoreException
     */
    Map<String, Object> create(
            JsonSchema jsonSchema,
            Map<String, Object> jsonData)
            throws DataStoreException;

    /**
     * @param jsonSchema schema of dynamic data
     * @param idValues   dynamic object name
     * @return dynamic data as map
     * @throws DataStoreException
     */
    Map<String, Object> read(
            JsonSchema jsonSchema,
            SimpleEntry<String, Object>... idValues) throws DataStoreException;

    /**
     * @param jsonSchema schema of dynamic data
     * @param idValues   dynamic object name
     * @return status of the delete operation
     * @throws DataStoreException
     */
    boolean delete(JsonSchema jsonSchema,
                   SimpleEntry<String, Object>... idValues)
            throws DataStoreException;

    /**
     * @param jsonSchema
     * @param query
     * @return schema
     * @throws DataStoreException
     */
    default boolean delete(final JsonSchema jsonSchema,
                               final Condition<MapQuery> query)
            throws DataStoreException {
        return this.delete(jsonSchema, getNode(query));
    }

    /**
     * @param jsonSchema
     * @param queryNode
     * @return schema
     * @throws DataStoreException
     */
    boolean delete(JsonSchema jsonSchema, Node queryNode)
            throws DataStoreException;

    /**
     * @param jsonSchema
     * @param jsonData jsonData
     * @param idValues idValues
     * @return schema
     * @throws DataStoreException
     */
    boolean update(JsonSchema jsonSchema,
                   Map<String, Object> jsonData,
                   SimpleEntry<String, Object>... idValues)
            throws DataStoreException;

    /**
     * @param jsonSchema schema of dynamic data
     * @param jsonData   validated Object
     * @param idValues the idValues
     * @return status of the update operation
     * @throws DataStoreException
     */
    boolean updatePartially(JsonSchema jsonSchema,
                            Map<String, Object> jsonData,
                            AbstractMap.SimpleEntry<String, Object>... idValues)
            throws DataStoreException;

    /**
     * @param jsonSchema schema of dynamic data
     * @param query      query to consider
     * @return list of dynamic objects
     * @throws DataStoreException
     */
    default List<Map<String, Object>> list(final JsonSchema jsonSchema,
                                           final Condition<MapQuery> query)
            throws DataStoreException {
        return this.list(jsonSchema, getNode(query));
    }

    /**
     * @param jsonSchema schema of dynamic data
     * @param queryNode  query to consider
     * @return list of dynamic objects
     * @throws DataStoreException
     */
    List<Map<String, Object>> list(JsonSchema jsonSchema,
                                   Node queryNode)
            throws DataStoreException;

    /**
     * @param jsonSchema schema of dynamic data
     * @param query      query to consider
     * @param pageNumber
     * @param pageSize
     * @return list of dynamic objects
     * @throws DataStoreException
     */
    default Page<Map<String, Object>> list(final JsonSchema jsonSchema,
                                           final Condition<MapQuery> query,
                                           final Integer pageNumber,
                                           final Integer pageSize)
            throws DataStoreException {
        return this.list(jsonSchema, getNode(query), pageNumber, pageSize);
    }

    /**
     * @param jsonSchema schema of dynamic data
     * @param queryNode  query to consider
     * @param pageNumber
     * @param pageSize
     * @return list of dynamic objects
     * @throws DataStoreException
     */
    Page<Map<String, Object>> list(JsonSchema jsonSchema,
                                   Node queryNode,
                                   Integer pageNumber,
                                   Integer pageSize)
            throws DataStoreException;

    /**
     * @param query the query
     * @return list of dynamic objects
     * @throws DataStoreException
     */
    default Node getNode(final Condition<MapQuery> query) {
        return query == null ? null
                : new RSQLParser().parse(query.query(new RSQLVisitor()));
    }
}
