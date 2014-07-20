/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.zols.datastore.domain.Category;
import org.zols.datastore.mongo.MongoDataStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author sathish_ku
 */
public class DataStoreTest {

    private final DataStore dataStore;

    public DataStoreTest() {
        dataStore = new MongoDataStore();
    }

    private Category category;

    /**
     * Test of create method, of class CategoryService.
     */
    @Test
    public void testCreate() {
        Category readCategory = dataStore.read(Category.class, category.getName());
        assertThat("Created Category ", (readCategory != null));
    }

    @Test
    public void testUpdate() {
        Category readCategory = dataStore.read(Category.class, category.getName());
        readCategory.setDescription("Description2");
        dataStore.update(readCategory);
        Category updatedCategory = dataStore.read(Category.class, readCategory.getName());
        assertThat("Updated Category ", updatedCategory.getDescription(), equalTo("Description2"));
    }

    @Test
    public void testList() {
       List<Category> categories = dataStore.list(Category.class);
       assertThat("Listed Categories ", categories.size(), equalTo(1));
    }

    @Before
    public void setUp() {
        category = new Category();
        category.setName("header");
        category.setLabel("Header");  
        dataStore.create(Category.class, category);
    }

    @After
    public void after() {
        dataStore.delete(Category.class, category.getName());
    }

}
