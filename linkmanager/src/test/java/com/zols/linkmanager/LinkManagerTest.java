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
import java.util.StringTokenizer;
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

    @Test
    public void listByCategory() {
        List<Link> links = linkmanager.listFirstLevelByCategory("header");
        for (Link link : links) {
//            System.out.println("link name " + link.getName());
        }

    }

    @Test
    public void listByparent() {
        List<Link> links = linkmanager.listByParent("Parent1");
        for (Link link : links) {
            System.out.println("link name " + link.getName());
        }

    }

    @Test
    public void deleteLink() {
        //linkmanager.delete("Parent1");
    }

    @Test
    public void deleteCategory() {
        linkmanager.deleteCategory("header");
    }

    @Before
    public void before() {

    }

    @After
    public void after() {

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
