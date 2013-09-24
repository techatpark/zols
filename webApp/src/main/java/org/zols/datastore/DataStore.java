package org.zols.datastore;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.zols.datastore.model.Entity;

@Service
public class DataStore {

    @Autowired
    private MongoOperations mongoOperation;

    @SuppressWarnings("unchecked")
    public <T> T create(Object object, Class<T> clazz) {
        mongoOperation.insert(object);
        return (T) object;
    }

    public <T> T read(String name, Class<T> clazz) {
        T object = mongoOperation.findById(name, clazz);
        return object;
    }

    @SuppressWarnings("unchecked")
    public <T> T update(Object object, Class<T> clazz) {
        mongoOperation.save(object);
        return (T) object;
    }

    public <T> T delete(String name, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        T object = mongoOperation.findAndRemove(query, clazz);
        return object;
    }

    public <T> Page<T> list(Pageable pageable, Class<T> clazz) {
        Query query = getListQuery(pageable);
        int totalRecords = (int) mongoOperation.count(query, clazz);
        if (totalRecords != 0) {
            Page<T> objects = new PageImpl<T>(mongoOperation.find(query, clazz), pageable, totalRecords);
            return objects;
        }
        return null;
    }

    private Query getListQuery(Pageable pageable) {
        Query query = new Query();
        if (pageable != null) {
            query.skip(pageable.getPageNumber() * pageable.getPageSize());
            query.limit(pageable.getPageSize());
        }
        return query;
    }

    public <T> List<T> list(Class<T> aClass) {
        Query query = getListQuery(null);
        int totalRecords = (int) mongoOperation.count(query, aClass);
        if (totalRecords != 0) {
            return mongoOperation.find(query, aClass);
        }
        return null;
    }
}
