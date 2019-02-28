/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.persistence;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import org.zols.datastore.query.AggregatedResults;
import org.zols.datastore.query.MapQuery;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;

/**
 *
 * @author sathish
 */
public interface BrowsableDataStorePersistence extends DataStorePersistence{

    AggregatedResults browse(JsonSchema jsonSchema,
            String keyword,
            Condition<MapQuery> query,
            Integer pageNumber,
            Integer pageSize) throws DataStoreException ;
}
