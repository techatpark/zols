/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.zols.datastore.util.JsonUtil.asMap;
import static org.zols.datastore.util.JsonUtil.asObject;
import org.zols.datatore.exception.ConstraintViolationException;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;
import static org.zols.jsonschema.util.JsonSchemaUtil.getJsonSchema;

/**
 *
 * @author sathish
 */
public final class ObjectManager<T> {

    private final DataStore dataStore;
    private final Validator validator;

    public ObjectManager(DataStore dataStore) {
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
                JsonSchema jsonSchema = getJsonSchema(object.getClass());
                Map<String, Object> dataAsMap = asMap(object);
                Map<String, Object> createdDataAsMap;
                if (locale == null) {
                    createdDataAsMap = dataStore.create(jsonSchema, dataAsMap);
                } else {
                    createdDataAsMap = dataStore.create(jsonSchema, jsonSchema.localizeData(dataAsMap, locale,true));
                }
                createdObject = asObject((Class<T>) object.getClass(), createdDataAsMap);
            } else {
                throw new ConstraintViolationException(object, violations);
            }
        }
        return createdObject;
    }

    public <T> T read(Class<T> clazz, String idValue, Locale locale) throws DataStoreException {
        JsonSchema jsonSchema = getJsonSchema(clazz.getClass());
        Map<String, Object> dataAsMap = dataStore.read(jsonSchema, idValue);
        return (locale == null) ? asObject(clazz, dataAsMap)
                : asObject(clazz, jsonSchema.delocalizeData(dataAsMap, locale));
    }

    public <T> T read(Class<T> clazz, String idValue) throws DataStoreException {
        return read(clazz, idValue, null);
    }
}
