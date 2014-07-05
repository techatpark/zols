/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documents.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zols.documents.domain.Document;
import org.zols.documents.domain.DocumentRepository;
import org.zols.documents.domain.Upload;

/**
 * DocumentService provides methods to access and add files to the file systems.
 *
 */
@Service
public class DocumentService {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocumentService.class);

    @Autowired
    private DocumentRepositoryService documentRepositoryService;

    /**
     * Upload documents
     *
     * @param documentRepository DocumentRepository in which the files have to
     * be uploaded
     * @param upload documents to be uploaded
     * @throws IOException
     */
    public void upload(DocumentRepository documentRepository, Upload upload) throws IOException {
        upload(documentRepository, upload, null);
    }

    /**
     * Upload documents
     *
     * @param documentRepository DocumentRepository in which the files have to
     * be uploaded
     * @param upload documents to be uploaded
     * @param rootFolderPath source path of the document
     * @throws IOException
     */
    public void upload(DocumentRepository documentRepository, Upload upload, String rootFolderPath) throws IOException {
        String folderPath = documentRepository.getPath();
        if (rootFolderPath != null && rootFolderPath.trim().length() != 0) {
            folderPath = folderPath + File.separator + rootFolderPath;
        }
        List<MultipartFile> multipartFiles = upload.getFiles();
        if (null != multipartFiles && multipartFiles.size() > 0) {
            for (MultipartFile multipartFile : multipartFiles) {
                //Handle file content - multipartFile.getInputStream()
                byte[] bytes = multipartFile.getBytes();
                BufferedOutputStream stream
                        = new BufferedOutputStream(new FileOutputStream(new File(folderPath + File.separator + multipartFile.getOriginalFilename())));
                stream.write(bytes);
                stream.close();
            }
        }
    }

    /**
     * Creates a directory in the given document path
     *
     * @param documentRepositoryName the path in which the directory will be
     * created
     * @param directoryName the name of the directory to be created
     */
    public void createDirectory(String documentRepositoryName, String directoryName) {
        createDirectory(documentRepositoryName, null, directoryName);
    }

    /**
     * Creates a directory in the given document path
     *
     * @param documentRepositoryName the path in which the directory will be
     * created
     * @param rootFolderPath folder where we need to create directory
     * @param directoryName the name of the directory to be created
     */
    public void createDirectory(String documentRepositoryName, String rootFolderPath, String directoryName) {
        DocumentRepository documentRepository = documentRepositoryService.read(documentRepositoryName);
        String folderPath = documentRepository.getPath();
        if (rootFolderPath != null && rootFolderPath.trim().length() != 0) {
            folderPath = folderPath + File.separator + rootFolderPath;
        }
        File newFolder = new File(folderPath + File.separator + directoryName);
        newFolder.mkdirs();
    }

    /**
     * List all the files in the root directory
     *
     * @param documentRepositoryName type of storage
     * @return
     */
    public List<Document> list(String documentRepositoryName) {
        return list(documentRepositoryName, null);
    }

    /**
     * List all the files in the current directory
     *
     * @param documentRepositoryName type of storage
     * @param folderPath the directory to list the files
     * @return
     */
    public List<Document> list(String documentRepositoryName, String folderPath) {
        DocumentRepository documentRepository = documentRepositoryService.read(documentRepositoryName);
        String path = documentRepository.getPath();
        if (folderPath != null && folderPath.trim().length() != 0) {
            path = path + File.separator + folderPath;
        }
        
        LOGGER.info("Listing documents from file path {}", path);
        
        List<Document> documents = new ArrayList<>();
        Document document;
        for (File innerFile : new File(path).listFiles()) {
            document = new Document();
            document.setRepositoryName(documentRepositoryName);
            document.setFileName(innerFile.getName());
            document.setIsDir(innerFile.isDirectory());
            document.setPath(innerFile.getPath());
            documents.add(document);
        }
        return documents;
    }

}
