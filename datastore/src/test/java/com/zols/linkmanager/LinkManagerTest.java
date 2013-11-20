/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zols.linkmanager;

import com.zols.datastore.config.DataStoreConfiguration;
import com.zols.linkmanager.domain.Category;
import com.zols.linkmanager.domain.Link;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {DataStoreConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LinkManagerTest {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(LinkManagerTest.class);
    
    @Autowired
    private LinkManager linkmanager;
    
    private static final String categoryName = "newcategory";
    private static final String parentName = "categoryparent";
    private static final String linkName = "newlink";
    
    @Test
    public void createCatyegory() {
        LOGGER.debug("myTest1");
    }
    
    @Test
    public void getCatyegory() {
        List category1 = linkmanager.categoryList();
        LOGGER.debug("getCategory" + category1);
    }
    
    @Test
    public void getCatgory() {
        Category cate = linkmanager.getCategory(categoryName);
        LOGGER.debug("Category" + cate);
    }
    
    @Test
    public void updateCatgory() {
        Category category = new Category();
        category.setName(categoryName);
        category.setLabel("Labe");
        category.setDescription("This is for testing");
        linkmanager.update(category);
        LOGGER.debug(category.getLabel());
    }
    
    @Test
    public void listByCategory() {
        List list = linkmanager.listFirstLevelByCategory(categoryName);
        System.out.println("List By Category===" + list);
    }
    
    @Test
    public void listByParent() {
        List list = linkmanager.listByParent(parentName);
        System.out.println("list By Parent" + list);
    }
    
    @Test
    public void getLink() {
        Link link = linkmanager.getLink(linkName);
        System.out.println("Get link" + link);
    }
    

    @Test
    public void deleteCategory() {
        System.out.println("Delete Category");
        linkmanager.deleteCategory(categoryName);
    }
    
    @Before
    public void before() {
        LOGGER.debug("Before");
        Category category = new Category();
        category.setName(categoryName);
        category.setLabel("Label1");
        category.setDescription("This is for testing");
        linkmanager.add(category);
        
        Link link = new Link();
        link.setName(linkName);
        link.setLabel("Sample link label");
        link.setCategoryName(categoryName);
        link.setDescription("Sample link Descriptions");
        link.setParentLinkName("test");
        linkmanager.add(link);
        
    }
    
    @After
    public void after() {
        LOGGER.debug("After");
        linkmanager.deleteCategory(categoryName);
        linkmanager.delete(linkName);
        System.out.println("After");
    }
    
    @AfterClass
    public static void afterClass() {
        LOGGER.debug("afterClass");
    }
    
    @BeforeClass
    public static void beforeClass() {
        LOGGER.debug("beforeClass");
    }
    
}
