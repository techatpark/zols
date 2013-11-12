/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zols.datastore.util;

import com.zols.datastore.DataStore;
import com.zols.datastore.domain.Attribute;
import com.zols.datastore.domain.BaseObject;
import com.zols.datastore.domain.Entity;
import java.util.Date;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamicBeanGenerator {
    @Autowired
    private DataStore dataStore;
    public final Class<? extends BaseObject> getBeanClass(final String entityName) {
        Entity entity = dataStore.read(entityName, Entity.class);
        Class clazz;
        try {
            clazz = Class.forName(getClassName(entity.getName()));
        } catch (ClassNotFoundException ex) {
            clazz = createBeanClass(entity);
        }
        return clazz;
    }

    private Class<?> createBeanClass(
            final Entity entity) {
        final BeanGenerator beanGenerator = new BeanGenerator();
        final String className = getClassName(entity.getName());
        beanGenerator.setSuperclass(BaseObject.class);
        beanGenerator.setNamingPolicy(new NamingPolicy() {
            public String getClassName(String string, String string1, Object o, Predicate prdct) {
                return className;
            }
        });
        getProperties(beanGenerator, entity);
        return (Class<?>) beanGenerator.createClass();
    }

    private String getClassName(final String entityName) {
        return "ZOLS" + entityName + "BEAN";
    }

    private void getProperties(BeanGenerator beanGenerator, final Entity entity) {
        for (Attribute attribute : entity.getAttributes()) {
            if (attribute.getType().equals("Integer")) {
                beanGenerator.addProperty(attribute.getName(), Integer.class);
            }
            else if (attribute.getType().equals("Double")) {
                beanGenerator.addProperty(attribute.getName(), Double.class);
            }
            else if (attribute.getType().equals("Float")) {
                beanGenerator.addProperty(attribute.getName(), Float.class);
            }else if (attribute.getType().equals("Date")) {
                beanGenerator.addProperty(attribute.getName(), Date.class);
            }else {
                beanGenerator.addProperty(attribute.getName(), String.class);
            }
        }
    }
    
}
