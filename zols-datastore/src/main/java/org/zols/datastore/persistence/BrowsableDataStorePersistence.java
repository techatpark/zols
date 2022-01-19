/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.persistence;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import cz.jirutka.rsql.parser.ast.Node;
import org.zols.datastore.DataStoreException;
import org.zols.datastore.query.AggregatedResults;
import org.zols.datastore.query.MapQuery;
import org.zols.jsonschema.JsonSchema;

/**
 * @author sathish
 */
public interface BrowsableDataStorePersistence extends DataStorePersistence {

    /**
     * browse the schema.
     *
     * @param jsonSchema schema of dynamic data
     * @param keyword    the keyword
     * @param query      the query
     * @param pageNumber the pageNumber
     * @param pageSize   the pageSize
     * @return schema
     */
    default AggregatedResults browse(final JsonSchema jsonSchema,
                                     final String keyword,
                                     final Condition<MapQuery> query,
                                     final Integer pageNumber,
                                     final Integer pageSize)
            throws DataStoreException {
        return this.browse(jsonSchema, keyword, getNode(query), pageNumber,
                pageSize);
    }

    /**
     * @param jsonSchema schema of dynamic data
     * @param keyword    the keyword
     * @param queryNode  the queryNode
     * @param pageNumber the pageNumber
     * @param pageSize   the pageSize
     * @return schema
     */
    AggregatedResults browse(JsonSchema jsonSchema,
                             String keyword,
                             Node queryNode,
                             Integer pageNumber,
                             Integer pageSize) throws DataStoreException;
}
