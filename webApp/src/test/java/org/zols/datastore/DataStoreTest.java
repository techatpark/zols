package org.zols.datastore;

import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zols.datastore.model.Attribute;
import org.zols.datastore.model.BaseObject;
import org.zols.datastore.model.Entity;
import org.zols.datastore.util.DynamicBeanGenerator;
import org.zols.test.loader.AnnotationConfigContextLoader;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, value = {"org.zols.core.web.config.ControllerConfiguration"})
@RunWith(SpringJUnit4ClassRunner.class)
public class DataStoreTest {

    @Autowired
    private DataStore dataStore;
    
    @Autowired
    private DynamicBeanGenerator beanGenerator;

    @Test
    public void testentityCreate() {

        Entity entity = dataStore.read("basic", Entity.class);
        if (entity == null) {
            entity = new Entity();
            entity.setName("basic");
            entity.setLabel("Basic Entity");
            entity.setDescription("Describe an Basic Entity");

            Attribute attribute = null;

            List<Attribute> attributes = new ArrayList<Attribute>(1);

            attribute = new Attribute();
            attribute.setDescription("Count is an Integer");
            attribute.setName("count");
            attribute.setType("Integer");
            attributes.add(attribute);

            entity.setAttributes(attributes);
            dataStore.create(entity, Entity.class);
        }
    }

    @Test
    public void testCreateBean() throws IntrospectionException, InstantiationException, IllegalAccessException {
        final Class<?> beanClass =
                beanGenerator.getBeanClass("basic");
        System.out.println(beanClass);
        for (final Method method : beanClass.getMethods()) {
            System.out.println(method);
        }
    }
    
    @Test
    public void testCreateDynamicObjectFromJSON() {
        String jsonString = "{ \"name\": \"new\",\"count\": 2 }";        
        BaseObject baseObject = beanGenerator.getBaseObject("basic", jsonString);
        System.out.println(baseObject.getName());
    }
}
