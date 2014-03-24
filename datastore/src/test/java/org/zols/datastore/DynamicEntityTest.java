/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import org.zols.datastore.config.DataStoreConfiguration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zols.datastore.domain.Attribute;
import org.zols.datastore.domain.BaseObject;
import org.zols.datastore.domain.Entity;

/**
 * Test cases that test a Static Entity. Dynamic Entities are those which are
 * configured they control panel. Technically they are proxy beans generated
 * with CGLib
 */
@ContextConfiguration(classes = {DataStoreConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class DynamicEntityTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DynamicEntityTest.class);

    @Autowired
    private DataStore dataStore;

    /**
     * Dynamic Object Created
     */
    private BaseObject baseObject;

    /**
     * Class Of the Dynamic Object Created
     */
    private Class baseObjectClass;

    /**
     * Basic Entity to test
     */
    private Entity entity;
    
    @Autowired
    private ExpressionParser expressionParser;

    @Before
    public void beforeTest() throws InstantiationException, IllegalAccessException {
        entity = getBasicEntity();
        dataStore.create(entity, Entity.class);
        baseObjectClass = dataStore.getBeanClass(entity.getName());        
        baseObject = (BaseObject) baseObjectClass.newInstance();
        Expression expression = expressionParser.parseExpression("count");        
        expression.setValue(baseObject, 20);        
        dataStore.create(baseObject, baseObjectClass);       
    }

    @Test
    public void testCreate() {
        Assert.assertNotNull("Base Object created",baseObject.getName());
    }

    @After
    public void afterTest() {
        LOGGER.info("Deleting 'Dynamic' Objects", entity.getName());
        dataStore.delete(baseObject.getName(), baseObjectClass);
        LOGGER.info("Deleted 'Dynamic' Objects", entity.getName());
        LOGGER.info("Deleting 'basic' Entity", entity.getName());
        dataStore.delete(entity.getName(), Entity.class);
        LOGGER.info("Deleted 'basic' Entity", entity.getName());
    }

    private Entity getBasicEntity() {
        entity = new Entity();
        entity.setName("basic");
        entity.setLabel("Basic Entity");
        entity.setDescription("Describe an Basic Entity");
        Attribute attribute;
        List<Attribute> attributes = new ArrayList<Attribute>(1);
        attribute = new Attribute();
        attribute.setDescription("Count is an Integer");
        attribute.setName("count");
        attribute.setType("Integer");
        attributes.add(attribute);
        entity.setAttributes(attributes);
        entity.setCreatedDate(new Date());
        return entity;
    }
}
