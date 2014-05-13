/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.linkmanager;

import java.util.List;
import java.util.Map;
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
import org.zols.datastore.config.DataStoreConfiguration;
import org.zols.linkmanager.domain.Category;
import org.zols.linkmanager.domain.Link;

@ContextConfiguration(classes = {DataStoreConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LinkManagerTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(LinkManagerTest.class);

    @Autowired
    private LinkManager linkmanager;

    private Link link;
    private Category category;

    @Before
    public void before() {
        category = new Category();
        category.setName("header");
        category.setLabel("Header");
        linkmanager.add(category);

        link = new Link();
        link.setName("link");
        link.setLabel("Link");
        link.setCategoryName(category.getName());
        linkmanager.add(link);
    }

    
    @Test
    public void testApplicationLinks() {
        Map<String, List<Link>> createdLink = linkmanager.getApplicationLinks();
        Assert.assertNotNull("Created Link available", createdLink);
    }
    
    @Test
    public void testCreateLink() {
        Link createdLink = linkmanager.getLink(link.getName());
        Assert.assertNotNull("Created Link available", createdLink);
    }

    @Test
    public void testLinkUrl() {
        String urlTobeLinked = "http://www.google.com";
        linkmanager.linkUrl(link.getName(), urlTobeLinked);
        Link updatedLink = linkmanager.getLink(link.getName());
        Assert.assertEquals("Created Link available", urlTobeLinked ,updatedLink.getTargetUrl());
    }

    @After
    public void after() {
        linkmanager.deleteCategory(category.getName());
    }

}
