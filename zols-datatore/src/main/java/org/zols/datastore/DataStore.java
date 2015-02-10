package org.zols.datastore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import org.zols.datastore.domain.Schema;
import org.zols.datastore.query.Query;
import org.zols.datastore.validator.TV4;
import org.zols.datatore.exception.DataStoreException;

/**
 * Data Store is used to Store static and Dynamic Objects using JSON Schema
 *
 * @author sathish_ku
 */
public abstract class DataStore {

    /**
     * JSON Schema validator
     */
    protected final TV4 tv4;

    /**
     * Intialize the DataStore with JSON Schema validator
     */
    public DataStore() {
        tv4 = new TV4();
    }

    /**
     *
     * @param schemaName name of the schema
     * @param jsonData Dynamic Data for the given schema
     * @return created Object
     * @throws org.zols.datatore.exception.DataStoreException
     */
    public Map<String, Object> create(String schemaName, Map<String, Object> jsonData) throws DataStoreException {
        Schema schema = read(Schema.class, schemaName);
        try {
            if (tv4.validate(jsonData, schema.getSchema())) {
                return createData(schema.getSchema(), jsonData);
            }
        } catch (ScriptException | NoSuchMethodException | JsonProcessingException ex) {
            throw new DataStoreException("Unable to create " + schemaName, ex);
        }
        return null;
    }

    /**
     *
     * @param schemaName name of the schema
     * @param name name of the Schema data
     * @return Object with given name
     * @throws org.zols.datatore.exception.DataStoreException
     */
    public Map<String, Object> read(String schemaName, String name) throws DataStoreException {
        return readData(read(schemaName), name);
    }

    /**
     *
     * @param schemaName name of the schema
     * @param jsonData Dynamic Data for the given schema
     * @return status of the update operation
     */
    public boolean update(String schemaName, Map<String, Object> jsonData) throws DataStoreException {
        Schema schema = read(Schema.class, schemaName);
        try {
            if (tv4.validate(jsonData, schema.getSchema())) {
                return updateData(schema.getSchema(), jsonData);
            }
        } catch (ScriptException | NoSuchMethodException | JsonProcessingException ex) {
            throw new DataStoreException("Unable to update " + schemaName, ex);
        }
        return false;
    }

    /**
     *
     * @param schemaName name of the schema
     * @param name name of the Schema data
     * @return status of the delete operation
     */
    public boolean delete(String schemaName, String name) throws DataStoreException {
        return DataStore.this.deleteData(read(schemaName), name);
    }

    /**
     *
     * @param schemaName name of the schema
     * @return lis of dynamic objects
     */
    public List<Map<String, Object>> list(String schemaName) throws DataStoreException {
        return DataStore.this.listData(read(schemaName));
    }

    /**
     * Creates a new object
     *
     * @param <T> Type of the Object
     * @param clazz Class of the Object to be created
     * @param object Object to be created
     * @return created object
     */
    public <T> T create(Class<T> clazz, Object object) throws DataStoreException {
        Map<String, Object> jsonData = tv4.getValueAsMap(object);

        try {
            String jsonSchema = tv4.getJsonSchema(clazz);
            if (tv4.validate(jsonData, jsonSchema)) {
                return tv4.getObjectOfType(clazz, createData(jsonSchema, jsonData));
            }
        } catch (ScriptException | NoSuchMethodException | IOException ex) {
            throw new DataStoreException("Unable to create " + clazz.getName(), ex);
        }
        return null;
    }

    /**
     * reads an Object with given name
     *
     * @param <T> Type of the Object
     * @param clazz Class of the Object to be read
     * @param name name of the Object
     * @return object with given name
     */
    public <T> T read(Class<T> clazz, String name) throws DataStoreException {
        try {
            return tv4.getObjectOfType(clazz, readData(tv4.getJsonSchema(clazz), name));
        } catch (IOException ex) {
            throw new DataStoreException("Unable to read " + clazz.getName(), ex);
        }
    }

    /**
     * reads an Object with given name
     *
     * @param <T> Type of the Object
     * @param clazz Class of the Object to be read
     * @return object with given name
     */
    public <T> List<T> list(Class<T> clazz) throws DataStoreException {
        try {
            List<Map<String, Object>> listOfMap;

            listOfMap = listData(tv4.getJsonSchema(clazz));
            if (listOfMap != null && !listOfMap.isEmpty()) {
                List<T> listOfObject = new ArrayList<>(listOfMap.size());
                for (Map<String, Object> map : listOfMap) {
                    listOfObject.add(tv4.getObjectOfType(clazz, map));
                }
                return listOfObject;
            }

        } catch (IOException ex) {
            throw new DataStoreException("Unable to list " + clazz.getName(), ex);
        }
        return null;
    }

    /**
     * reads an Object with given name
     *
     * @param <T> Type of the Object
     * @param clazz Class of the Object to be read
     * @param query query for the retrival
     * @return object with given name
     */
    public <T> List<T> list(Class<T> clazz, Query query) throws DataStoreException {
        try {
            List<Map<String, Object>> listOfMap = listData(tv4.getJsonSchema(clazz), query);
            if (listOfMap != null && !listOfMap.isEmpty()) {
                List<T> listOfObject = new ArrayList<>(listOfMap.size());
                for (Map<String, Object> map : listOfMap) {
                    listOfObject.add(tv4.getObjectOfType(clazz, map));
                }
                return listOfObject;
            }

        } catch (IOException ex) {
            throw new DataStoreException("Unable to list " + clazz.getName(), ex);
        }
        return null;
    }

    /**
     * reads an Object with given name
     *
     * @param <T> Type of the Object
     * @param clazz Class of the Object to be read
     * @param pageNumber page number
     * @param pageSize size of the page
     * @return object with given name
     */
    public <T> List<T> list(Class<T> clazz, int pageNumber, int pageSize) {
        return null;
    }

    /**
     * @param object dataObject
     * @return status of the update
     */
    public boolean update(Object object) throws DataStoreException {
        try {
            Map<String, Object> jsonData = tv4.getValueAsMap(object);
            String jsonSchema = tv4.getJsonSchema(object.getClass());
            return updateData(jsonSchema, jsonData);
        } catch (IOException ex) {
            throw new DataStoreException("Unable to update " + object, ex);
        }
    }

    /**
     * deletes the Object with given name
     *
     * @param clazz - Class of the Object
     * @param name - name of the Object
     * @return successFlag
     */
    public boolean delete(Class clazz, String name) throws DataStoreException {
        try {
            return deleteData(tv4.getJsonSchema(clazz), name);
        } catch (IOException ex) {
            throw new DataStoreException("Unable to delete " + clazz, ex);
        }
    }

    /**
     * deletes the Objects with given query
     *
     * @param clazz - Class of the Object
     * @param query - query for the Object
     * @return successFlag
     */
    public boolean delete(Class clazz, Query query) throws DataStoreException {
        try {
            return deleteData(tv4.getJsonSchema(clazz), query);
        } catch (IOException ex) {
            throw new DataStoreException("Unable to delete " + clazz, ex);
        }
    }

    public String getIdField(String jsonSchema) {
        try {
            return tv4.getIdField(jsonSchema);
        } catch (IOException ex) {
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getId(String jsonSchema) {
        return tv4.getId(jsonSchema);
    }

    /**
     * gets Id field of a schema
     *
     * @param jsonSchema schema object
     * @return name of the id field
     */
    public String getIdField(JsonSchema jsonSchema) {
        String idField = null;
        String idValue;
        Map<String, JsonSchema> properties = jsonSchema.asObjectSchema().getProperties();
        for (Map.Entry<String, JsonSchema> property : properties.entrySet()) {
            idValue = property.getValue().getId();
            if (idValue != null && idValue.trim().equals("true")) {
                idField = property.getKey();
                break;
            }
        }
        return idField;
    }

    /**
     * Schema Related Methods
     *
     * @param jsonSchemaObject schema object
     * @return created json schema
     */
    public String create(Map<String, Object> jsonSchemaObject) throws DataStoreException {
        Schema schema = new Schema();
        String jsonSchema;
        try {
            jsonSchema = tv4.getValueAsString(jsonSchemaObject);
            schema.setName(getId(jsonSchema));
            schema.setSchema(jsonSchema);
            schema = create(Schema.class, schema);
            return schema.getSchema();
        } catch (JsonProcessingException ex) {
            throw new DataStoreException("Unable to create schema ", ex);
        }

    }

    public String read(String schemaName) throws DataStoreException {
        Schema schema = read(Schema.class, schemaName);
        return schema.getSchema();
    }

    public Map<String, Object> readAsMap(String schemaName) throws DataStoreException {
        try {
            return tv4.getValueAsMap(read(schemaName));
        } catch (IOException ex) {
            throw new DataStoreException("Unable to read as Map ", ex);
        }
    }

    public boolean delete(String schemaName) throws DataStoreException {
        return delete(Schema.class, schemaName);
    }

    public boolean update(String jsonSchema) throws DataStoreException {
        Schema schema = read(Schema.class, getId(jsonSchema));
        schema.setSchema(jsonSchema);
        return update(schema);
    }

    public List<Map<String, Object>> list() throws DataStoreException {
        List<Schema> schemas = list(Schema.class);
        if (schemas != null) {
            List<Map<String, Object>> jsonSchemas = new ArrayList<>(schemas.size());
            for (Schema schema : schemas) {
                try {
                    jsonSchemas.add(tv4.getValueAsMap(schema.getSchema()));
                } catch (IOException ex) {
                    Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return jsonSchemas;
        }
        return null;
    }

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected List<Map<String, Object>> listData(String jsonSchema) throws DataStoreException {
        return listData(jsonSchema, null);
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
            String jsonSchema,
            Map<String, Object> validatedDataObject) throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param idValue dynamic object name
     * @return dynamic data as map
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract Map<String, Object> readData(
            String jsonSchema,
            String idValue) throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param idValue dynamic object name
     * @return status of the delete operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract boolean deleteData(String jsonSchema,
            String idValue) throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param query query to delete
     * @return status of the delete operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract boolean deleteData(String jsonSchema,
            Query query) throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param validatedDataObject validated Object
     * @return status of the update operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract boolean updateData(String jsonSchema,
            Map<String, Object> validatedDataObject) throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param query query to be used
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract List<Map<String, Object>> listData(String jsonSchema,
            Query query) throws DataStoreException;

}
