package org.zols.datastore;

import org.zols.datastore.config.DataStoreConfiguration;
import org.zols.datastore.domain.Attribute;
import org.zols.datastore.domain.Criteria;
import org.zols.datastore.domain.Entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test cases that test a Static Entity.
 * Static Entities are those for which there is a POJO available at compilation time.(For E.g Entity.java or Link.java)
 */
@ContextConfiguration(classes = {DataStoreConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class StaticEntityTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(StaticEntityTest.class);

    @Autowired
    private DataStore dataStore;

    /**
     * Basic Entity to test
     */
    private Entity entity;

    @Before
    public void beforeTest() {
        entity = getBasicEntity();
        dataStore.create(entity, Entity.class);
    }

    @Test
    public void testBasicEntityCreate() {
        LOGGER.info("testing Entity Create", entity.getName());
        Assert.assertTrue("basic entity should have name", entity.getName() != null);
        LOGGER.info("tested Entity Create", entity.getName());
    }
    
    @Test
    public void testBasicEntityRead() {
        LOGGER.info("testing Entity Read", entity.getName());
        Entity entityResult = dataStore.read(entity.getName(), Entity.class);
        Assert.assertEquals("basic entity has to be present with Label 'Basic Entity'", "Basic Entity", entityResult.getLabel());
        LOGGER.info("tested Entity Read", entity.getName());
    }

    @Test
    public void testSearchEntity() {
        LOGGER.info("testing Entity Read", entity.getName());
        Entity searchEntity = new Entity();
        searchEntity.setName(entity.getName());
        List entitiesResult = dataStore.listByExample(searchEntity);
        Assert.assertEquals("There should be one Entity in the db with name " + entity.getName(), 1, entitiesResult.size());
        LOGGER.info("tested Entity Read", entity.getName());
    }

    @Test
    public void testSearchAndRemove() {
        LOGGER.info("testing Entity Read", entity.getName());
        Entity searchEntity = new Entity();
        searchEntity.setName(entity.getName());
        dataStore.deleteByExample(searchEntity);
        List entitiesResult = dataStore.listByExample(searchEntity);
        Assert.assertEquals("There should be one Entity in the db with name " + entity.getName(), 0, entitiesResult.size());
        LOGGER.info("tested Entity Read", entity.getName());
    }

    
    public void testGetByCriteria() {
        LOGGER.info("testing Entity Read", entity.getName());

        List<Criteria> criterias = new ArrayList<Criteria>();
        criterias.add(new Criteria("createdDate", Criteria.Type.LESSER_THAN, new Date()));

        List<Entity> entitiesResult = dataStore.list(criterias,Entity.class);
        Assert.assertEquals("There should be one Entity lesser than this date " + entity.getName(), 1, entitiesResult.size());
        LOGGER.info("tested Entity Read", entity.getName());
    }

    @After
    public void afterTest() {
        LOGGER.info("Deleting 'basic' Entity", entity.getName());
        dataStore.delete(entity.getName(), Entity.class);
        LOGGER.info("Deleted 'basic' Entity", entity.getName());
    }

    /**
     * Entity Object with which we conduct all basic test cases.
     *
     * @return
     */
    private Entity getBasicEntity() {
        entity = new Entity();
        //entity.setName("Basic");
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
