/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.util;

import org.zols.datastore.DataStore;
import org.zols.datastore.domain.Attribute;
import org.zols.datastore.domain.BaseObject;
import org.zols.datastore.domain.Entity;
import java.util.Date;
import net.sf.cglib.beans.BeanGenerator;
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
        beanGenerator.setNamingPolicy(new NamingPolicy(className));
        getProperties(beanGenerator, entity);
        return (Class<?>) beanGenerator.createClass();
    }

    private String getClassName(final String entityName) {
        return "ZOLS" + entityName + "BEAN";
    }

    private void getProperties(BeanGenerator beanGenerator, final Entity entity) {
        for (Attribute attribute : entity.getAttributes()) {
            if (attribute.getType().equals("String")) {
                beanGenerator.addProperty(attribute.getName(), String.class);
            } else if (attribute.getType().equals("BigText")) {
                beanGenerator.addProperty(attribute.getName(), String.class);
            }else if (attribute.getType().equals("Integer")) {
                beanGenerator.addProperty(attribute.getName(), Integer.class);
            } else if (attribute.getType().equals("Double")) {
                beanGenerator.addProperty(attribute.getName(), Double.class);
            } else if (attribute.getType().equals("Float")) {
                beanGenerator.addProperty(attribute.getName(), Float.class);
            } else if (attribute.getType().equals("Date")) {
                beanGenerator.addProperty(attribute.getName(), Date.class);
            } else if (attribute.getType().equals("Time")) {
                beanGenerator.addProperty(attribute.getName(), String.class);
            } else if (attribute.getType().equals("Boolean")) {
                beanGenerator.addProperty(attribute.getName(), Boolean.class);
            }else if (attribute.getType().equals("RichText")) {
                beanGenerator.addProperty(attribute.getName(), String.class);
            } else {
                if (attribute.isIsReference()) {
                    beanGenerator.addProperty(attribute.getName(), String.class);
                } else {
                    beanGenerator.addProperty(attribute.getName(), getBeanClass(attribute.getType()));
                }

            }
        }
    }

    private static final class NamingPolicy implements net.sf.cglib.core.NamingPolicy {

        private final String className;

        public NamingPolicy(String className) {
            this.className = className;
        }

        @Override
        public String getClassName(String prefix,
                String source,
                Object key,
                Predicate names) {
            return className;
        }
    }

}
