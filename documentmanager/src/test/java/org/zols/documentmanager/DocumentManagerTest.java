/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documentmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zols.datastore.config.DataStoreConfiguration;
import org.zols.documentmanager.domain.DocumentStorage;

@ContextConfiguration(classes = {DataStoreConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentManagerTest {

    @Autowired
    private DocumentManager documentManager;
    @Autowired
    private DocumentStorageManager documentStorageManager;


    public void beforeTest() {
        File file = new File(System.getProperty("java.io.tmpdir") + new Date().getTime());
        file.mkdirs();
        DocumentStorage documentStorage = new DocumentStorage();
        documentStorage.setType("file");
        documentStorage.setName("testDS");
        documentStorage.setPath(file.getAbsolutePath());
        documentStorageManager.add(documentStorage);
    }

    public void testCreateFolder() {
        documentManager.createDirectory("test", "zols");
    }

    public void afterTest() throws IOException {
        delete(new File(documentStorageManager.get("testDS").getPath()));
        documentStorageManager.delete("testDS");

    }

    void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

    @Test
    public void testListDir() {
//        documentManager.createDirectory("test\\test2", "testDS");
//        documentManager.createDirectory("test1", "testDS");
//        System.out.println(documentManager.list("testDS", "test"));
    }
}
