/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documentmanager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zols.documentmanager.domain.Document;
import org.zols.documentmanager.domain.DocumentStorage;

/**
 *
 * @author navin_kr
 */
@Service
public class DocumentManager {
    
    @Autowired
    private DocumentStorageManager documentStorageManager;

    public void upload(Document document, String documentPath) throws IOException {
        List<MultipartFile> multipartFiles = document.getFiles();
        if (null != multipartFiles && multipartFiles.size() > 0) {
            for (MultipartFile multipartFile : multipartFiles) {
                //Handle file content - multipartFile.getInputStream()
                byte[] bytes = multipartFile.getBytes();
                BufferedOutputStream stream
                        = new BufferedOutputStream(new FileOutputStream(new File(documentPath + File.separator + multipartFile.getOriginalFilename())));
                stream.write(bytes);
                stream.close();
            }
        }
    }
    
    /**
     * Creates a directory in the given document path
     * @param directoryName the name of the directory to be created
     * @param documentStorageName the path in which the directory will be created
     */
    public  void createDirectory(String directoryName, String documentStorageName){
        DocumentStorage documentStorage = documentStorageManager.get(documentStorageName);
        File newDir = new File(documentStorage.getPath()+File.separator+directoryName);
        newDir.mkdirs();
    }
    
    public List<Document> list(String documentStorageName,String folderPath) {
        DocumentStorage documentStorage = documentStorageManager.get(documentStorageName);
        String path = documentStorage.getPath()+(null==folderPath ? "" : (File.separator+folderPath));
        List<Document> documents = new ArrayList<Document>();
        Document document = null;
        for(File innerFile :new File(path).listFiles()){
            document = new Document();
            document.setFileName(innerFile.getName());
            document.setIsDir(innerFile.isDirectory());
            document.setPath(innerFile.getPath());
            documents.add(document);
        }
        return documents;
    }

}
