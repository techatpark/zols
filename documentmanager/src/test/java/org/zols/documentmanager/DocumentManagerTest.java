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
import org.zols.datastore.config.DataStoreConfiguration;
import org.zols.documentmanager.domain.Document;
import org.zols.documentmanager.domain.DocumentStorage;

@ContextConfiguration(classes = {DataStoreConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentManagerTest {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocumentManagerTest.class);

    @Autowired
    private DocumentManager documentManager;
    @Autowired
    private DocumentStorageManager documentStorageManager;
    
    private static final String DOCUMENT_STORAGE_NAME = "testDS";

    @Before
    public void beforeTest() {        
        
        File file = new File(System.getProperty("java.io.tmpdir") + new Date().getTime());
        file.mkdirs();
        LOGGER.debug("Created a temporary Folder ", file.getAbsolutePath());
        
        DocumentStorage documentStorage = new DocumentStorage();
        documentStorage.setType("file");
        documentStorage.setName(DOCUMENT_STORAGE_NAME);
        documentStorage.setPath(file.getAbsolutePath());
        documentStorageManager.add(documentStorage);
        
        LOGGER.debug("Created a File based Document Storage ", documentStorage.getName());
    }

    @Test
    public void testCreateFolder() {
        documentManager.createDirectory(DOCUMENT_STORAGE_NAME, "NEW_DIR");
        List<Document> documents = documentManager.list(DOCUMENT_STORAGE_NAME, null);
        Assert.assertEquals("NEW_DIR",documents.get(0).getFileName());
    }
    
    @Test
    public void testCreateInnerFolder() {
        documentManager.createDirectory(DOCUMENT_STORAGE_NAME, "NEW_DIR");
        documentManager.createDirectory(DOCUMENT_STORAGE_NAME, "NEW_DIR","NEW_DIR2");
        List<Document> documents = documentManager.list(DOCUMENT_STORAGE_NAME, "NEW_DIR");
        Assert.assertEquals("NEW_DIR2",documents.get(0).getFileName());
    }

    @After
    public void afterTest() throws IOException {
        DocumentStorage documentStorage = documentStorageManager.get(DOCUMENT_STORAGE_NAME);
        File tempFolder = new File(documentStorage.getPath());
        
        delete(tempFolder);
        LOGGER.debug("Deleted the temporary Folder ", tempFolder.getAbsolutePath());
        
        documentStorageManager.delete(DOCUMENT_STORAGE_NAME);
        LOGGER.debug("Deleted the Document Storage ", DOCUMENT_STORAGE_NAME);

    }

    private void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

   
}
