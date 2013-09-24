package org.zols.datastore.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zols.core.web.config.JacksonObjectMapper;
import org.zols.datastore.DataStore;
import org.zols.datastore.model.Attribute;
import org.zols.datastore.model.BaseObject;
import org.zols.datastore.model.Entity;

@Service
public class DynamicBeanGenerator {
    
    @Autowired
    private DataStore dataStore;
    
    @Autowired
    private JacksonObjectMapper jacksonObjectMapper ;
    
    private final String getClassName(final String entityName) {
        return entityName+"_";
    }
    
    public final Class<?> getBeanClass(
            final String entityName) {
        Class clazz = null;
        try {
            clazz = Class.forName(getClassName(entityName));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DynamicBeanGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(clazz == null) {
            clazz = createBeanClass(entityName);
        }
        return clazz;        
    }

    private Class<?> createBeanClass(
            final String entityName) {
        final Entity entity = dataStore.read(entityName, Entity.class);
        final BeanGenerator beanGenerator = new BeanGenerator();
        final String className = getClassName(entityName);
        beanGenerator.setSuperclass(BaseObject.class);
        /* use our own hard coded class name instead of a real naming policy */
        beanGenerator.setNamingPolicy(new NamingPolicy() {
            @Override
            public String getClassName(final String prefix,
                    final String source, final Object key, final Predicate names) {
                return className;
            }
        });
        
        BeanGenerator.addProperties(beanGenerator, getProperties(entity));
        return (Class<?>) beanGenerator.createClass();
    }
    
    public BaseObject getBaseObject(String entityName,String jsonString) {
        final Class<?> beanClass =
                createBeanClass(entityName);
        BaseObject baseObject = null;
        try {
            baseObject = (BaseObject) jacksonObjectMapper.readValue(jsonString, beanClass);
        } catch (Exception ex) {
            Logger.getLogger(DynamicBeanGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return baseObject ;
    }
    
    private Map<String, Class<?>> getProperties(final Entity entity) {
        final Map<String, Class<?>> properties =
                new HashMap<String, Class<?>>();
        for (Attribute attribute : entity.getAttributes()) {
            if(attribute.getType().equals("Integer")) {
                properties.put(attribute.getName(), Integer.class);
            }
            else {
                properties.put(attribute.getName(), String.class);
            }
        }
                
        return properties;
    } 

    public static void main(final String[] args) throws Exception {
        final Map<String, Class<?>> properties =
                new HashMap<String, Class<?>>();
        properties.put("foo", Integer.class);
        properties.put("bar", String.class);
        properties.put("baz", int[].class);
        properties.put("entities", Entity[].class);

        

    }
}
