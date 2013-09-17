package org.zols.datastore.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zols.datastore.model.Attribute;
import org.zols.datastore.model.SampleBean;
import org.zols.datastore.model.Schema;


/**
 * <code>DynamicBeanGenerator</code>
 * 
 * Given an interface, generate the associated bean WARNING. If your interface
 * implements a set but not a get it will be ignored Same thing for the other
 * methods of your interface which are not property methods.
 * 
 * 
 */
public class DynamicBeanGenerator {

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * <code>generate</code>
	 * 
	 * @param interfaceClass
	 *            The interface the generated bean will be based on
	 * @return a dynamic bean based on the interface
	 * @throws IntrospectionException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unchecked")
	public static Object generate(Schema schema) throws IntrospectionException, InstantiationException, IllegalAccessException {
		// Extract properties
		final List<Property> properties = getMethodProperties(schema);
		// Generate the bean
		final BeanGenerator bg = new BeanGenerator();


		PropertyDescriptor[] descriptorList = new PropertyDescriptor[schema.getAttributes().size()];
		int index= 0;
		for (Property property : properties) {
			descriptorList[index++] = new PropertyDescriptor(property.getFieldName(), SampleBean.class);
		}
		
		BeanGenerator.addProperties(bg, descriptorList);
		
		Class beanClass = (Class)  bg.createClass();
		
		return beanClass.newInstance();
	}

	/**
	 * <code>getMethodProperties</code>
	 * 
	 * @param methods
	 *            a set of methods
	 * @return a list of properties
	 */
	private static List<Property> getMethodProperties(Schema schema) {
		List<Property> properties = new ArrayList<Property>(schema
				.getAttributes().size());

		for (Attribute attribute : schema.getAttributes()) {
			properties.add(new Property(attribute.getName(), attribute
					.getType()));
		}

		return properties;
	}

	private static class Property {
		private String fieldName;
		private Class type;

		Property(String fieldName, String type) {
			this.fieldName = fieldName;
			if (type.equals("Integer")) {
				this.type = Integer.class;
			} else {
				this.type = String.class;
			}

		}

		String getFieldName() {
			return fieldName;
		}

		Class getType() {
			return type;
		}
	}

}
