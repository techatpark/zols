/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zols.templatemanager;

import com.zols.datastore.config.DataStoreConfiguration;
import com.zols.datastore.domain.NameLabel;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {DataStoreConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class TemplateRepositoryManagerTest {

    @Autowired
    private TemplateRepositoryManager templateRepositoryManager;


    @Test
    public void testTemplatePath() throws IOException {
        List<NameLabel> templatePaths = templateRepositoryManager.templatePaths() ;
        for (NameLabel nameLabel : templatePaths) {
            System.out.println(nameLabel.getName() + "=" + nameLabel.getLabel());
        }
    }
}
