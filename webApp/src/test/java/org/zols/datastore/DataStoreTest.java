package org.zols.datastore;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zols.datastore.model.Attribute;
import org.zols.datastore.model.Schema;
import org.zols.datastore.util.DynamicBeanGenerator;
import org.zols.test.loader.AnnotationConfigContextLoader;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, value = "org.zols.core.config.SpringMongoConfig")
@RunWith(SpringJUnit4ClassRunner.class)
public class DataStoreTest {

	@Autowired
	private DataStore dataStore;

	
	public void testSchemaCreate() {
		Schema schema = new Schema();
		schema.setName("employee");
		schema.setLabel("Employee");
		schema.setDescription("Describe an Employee of the Organization");
		
		Attribute attribute = null ;
		
		List<Attribute> attributes = new ArrayList<Attribute>(2);
		
		attribute = new Attribute() ;
		attribute.setDescription("Name of the Employee");
		attribute.setName("name");
		attribute.setType("String");
		attributes.add(attribute);
		
		attribute = new Attribute() ;
		attribute.setDescription("Age of the Employee");
		attribute.setName("age");
		attribute.setType("Integer");
		attributes.add(attribute);
		
		schema.setAttributes(attributes);
		
		dataStore.create(schema,Schema.class);
		System.out.println("dataStore " + schema.getLabel());
	}
	
        @Test
	public void testMethod() {
            
        }
	public void testCreateBean() throws IntrospectionException, InstantiationException, IllegalAccessException {
		Schema schema = dataStore.read("employee", Schema.class) ;
		Object object = DynamicBeanGenerator.generate(schema);
		System.out.println(object);
	}

}
