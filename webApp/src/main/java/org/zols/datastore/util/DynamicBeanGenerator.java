package org.zols.datastore.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zols.datastore.DataStore;
import org.zols.datastore.model.Attribute;
import org.zols.datastore.model.Entity;

@Service
public class DynamicBeanGenerator {
    
    @Autowired
    private DataStore dataStore;

    public final Class<?> createBeanClass(
            final String entityName) {
        final Entity entity = dataStore.read(entityName, Entity.class);
        final BeanGenerator beanGenerator = new BeanGenerator();     

        /* use our own hard coded class name instead of a real naming policy */
        beanGenerator.setNamingPolicy(new NamingPolicy() {
            @Override
            public String getClassName(final String prefix,
                    final String source, final Object key, final Predicate names) {
                return entity.getName();
            }
        });
        
        BeanGenerator.addProperties(beanGenerator, getProperties(entity));
        return (Class<?>) beanGenerator.createClass();
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
