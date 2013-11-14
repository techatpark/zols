/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zols.linkmanager;

import com.zols.datastore.util.LinkManager;
import com.zols.datastore.DataStore;
import com.zols.datastore.config.DataStoreConfiguration;
import com.zols.linkmanager.domain.Category;
import org.junit.Assert;
import org.junit.Before;
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
    public void testLinkManagerCreate() {

        LOGGER.info("tested Category Create");
    }

}
