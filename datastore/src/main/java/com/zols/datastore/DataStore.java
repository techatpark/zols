package com.zols.datastore;

import com.zols.datastore.domain.BaseObject;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Persistence Utility for Static and Dynamic Objects.
 * @author Sathish Kumar Thiyagarajan
 */
public abstract class DataStore {

    /**
     * Creates a new object
     * @param <T> Type of the Object
     * @param object Object to be created
     * @param clazz Class of the Object to be created
     * @return created object
     */
    public abstract <T> T create(Object object, Class<T> clazz);

    /**
     * reads an Object with given name
     * @param <T> Type of the Object
     * @param name name of the Object
     * @param clazz Class of the Object to be read
     * @return object with given name
     */
    public abstract <T> T read(String name, Class<T> clazz);

    public abstract <T> T update(Object object, Class<T> clazz);

    public abstract <T> T delete(String name, Class<T> clazz);

    public abstract <T> Page<T> list(Pageable pageable, Class<T> clazz);

    public abstract <T> List<T> list(Class<T> aClass);
    
    /**
     * Creates a new object
     * @param entityName name of the entity to be created
     * @param object Object to be created
     * @return created object
     */
    public abstract BaseObject create(String entityName,BaseObject object);
}
