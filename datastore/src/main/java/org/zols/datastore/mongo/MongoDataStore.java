package org.zols.datastore.mongo;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.zols.datastore.DataStore;
import org.zols.datastore.domain.Attribute;
import org.zols.datastore.domain.BaseObject;
import org.zols.datastore.domain.Entity;
import org.zols.datastore.exception.DataStoreException;

@Service
public class MongoDataStore extends DataStore {

    @Autowired
    private MongoOperations mongoOperation;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(Object object, Class<T> clazz) {
        try {
            mongoOperation.insert(object);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new DataStoreException(duplicateKeyException.getMessage(), duplicateKeyException);
        }
        return (T) object;
    }

    @Override
    public <T> T read(String name, Class<T> clazz) {
        T object = mongoOperation.findById(name, clazz);
        return object;
    }

    /**
     *
     * @param <T>
     * @param object
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T update(Object object, Class<T> clazz) {
        mongoOperation.save(object);
        return (T) object;
    }

    @Override
    public <T> void update(String id, Map<String, Object> objectMap, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        Update update = new Update();
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            update.set(entry.getKey(), entry.getValue());
        }
        mongoOperation.updateFirst(query, update, clazz);
    }

    @Override
    public <T> T delete(String id, Class<T> clazz) {
        T object = mongoOperation.findAndRemove(getByIdQuery(id, clazz), clazz);
        return object;
    }

    @Override
    public <T> Page<T> list(Pageable pageable, Class<T> clazz) {
        Query query = getListQuery(pageable, null);
        int totalRecords = (int) mongoOperation.count(query, clazz);
        if (totalRecords != 0) {
            Page<T> objects = new PageImpl<T>(mongoOperation.find(query, clazz), pageable, totalRecords);
            return objects;
        }
        return null;
    }
    
    @Override
    public  Page<BaseObject> list(Pageable pageable, String entityName, String attributePart) {
        Query query = getListQuery(pageable, null);
        
        Class<? extends BaseObject> clazz = (Class<BaseObject>)getBeanClass(entityName);
        Entity entity= read(entityName, Entity.class);
        
        for(Attribute attr : entity.getAttributes()){
        	if(attr.isSearcheable()){
        		Criteria where = Criteria.where(attr.getName());
        		query.addCriteria(where.regex(attributePart));
        	}
        		
        }
        
        for(BaseObject baseobj : mongoOperation.findAll(clazz)){
        	System.out.println(baseobj);
        	BeanWrapper wrapper = new BeanWrapperImpl(baseobj);
        	for(PropertyDescriptor desc : wrapper.getPropertyDescriptors()){
        		System.out.println(desc.getName());
        	}
        }
        Class clazz2 = (Class<BaseObject>)clazz;
        int totalRecords = (int) mongoOperation.count(query, clazz2);
        if (totalRecords != 0) 
        {
        	Page<? extends BaseObject> objects = new PageImpl<BaseObject>(mongoOperation.find(query, clazz2), pageable, totalRecords);
            return (Page<BaseObject>)objects;
        }
        return null;
    }

    private Query getListQuery(Pageable pageable, Object searchObjct) {
        Query query = new Query();
        if (pageable != null) {
            query.skip((pageable.getPageNumber()  ) * pageable.getPageSize());
            query.limit(pageable.getPageSize());
        }
        if (searchObjct != null) {
            Object propertyValue;
            Criteria criteria = null;
            BeanWrapper beanWrapper = new BeanWrapperImpl(searchObjct);
            for (PropertyDescriptor propertyDescriptor : beanWrapper.getPropertyDescriptors()) {
                propertyValue = beanWrapper.getPropertyValue(propertyDescriptor.getName());
                if (propertyValue != null && !(propertyValue instanceof Class)) {
                    if (criteria == null) {
                        criteria = Criteria.where(propertyDescriptor.getName()).is(propertyValue);
                    } else {
                        criteria.and(propertyDescriptor.getName()).is(propertyValue);
                    }
                }
            }
            if (criteria != null) {
                query.addCriteria(criteria);
            }

        }
        return query;
    }

    @Override
    public <T> List<T> list(Class<T> aClass) {
        Query query = getListQuery(null, null);
        int totalRecords = (int) mongoOperation.count(query, aClass);
        if (totalRecords != 0) {
            return mongoOperation.find(query, aClass);
        }
        return null;
    }

    @Override
    public <T> List<T> listByExample(T searchObject) {
        Query query = getListQuery(null, searchObject);
        return (List<T>) mongoOperation.find(query, searchObject.getClass());
    }

    @Override
    public <T> T deleteByExample(T searchObject) {
        Query query = getListQuery(null, searchObject);
        return (T) mongoOperation.findAndRemove(query, searchObject.getClass());
    }

    @Override
    public <T> List<T> list(List<org.zols.datastore.domain.Criteria> criterias, Class<T> aClass) {
        return (List<T>) mongoOperation.find(getQuery(criterias), aClass);
    }

    private Query getQuery(List<org.zols.datastore.domain.Criteria> criterias) {
        Query query = new Query();
        Criteria mongoCriteria = null;
        for (org.zols.datastore.domain.Criteria criteria : criterias) {
            if (mongoCriteria == null) {
                mongoCriteria = Criteria.where(criteria.getFieldName());
                addCriteriaCondition(mongoCriteria, criteria);
            } else {
                mongoCriteria.andOperator(addCriteriaCondition(Criteria.where(criteria.getFieldName()), criteria));
            }
        }
        query.addCriteria(mongoCriteria);
        return query;
    }

    private Criteria addCriteriaCondition(Criteria mongoCriteria, org.zols.datastore.domain.Criteria criteria) {
        switch (criteria.getType()) {
            case GREATER_THAN_EQUALS:
                mongoCriteria.gte(criteria.getValue());
                break;

            case GREATER_THAN:
                mongoCriteria.gt(criteria.getValue());
                break;

            case LESSER_THAN_EQUALS:
                mongoCriteria.lte(criteria.getValue());
                break;

            case LESSER_THAN:
                mongoCriteria.lt(criteria.getValue());
                break;

            case IS:
                mongoCriteria.is(criteria.getValue());
                break;

            case IS_NULL:
                mongoCriteria.exists(false);
                break;

            case IS_NOTNULL:
                mongoCriteria.exists(true);
                break;

        }
        return mongoCriteria;
    }

    private Query getByIdQuery(String id, Class clazz) {
        Query query = new Query();
        query.addCriteria(Criteria.where(getIdField(clazz).getName()).is(id));
        return query;
    }

    /**
     * Gets the Field which is annotated with @Id.class
     *
     * @param clazz
     * @return
     */
    private Field getIdField(Class clazz) {
        Class superClass = clazz;
        while (superClass != null) {
            for (Field field : superClass.getDeclaredFields()) {
                Id id = field.getAnnotation(Id.class);
                if (id != null) {
                    return field;
                }
            }
            superClass = superClass.getSuperclass();
        }
        return null;
    }

}