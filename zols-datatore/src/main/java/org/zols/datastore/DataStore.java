package org.zols.datastore;

import java.util.List;
import java.util.Map;
import org.zols.datastore.jsonschema.JSONSchema;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchemaForSchema;
import static org.zols.datastore.util.JsonUtil.asMap;
import org.zols.datatore.exception.DataStoreException;

/**
 * Data Store is used to Store Static and Dynamic Objects using JSON Schema
 *
 * @author Sathish Kumar Thiyagarajan
 */
public abstract class DataStore {
    
    public Map<String, Object> createSchema(
            String jsonSchemaTxt)
            throws DataStoreException {
        if (jsonSchemaForSchema().validate(jsonSchemaTxt)) {
            return createData(jsonSchemaForSchema(), asMap(jsonSchemaTxt));
        }
        return null;
    }
    
    public Map<String, Object> getSchema(String id)
            throws DataStoreException {
        return readData(jsonSchemaForSchema(), id);
    }
    
    public Boolean deleteSchema(String id) throws DataStoreException {
        return deleteData(jsonSchemaForSchema(), id);
    }
    
    public List<Map<String, Object>> listSchema() throws DataStoreException {
        return listData(jsonSchemaForSchema());
    }

    /**
     * ALL ABSTRACT METHODS WILL COME HERE
     */
    /**
     *
     * @param validatedDataObject validated object
     * @param jsonSchema schema of dynamic data
     * @return dynamic data as map
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract Map<String, Object> createData(
            JSONSchema jsonSchema,
            Map<String, Object> validatedDataObject)
            throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param idValue dynamic object name
     * @return dynamic data as map
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract Map<String, Object> readData(
            JSONSchema jsonSchema,
            String idValue) throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param idValue dynamic object name
     * @return status of the delete operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract boolean deleteData(JSONSchema jsonSchema,
            String idValue) throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param validatedDataObject validated Object
     * @return status of the update operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract boolean updateData(JSONSchema jsonSchema,
            Map<String, Object> validatedDataObject)
            throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract List<Map<String, Object>> listData(JSONSchema jsonSchema)
            throws DataStoreException;

    /**
     * Drops the DataStore
     *
     * @throws DataStoreException
     */
    protected abstract void drop() throws DataStoreException;
    
}
