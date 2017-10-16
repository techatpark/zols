/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import javax.validation.Validation;
import javax.validation.Validator;
import org.zols.datastore.query.Page;
import org.zols.datastore.query.Query;
import org.zols.datastore.util.JsonUtil;
import static org.zols.datastore.util.JsonUtil.asMap;
import org.zols.datatore.exception.ConstraintViolationException;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;
import static org.zols.jsonschema.util.JsonSchemaUtil.getJsonSchema;

/**
 *
 * @author sathish
 * @param <T> type of the objects that we will manage through this instance
 */
public final class ObjectManager<T> {

    private final DataStore dataStore;
    private final Validator validator;

    private final Class<T> clazz;
    private final JsonSchema jsonSchema;

    public ObjectManager(Class<T> clazz, DataStore dataStore) {
        this.clazz = clazz;
        this.jsonSchema = getJsonSchema(clazz);
        this.dataStore = dataStore;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public T create(T object) throws DataStoreException {
        return create(object, null);
    }

    public T create(T object, Locale locale) throws DataStoreException {
        T createdObject = null;
        if (object != null) {
            Set violations = validator.validate(object);
            if (violations.isEmpty()) {
                Map<String, Object> dataAsMap = asMap(object);
                Map<String, Object> createdDataAsMap;
                if (locale == null) {
                    createdDataAsMap = dataStore.create(jsonSchema, dataAsMap);
                    createdObject = asObject(createdDataAsMap);
                } else {
                    createdDataAsMap = dataStore.create(jsonSchema, jsonSchema.localizeData(dataAsMap, locale, true));
                    createdObject = asObject(jsonSchema.delocalizeData(createdDataAsMap, locale));
                }

            } else {
                throw new ConstraintViolationException(object, violations);
            }
        }
        return createdObject;
    }

    public Optional<T> read(String idValue, Locale locale) throws DataStoreException {

        Map<String, Object> dataAsMap = dataStore.read(jsonSchema, idValue);

        if (dataAsMap == null) {
            return Optional.empty();
        }

        return Optional.of(asObject(jsonSchema.delocalizeData(dataAsMap, locale)));
    }

    public Optional<T> read(String idValue) throws DataStoreException {
        return read(idValue, null);
    }

    public T update(T object, String idValue) throws DataStoreException {
        return update(object, idValue, null);
    }

    public T update(T object, String idValue, Locale locale) throws DataStoreException {
        T updatedObject = null;
        if (object != null) {
            Set violations = validator.validate(object);
            if (violations.isEmpty()) {
                Map<String, Object> dataAsMap = asMap(object);

                boolean updated;
                if (locale == null) {
                    updated = dataStore.updatePartial(jsonSchema, dataAsMap);
                } else {
                    updated = dataStore.updatePartial(jsonSchema, jsonSchema.localizeData(dataAsMap, locale));
                }

                if (updated) {
                    updatedObject = read(idValue, locale).get();
                }

            } else {
                throw new ConstraintViolationException(object, violations);
            }
        }
        return updatedObject;
    }

    public boolean delete() throws DataStoreException {
        return dataStore.delete(jsonSchema);
    }

    public boolean delete(String idValue) throws DataStoreException {
        return dataStore.delete(jsonSchema, idValue);
    }

    public boolean delete(Query query)
            throws DataStoreException {
        return dataStore.delete(jsonSchema, query);
    }

    public List<T> list(Query query, Locale locale) throws DataStoreException {
        return getObjects(dataStore.list(jsonSchema, query), locale);
    }

    public List<T> list() throws DataStoreException {
        return list((Locale) null);
    }

    public List<T> list(Locale locale) throws DataStoreException {
        return list(null, locale);
    }

    public List<T> list(Query query) throws DataStoreException {
        return list(query, (Locale) null);
    }

    private List<T> getObjects(List<Map<String, Object>> maps, Locale locale) {
        return maps.parallelStream().map(dataAsMap
                -> asObject(jsonSchema.delocalizeData(dataAsMap, locale))
        ).collect(toList());
    }

    public Page<T> list(Integer pageNumber, Integer pageSize) throws DataStoreException {
        return list(null, null, pageNumber, pageSize);
    }

    public Page<T> list(Locale locale, Integer pageNumber, Integer pageSize) throws DataStoreException {

        return list(null, locale, pageNumber, pageSize);
    }

    public Page<T> list(Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        return list(query, null, pageNumber, pageSize);
    }

    public Page<T> list(Query query, Locale locale, Integer pageNumber, Integer pageSize) throws DataStoreException {

        Page<Map<String, Object>> page = dataStore.list(jsonSchema, query, pageNumber, pageSize);

        if (page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(), page.getTotal(), page.getContent().parallelStream().map(dataAsMap
                    -> asObject(jsonSchema.delocalizeData(dataAsMap, locale))
            ).collect(toList()));
        }
        return null;
    }

    private T asObject(Map<String, Object> dataAsMap) {
        if (dataAsMap == null) {
            return null;
        }
        dataAsMap.remove("$type");
        return JsonUtil.asObject(clazz, dataAsMap);
    }

}
