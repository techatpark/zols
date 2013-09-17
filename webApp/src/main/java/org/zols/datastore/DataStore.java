package org.zols.datastore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.zols.datastore.model.ZolsPage;

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
	public  <T> T update(Object object, Class<T> clazz) {
		mongoOperation.save(object);
		return (T) object;
	}

	public <T> T  delete(String name, Class<T> clazz) {
		Query query = new Query();
		query.addCriteria(Criteria.where("name").is(name));
		T object = mongoOperation.findAndRemove(query, clazz);
		return object;
	}

	public <T> Page<T> list(Integer page, Integer per_page, Class<T> clazz) {

		Query query = new Query();

		if (page != null) {
			query.skip((page - 1) * per_page);
		}

		query.limit(per_page);

		int totalRecords = (int) mongoOperation.count(query, clazz);
		if (totalRecords != 0) {
			Page<T> objects = new ZolsPage<T>(0, per_page, totalRecords,
					mongoOperation.find(query, clazz));
			return objects;
		}

		return null;
	}

}
