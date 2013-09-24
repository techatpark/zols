package org.zols.datastore.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import org.zols.datastore.model.Schema;

/**
 * <code>DynamicBeanGenerator</code>
 *
 * 
 */
public class DynamicBeanGenerator {

    public static Class<?> createBeanClass(
            /* fully qualified class name */
            final String className,
            /* bean properties, name -> type */
            final Map<String, Class<?>> properties) {

        final BeanGenerator beanGenerator = new BeanGenerator();     

        /* use our own hard coded class name instead of a real naming policy */
        beanGenerator.setNamingPolicy(new NamingPolicy() {
            @Override
            public String getClassName(final String prefix,
                    final String source, final Object key, final Predicate names) {
                return className;
            }
        });
        
        BeanGenerator.addProperties(beanGenerator, properties);
        return (Class<?>) beanGenerator.createClass();
    }

    public static void main(final String[] args) throws Exception {
        final Map<String, Class<?>> properties =
                new HashMap<String, Class<?>>();
        properties.put("foo", Integer.class);
        properties.put("bar", String.class);
        properties.put("baz", int[].class);
        properties.put("schemas", Schema[].class);

        final Class<?> beanClass =
                createBeanClass("some.ClassName", properties);
        System.out.println(beanClass);
        for (final Method method : beanClass.getDeclaredMethods()) {
            System.out.println(method);
        }

    }
}
