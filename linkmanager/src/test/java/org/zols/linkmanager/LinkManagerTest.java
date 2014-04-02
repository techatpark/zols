/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.linkmanager;

import java.util.List;
import java.util.Map;

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
import org.zols.datastore.config.DataStoreConfiguration;
import org.zols.linkmanager.domain.Link;

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
            System.out.println("link name " + link.getName());
        }

    }

    @Test
    public void listByparent() {
        List<Link> links = linkmanager.listByParent("Parent1");
        for (Link link : links) {
            //System.out.println("link name " + link.getName());
        }

    }

    @Test
    public void deleteLink() {
        //linkmanager.delete("Parent1");
    }

    @Test
    public void deleteCategory() {
        //linkmanager.deleteCategory("header");
    }
    

    public void testAllicationLinks() {
        Map<String,List<Link>> applicationLinks = linkmanager.getApplicationLinks();
        for (Map.Entry<String, List<Link>> entry : applicationLinks.entrySet()) {
            String string = entry.getKey();
            List<Link> list = entry.getValue();
            for(Link childLink : list){
            	printLink(childLink, 0);
            }
            
            
        }
    }
    
    private void printLink(Link link,int index){
    	System.out.println(index + link.getName());
		if (link.getChildren() != null) {
			index ++;
			for (Link childLink : link.getChildren()) {
				printLink(childLink, index);
			}
		}
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
