package org.zols.datastore;

import java.util.*;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.zols.datastore.query.Filter;
import org.zols.datastore.query.Query;
import org.zols.datastore.util.JsonUtil;
import static org.zols.datastore.util.JsonUtil.asMap;
import org.zols.datatore.exception.ConstraintViolationException;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;
import static org.zols.jsonschema.util.JsonSchemaUtil.getJsonSchema;

/**
 * Data Store is used to Store Static and Dynamic Objects using JSON Schema
 *
 * @author Sathish Kumar Thiyagarajan
 */
public abstract class DataStore {

    private final Validator validator;
    private final SchemaManager schemaManager;

    public DataStore() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        schemaManager = new SchemaManager(this);
    }

    public <T> T create(T object) throws DataStoreException {
        return create(object, null);
    }

    public <T> T create(T object, Locale locale) throws DataStoreException {
        T createdObject = null;
        if (object != null) {
            Set violations = validator.validate(object);
            if (violations.isEmpty()) {
                JsonSchema jsonSchema = getJsonSchema(object.getClass());
                createdObject = (T) readJsonDataAsObject(object.getClass(),
                        create(jsonSchema, getImmutableJSONData(jsonSchema, object, locale)), locale);
            } else {
                throw new ConstraintViolationException(object, violations);
            }

        }
        return createdObject;
    }

    public <T> T read(Class<T> clazz, Locale locale, String idValue) throws DataStoreException {
        return (T) readJsonDataAsObject(clazz, read(getJsonSchema(clazz), idValue), locale);
    }

    public <T> T read(Class<T> clazz, String idValue) throws DataStoreException {
        return read(clazz, null, idValue);
    }

    public Map<String, Object> getImmutableJSONData(JsonSchema jsonSchema, Object object, Locale locale) {
        return getImmutableJSONData(jsonSchema, asMap(object), locale);
    }

    public Map<String, Object> getImmutableJSONData(JsonSchema jsonSchema, Map<String, Object> jsonData, Locale locale) {
        Map linkedHashMap = new LinkedHashMap<>(jsonData);
        linkedHashMap.put("$type", jsonSchema.getId());
        if (locale != null && !Locale.getDefault().equals(locale)) {
            linkedHashMap = jsonSchema.localizeData(linkedHashMap, locale);
        }
        return Collections.unmodifiableMap(linkedHashMap);
    }

    private <T> T readJsonDataAsObject(Class<T> clazz, Map<String, Object> map, Locale locale) {
        if (map != null) {
            map.remove("$type");
        }
        return JsonUtil.asObject(clazz, readJsonData(getJsonSchema(clazz), map, locale));
    }

    private List<Map<String, Object>> readJsonData(JsonSchema jSONSchema, List<Map<String, Object>> listofData, Locale locale) {
        if (listofData != null) {
            return listofData.stream().map(data
                    -> readJsonData(jSONSchema, data, locale)
            ).collect(toList());
        }
        return listofData;
    }

//TODO
    private Map<String, Object> readJsonData(JsonSchema jSONSchema, Map<String, Object> map, Locale locale) {
        if (locale != null && !Locale.getDefault().equals(locale)) {
            map = jSONSchema.delocalizeData(map, locale);
        }
        return map;
    }

    public <T> T update(T object, String idValue) throws DataStoreException {
        return update(object, idValue, null);
    }

    public <T> T update(T object, String idValue, Locale locale) throws DataStoreException {
        T updatedObject = null;
        if (object != null) {
            Set violations = validator.validate(object);
            if (violations.isEmpty()) {
                JsonSchema jsonSchema = getJsonSchema(object.getClass());
                boolean updated = updatePartially(jsonSchema, getImmutableJSONData(jsonSchema, object, locale));
                if (updated) {
                    updatedObject = (T) read(object.getClass(), idValue);
                }
            } else {
                throw new ConstraintViolationException(object, violations);
            }
        }
        return updatedObject;
    }

    public boolean delete(Class clazz) throws DataStoreException {
        return delete(getJsonSchema(clazz));
    }

    public boolean delete(Class clazz, String idValue) throws DataStoreException {
        return delete(getJsonSchema(clazz), idValue);
    }

    public boolean delete(Class clazz, Query query)
            throws DataStoreException {
        JsonSchema jsonSchema = getJsonSchema(clazz);
        return delete(jsonSchema, getTypeFilteredQuery(jsonSchema, query));
    }

    public <T> List<T> list(Class<T> clazz) throws DataStoreException {
        return list(clazz, (Locale) null);
    }

    public <T> List<T> list(Class<T> clazz, Locale locale) throws DataStoreException {
        List<T> objects = null;
        List<Map<String, Object>> maps = list(getJsonSchema(clazz));
        if (maps != null) {
            objects = maps.stream().map(map -> readJsonDataAsObject(clazz, map, locale)).collect(toList());
        }
        return objects;
    }

    public <T> List<T> list(Class<T> clazz, Query query) throws DataStoreException {
        return list(clazz, (Locale) null, query);
    }

    public <T> List<T> list(Class<T> clazz, Locale locale, Query query) throws DataStoreException {
        List<T> objects = null;
        JsonSchema jsonSchema = getJsonSchema(clazz);
        List<Map<String, Object>> maps = list(jsonSchema, getTypeFilteredQuery(jsonSchema, query));
        if (maps != null) {
            objects = maps.stream().map(map -> readJsonDataAsObject(clazz, map, locale)).collect(toList());
        }
        return objects;
    }

    public <T> Page<T> list(Class<T> clazz, Integer pageNumber, Integer pageSize) throws DataStoreException {
        return list(clazz, (Locale) null, pageNumber, pageSize);
    }

    public <T> Page<T> list(Class<T> clazz, Locale locale, Integer pageNumber, Integer pageSize) throws DataStoreException {
        Page<Map<String, Object>> page = list(getJsonSchema(clazz), locale, pageNumber, pageSize);
        if (page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(), page.getTotal(), page.getContent().stream().map(map -> readJsonDataAsObject(clazz, map, locale)).collect(toList()));
        }
        return null;
    }

    public <T> Page<T> list(Class<T> clazz, Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        return list(clazz, (Locale) null, query, pageNumber, pageSize);
    }

    public <T> Page<T> list(Class<T> clazz, Locale locale, Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        JsonSchema jsonSchema = getJsonSchema(clazz);
        Page<Map<String, Object>> page = list(jsonSchema, getTypeFilteredQuery(jsonSchema, query), pageNumber, pageSize);
        if (page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(), page.getTotal(), page.getContent().stream().map(map -> readJsonDataAsObject(clazz, map, locale)).collect(toList()));
        }
        return null;
    }

    /**
     * Data Related
     *
     */
    /**
     *
     * @param schemaId
     * @param jsonData
     * @return
     * @throws DataStoreException
     */
    public Map<String, Object> create(String schemaId, Map<String, Object> jsonData)
            throws DataStoreException {
        return create(schemaId, jsonData, null);
    }

    /**
     *
     * @param schemaId
     * @param jsonData
     * @param locale
     * @return
     * @throws DataStoreException
     */
    public Map<String, Object> create(String schemaId, Map<String, Object> jsonData, Locale locale)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        Set<ConstraintViolation> violations = jsonSchema.validate(jsonData);
        if (violations == null) {
            return jsonSchema.delocalizeData(create(jsonSchema, getImmutableJSONData(jsonSchema, jsonData, locale)), locale);
        } else {
            throw new ConstraintViolationException(jsonData, violations);
        }
    }

    public boolean update(String schemaId, Map<String, Object> jsonData)
            throws DataStoreException {
        return update(schemaId, jsonData, null);
    }

    public boolean update(String schemaId, Map<String, Object> jsonData, Locale locale)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        Set<ConstraintViolation> violations = jsonSchema.validate(jsonData);
        if (violations == null) {
            return updatePartially(jsonSchema, getImmutableJSONData(jsonSchema, jsonData, locale));
        } else {
            throw new ConstraintViolationException(jsonData, violations);
        }
    }

    public boolean updatePartial(String schemaId, String idValue, Map<String, Object> partialJsonData)
            throws DataStoreException {
        return updatePartial(schemaId, idValue, partialJsonData, null);
    }

    public boolean updatePartial(String schemaId, String idValue, Map<String, Object> partialJsonData, Locale locale)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        Map<String, Object> jsonData = read(jsonSchema, idValue);
        jsonData.putAll(partialJsonData);
        Set<ConstraintViolation> violations = jsonSchema.validate(jsonData);
        if (violations == null) {
            return updatePartially(jsonSchema, getImmutableJSONData(jsonSchema, jsonData, locale));
        } else {
            throw new ConstraintViolationException(jsonData, violations);
        }
    }
    
    public Map<String, Object> read(String schemaId, String name,Locale loc)
            throws DataStoreException {
        return read(schemaId, loc, name);
    }

    public Map<String, Object> read(String schemaId, String name)
            throws DataStoreException {
        return read(schemaId, (Locale) null, name);
    }

    public Map<String, Object> read(String schemaId, Locale locale, String name)
            throws DataStoreException {
        JsonSchema jSONSchema = schemaManager.getJsonSchema(schemaId);
        Map<String, Object> fullData = read(jSONSchema, name);
        return jSONSchema.delocalizeData(fullData, locale);
    }

    public boolean delete(String schemaId)
            throws DataStoreException {
        return delete(schemaManager.getJsonSchema(schemaId));
    }

    public boolean delete(String schemaId, String name)
            throws DataStoreException {
        return delete(schemaManager.getJsonSchema(schemaId), name);
    }

    public boolean delete(String schemaId, Query query)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return delete(jsonSchema, this.getTypeFilteredQuery(jsonSchema, query));
    }
    
    public List<Map<String, Object>> list(String schemaId,Locale loc)
            throws DataStoreException {
        return list(schemaManager.getJsonSchema(schemaId),loc);
    }

    public List<Map<String, Object>> list(String schemaId)
            throws DataStoreException {
        return list(schemaManager.getJsonSchema(schemaId));
    }

    public List<Map<String, Object>> list(String schemaId, Query query)
            throws DataStoreException {
        return list(schemaId, (Locale) null, query);
    }

    public List<Map<String, Object>> list(String schemaId, Locale locale, Query query)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        Query typeFilteredQuery = getTypeFilteredQuery(jsonSchema, query);
        return readJsonData(jsonSchema, list(jsonSchema, typeFilteredQuery), locale);
    }

    public Page<Map<String, Object>> list(String schemaId, Integer pageNumber, Integer pageSize)
            throws DataStoreException {
        return list(schemaManager.getJsonSchema(schemaId), (Locale) null, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(String schemaId, Locale locale, Integer pageNumber, Integer pageSize)
            throws DataStoreException {
        return list(schemaManager.getJsonSchema(schemaId), locale, pageNumber, pageSize);
    }

    public Page<Map<String, Object>> list(String schemaId, Query query, Integer pageNumber, Integer pageSize)
            throws DataStoreException {
        JsonSchema jsonSchema = schemaManager.getJsonSchema(schemaId);
        return list(jsonSchema, getTypeFilteredQuery(jsonSchema, query), pageNumber, pageSize);
    }


    protected List<Map<String, Object>> list(JsonSchema jsonSchema)
            throws DataStoreException {
        return list(jsonSchema, (Locale) null);
    }

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param locale
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected List<Map<String, Object>> list(JsonSchema jsonSchema, Locale locale)
            throws DataStoreException {
        return readJsonData(jsonSchema, list(jsonSchema, getTypeFilteredQuery(jsonSchema)), locale);
    }

    public Query getTypeFilteredQuery(JsonSchema jsonSchema, Query query) throws DataStoreException {
        if (query == null) {
            query = new Query();
        }

        List<String> implementations = schemaManager.listExtenstionTypes(jsonSchema.getId());
        if (implementations == null || implementations.isEmpty()) {
            query.addFilter(new Filter("$type", Filter.Operator.EQUALS, jsonSchema.getId()));
        } else {
            implementations.add(jsonSchema.getId());
            query.addFilter(new Filter("$type", Filter.Operator.EXISTS_IN, implementations));
        }
        return query;
    }

    public Query getTypeFilteredQuery(JsonSchema jsonSchema) throws DataStoreException {
        return getTypeFilteredQuery(jsonSchema, null);
    }

    private boolean delete(JsonSchema jsonSchema) throws DataStoreException {
        return delete(jsonSchema, getTypeFilteredQuery(jsonSchema));
    }

    private Page<Map<String, Object>> list(JsonSchema jsonSchema, Locale locale,
            Integer pageNumber, Integer pageSize) throws DataStoreException {

        Page<Map<String, Object>> page = list(jsonSchema, getTypeFilteredQuery(jsonSchema), pageNumber, pageSize);
        if (locale != null && page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(), page.getTotal(), readJsonData(jsonSchema, page.getContent(), locale));
        }
        return page;
    }

    public SchemaManager getSchemaManager() {
        return schemaManager;
    }
    
    public <T> ObjectManager<T> getSchemaManager(Class<T> clazz) {
        return new ObjectManager<>(this);
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
    protected abstract Map<String, Object> create(
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
    protected abstract Map<String, Object> read(
            JsonSchema jsonSchema,
            String idValue) throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param idValue dynamic object name
     * @return status of the delete operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract boolean delete(JsonSchema jsonSchema,
            String idValue) throws DataStoreException;

    /**
     *
     * @param jsonSchema
     * @param query
     * @return
     * @throws DataStoreException
     */
    protected abstract boolean delete(JsonSchema jsonSchema, Query query)
            throws DataStoreException;

    protected abstract boolean update(JsonSchema jsonSchema, Map<String, Object> validatedDataObject) throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param validatedData validated Object
     * @return status of the update operation
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract boolean updatePartially(JsonSchema jsonSchema,
            Map<String, Object> validatedData)
            throws DataStoreException;

    /**
     *
     * @param jsonSchema schema of dynamic data
     * @param query query to consider
     * @return list of dynamic objects
     * @throws org.zols.datatore.exception.DataStoreException
     */
    protected abstract List<Map<String, Object>> list(JsonSchema jsonSchema,
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
    protected abstract Page<Map<String, Object>> list(JsonSchema jsonSchema,
            Query query,
            Integer pageNumber,
            Integer pageSize)
            throws DataStoreException;

    /**
     * Drops the DataStore
     *
     * @throws DataStoreException
     */
    protected abstract void drop() throws DataStoreException;

    

}
