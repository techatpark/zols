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
import org.zols.documentmanager.domain.Upload;

/**
 * DocumentManager provides methods to access and add files to the file systems.
 *
 */
@Service
public class DocumentManager {

    @Autowired
    private DocumentStorageManager documentStorageManager;

    /**
     * Upload documents
     *
     * @param upload documents to be uploaded
     * @param documentPath source path of the document
     * @throws IOException
     */
    public void upload(Upload upload, String documentPath) throws IOException {
        List<MultipartFile> multipartFiles = upload.getFiles();
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
     * 
     *@param documentStorageName the path in which the directory will be created
     *@param directoryName the name of the directory to be created
     */
    public void createDirectory(String documentStorageName, String directoryName) {
        createDirectory(documentStorageName, null, directoryName);
    }
    
    /**
     * Creates a directory in the given document path
     * 
     * @param documentStorageName the path in which the directory will be created
     * @param rootFolderPath folder where we need to create directory
     * @param directoryName the name of the directory to be created
     */
    public void createDirectory(String documentStorageName, String rootFolderPath,String directoryName) {
        DocumentStorage documentStorage = documentStorageManager.get(documentStorageName);
        String folderPath = documentStorage.getPath();
        if( rootFolderPath != null && rootFolderPath.trim().length() != 0) {
            folderPath = folderPath + File.separator + rootFolderPath;
        }
        File newFolder = new File(folderPath + File.separator + directoryName);
        newFolder.mkdirs();
    }

    /**
     * List all the files in the current directory
     *
     * @param documentStorageName type of storage
     * @param folderPath the directory to list the files
     * @return
     */
    public List<Document> list(String documentStorageName, String folderPath) {
        DocumentStorage documentStorage = documentStorageManager.get(documentStorageName);
        String path = documentStorage.getPath() ;
        if(folderPath !=null && folderPath.trim().length() !=0 ) {
            path = path + File.separator + folderPath;
        }
        List<Document> documents = new ArrayList<Document>();
        Document document;
        for (File innerFile : new File(path).listFiles()) {
            document = new Document();
            document.setStorageName(documentStorageName);
            document.setFileName(innerFile.getName());
            document.setIsDir(innerFile.isDirectory());
            document.setPath(innerFile.getPath());
            documents.add(document);
        }
        return documents;
    }
    
    /**
     * List all the files in the current directory
     *
     * @param documentStorageName type of storage
     * @param rootFolderPath folder where we need to create directory
     * @param folderPath the directory to list the files
     * @return
     */
    public List<Document> list(String documentStorageName, String rootFolderPath,String folderPath) {
        DocumentStorage documentStorage = documentStorageManager.get(documentStorageName);
        String path = documentStorage.getPath() ;
        if(folderPath !=null && folderPath.trim().length() !=0 ) {
            path = path + File.separator + folderPath;
        }
        List<Document> documents = new ArrayList<Document>();
        Document document;
        for (File innerFile : new File(path).listFiles()) {
            document = new Document();
            document.setStorageName(documentStorageName);
            document.setFileName(innerFile.getName());
            document.setIsDir(innerFile.isDirectory());
            document.setPath(innerFile.getPath());
            documents.add(document);
        }
        return documents;
    }

}
