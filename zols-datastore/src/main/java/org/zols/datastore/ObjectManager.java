/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import org.zols.datastore.query.MapQuery;
import org.zols.datastore.query.Page;
import org.zols.jsonschema.JsonSchema;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.zols.datastore.util.MapUtil.asObject;
import static org.zols.jsonschema.util.JsonSchemaUtil.getJsonSchema;
import static org.zols.jsonschema.util.JsonUtil.asMap;

/**
 * @param <T> type of the objects that we will manage through this instance
 * @author sathish
 */
public final class ObjectManager<T> {

    /**
     * declares variable dataStore.
     */
    private final DataStore dataStore;
    /**
     * declares variable validator.
     */
    private final Validator validator;
    /**
     * declares variable clazz .
     */
    private final Class<T> clazz;
    /**
     * declares variable jsonSchema.
     */
    private final JsonSchema jsonSchema;

    /**
     * Instantiates a new ObjectManager.
     *
     * @param anClazz an clazz
     * @param anDataStore an dataStore
     */
    public ObjectManager(final Class<T> anClazz, final DataStore anDataStore) {
        this.clazz = anClazz;
        this.jsonSchema = getJsonSchema(clazz);
        this.dataStore = anDataStore;
        this.validator =
                Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * Create object.
     *
     * @param object  object
     * @return object
     */
    public T create(final T object) throws DataStoreException {
        return create(object, null);
    }
    /**
     * Create an object.
     *
     * @param object T
     * @param locale  the locale
     * @return createdObject
     */
    public T create(final T object, final Locale locale)
            throws DataStoreException {
        T createdObject = null;
        if (object != null) {
            Set violations = validator.validate(object);
            if (violations.isEmpty()) {
                Map<String, Object> dataAsMap = asMap(object);
                Map<String, Object> createdDataAsMap;
                if (locale == null) {
                    createdDataAsMap = dataStore.create(jsonSchema, dataAsMap);
                    createdObject = asObject(clazz, createdDataAsMap);
                } else {
                    createdDataAsMap = dataStore.create(jsonSchema,
                            jsonSchema.localizeData(dataAsMap, locale, true));
                    createdObject = asObject(clazz,
                            jsonSchema.delocalizeData(createdDataAsMap,
                                    locale));
                }

            } else {
                throw new ConstraintViolationException(object, violations);
            }
        }
        return createdObject;
    }
    /**
     * Read the object.
     *
     * @param locale the locale
     * @param idValues  the idValues
     * @return the optional empty
     */
    public Optional<T> read(final Locale locale, final SimpleEntry... idValues)
            throws DataStoreException {

        Map<String, Object> dataAsMap = dataStore.read(jsonSchema, idValues);

        if (dataAsMap == null) {
            return Optional.empty();
        }

        return Optional.of(
                asObject(clazz, jsonSchema.delocalizeData(dataAsMap, locale)));
    }

    /**
     * Read the object.
     *
     * @param idValues  the idValues
     * @return the idValue
     */
    public Optional<T> read(final AbstractMap.SimpleEntry... idValues)
            throws DataStoreException {
        return read(null, idValues);
    }

    /**
     * Update the object.
     *
     * @param object the object
     * @param idValues  the idValues
     * @return the optional object
     */
    public T update(final T object, final SimpleEntry... idValues)
            throws DataStoreException {
        return update(object, null, idValues);
    }

    /**
     * update the object.
     * @param object the object
     * @param locale the locale
     * @param idValues  the idValues
     * @return the update object
     */
    public T update(final T object, final Locale locale,
                    final SimpleEntry... idValues)
            throws DataStoreException {
        T updatedObject = null;
        if (object != null) {
            Set violations = validator.validate(object);
            if (violations.isEmpty()) {
                Map<String, Object> dataAsMap = asMap(object);

                boolean updated;
                if (locale == null) {
                    updated = dataStore.updatePartial(jsonSchema, dataAsMap,
                            idValues);
                } else {
                    updated = dataStore.updatePartial(jsonSchema,
                            jsonSchema.localizeData(dataAsMap, locale),
                            idValues);
                }

                if (updated) {
                    updatedObject = read(locale, idValues).get();
                }

            } else {
                throw new ConstraintViolationException(object, violations);
            }
        }
        return updatedObject;
    }

    /**
     * delete the object.
     *
     * @return the jsonSchema
     */
    public boolean delete() throws DataStoreException {
        return dataStore.delete(jsonSchema);
    }

    /**
     * delete the object.
     *
     * @param idValues the idValue
     * @return the jsonSchema
     */
    public boolean delete(final AbstractMap.SimpleEntry... idValues)
            throws DataStoreException {
        return dataStore.delete(jsonSchema, idValues);
    }

    /**
     * delete the object.
     *
     * @param query the query
     * @return the jsonSchema
     */
    public boolean delete(final Condition<MapQuery> query)
            throws DataStoreException {
        return dataStore.delete(jsonSchema, query);
    }

    /**
     * List the object.
     *
     * @param query the query
     * @param locale the locale
     * @return the jsonSchema
     */
    public List<T> list(final Condition<MapQuery> query, final Locale locale)
            throws DataStoreException {
        return getObjects(dataStore.list(jsonSchema, query), locale);
    }

    /**
     * List the object.
     *
     * @return the list
     */
    public List<T> list() throws DataStoreException {
        return list((Locale) null);
    }

    /**
     * List the object.
     *
     * @param locale the locale
     * @return the list of  query
     */
    public List<T> list(final Locale locale) throws DataStoreException {
        return list(null, locale);
    }

    /**
     * List the object.
     *
     * @param query the query
     * @return the list of query
     */
    public List<T> list(final Condition<MapQuery> query)
            throws DataStoreException {
        return list(query, null);
    }

    /**
     * Gets the object.
     *
     * @param maps the list
     * @param locale the locale
     * @return the maps
     */
    private List<T> getObjects(final List<Map<String, Object>> maps,
                               final Locale locale) {
        return maps == null ? null : maps.parallelStream().map(dataAsMap
                -> asObject(clazz,
                jsonSchema.delocalizeData(dataAsMap, locale))
        ).collect(toList());
    }

    /**
     * List the object page.
     *
     * @param pageNumber the pageNumber
     * @param pageSize the pageSize
     * @return the list of page
     */
    public Page<T> list(final Integer pageNumber, final Integer pageSize)
            throws DataStoreException {
        return list(null, null, pageNumber, pageSize);
    }

    /**
     * List the object page.
     *
     * @param locale the locale
     * @param pageNumber the pageNumber
     * @param pageSize the pageSize
     * @return the list of page
     */
    public Page<T> list(final Locale locale, final Integer pageNumber,
                        final Integer pageSize)
            throws DataStoreException {

        return list(null, locale, pageNumber, pageSize);
    }

    /**
     * List the object page.
     *
     * @param query the query
     * @param pageNumber the pageNumber
     * @param pageSize the pageSize
     * @return the list of page
     */
    public Page<T> list(final Condition<MapQuery> query,
                        final Integer pageNumber,
                        final Integer pageSize) throws DataStoreException {
        return list(query, null, pageNumber, pageSize);
    }

    /**
     * List the object page.
     *
     * @param locale the locale
     * @param query the query
     * @param pageNumber the pageNumber
     * @param pageSize the pageSize
     * @return the list of page
     */
    public Page<T> list(final Condition<MapQuery> query, final Locale locale,
                        final Integer pageNumber, final Integer pageSize)
            throws DataStoreException {

        Page<Map<String, Object>> page =
                dataStore.list(jsonSchema, query, pageNumber, pageSize);

        if (page != null) {
            return new Page(page.getPageNumber(), page.getPageSize(),
                    page.getTotal(),
                    page.getContent().parallelStream().map(dataAsMap
                            -> asObject(clazz,
                            jsonSchema.delocalizeData(dataAsMap, locale))
                    ).collect(toList()));
        }
        return null;
    }


}
