/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documents.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zols.datatore.exception.DataStoreException;
import org.zols.documents.domain.Document;
import org.zols.documents.domain.DocumentRepository;
import org.zols.documents.domain.Upload;

/**
 * DocumentService provides methods to access and add files to the file systems.
 *
 */

public class DocumentService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocumentService.class);

    
    private final DocumentRepositoryService documentRepositoryService;

    public DocumentService(DocumentRepositoryService documentRepositoryService) {
        this.documentRepositoryService = documentRepositoryService;
    }
    
    

    /**
     * Upload documents
     *
     * @param documentRepositoryName name of the repository
     * @param upload documents to be uploaded
     * @param rootFolderPath source path of the document
     */
    
    public void upload(String documentRepositoryName, Upload upload, String rootFolderPath) throws DataStoreException {
        Optional<DocumentRepository> documentRepository = documentRepositoryService.read(documentRepositoryName);
        String folderPath = documentRepository.get().getPath();
        if (rootFolderPath != null && rootFolderPath.trim().length() != 0) {
            folderPath = folderPath + File.separator + rootFolderPath;
        }
        List<File> multipartFiles = upload.getFiles();
        if (null != multipartFiles && multipartFiles.size() > 0) {
            for (File multipartFile : multipartFiles) {
                //Handle file content - multipartFile.getInputStream()
                byte[] bytes;
                try {
                    
                    BufferedOutputStream stream
                            = new BufferedOutputStream(new FileOutputStream(new File(folderPath + File.separator + multipartFile.getAbsolutePath())));
                    
                    stream.close();
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(DocumentService.class.getName()).log(Level.SEVERE, null, ex);
                }
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
    
    public void createDirectory(String documentRepositoryName, String directoryName) throws DataStoreException {
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
    
    public void createDirectory(String documentRepositoryName, String rootFolderPath, String directoryName) throws DataStoreException {
        Optional<DocumentRepository> documentRepository = documentRepositoryService.read(documentRepositoryName);
        String folderPath = documentRepository.get().getPath();
        if (rootFolderPath != null && rootFolderPath.trim().length() != 0) {
            folderPath = folderPath + File.separator + rootFolderPath;
        }
        File newFolder = new File(folderPath + File.separator + directoryName);
        newFolder.mkdirs();
    }

    
    public void delete(String documentRepositoryName, String filePath) throws DataStoreException {
        Optional<DocumentRepository> documentRepository = documentRepositoryService.read(documentRepositoryName);
        String path = documentRepository.get().getPath();
        if (filePath != null && filePath.trim().length() != 0) {
            path = path + File.separator + filePath;
        }
        LOGGER.info("Deleting a file form {}", path);
        File file = new File(path);
        try {
            delete(file);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(DocumentService.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    /**
     * List all the files in the current directory
     *
     * @param documentRepositoryName type of storage
     * @param folderPath the directory to list the files
     * @return list of documents
     */
    
    public List<Document> list(String documentRepositoryName, String folderPath) throws DataStoreException {
        Optional<DocumentRepository> documentRepository = documentRepositoryService.read(documentRepositoryName);
        String path = documentRepository.get().getPath();
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
            document.setPath(innerFile.getPath().replaceAll(documentRepository.get().getPath(), ""));
            documents.add(document);
        }
        return documents;
    }

    /**
     * List all the files in the current directory
     *
     * @param documentRepositoryName type of storage
     * @param folderPath the directory to list the files
     * @return list of documents
     */
    
    public List<Document> listAll(String documentRepositoryName, String folderPath) throws DataStoreException {
        Optional<DocumentRepository> documentRepository = documentRepositoryService.read(documentRepositoryName);
        String path = documentRepository.get().getPath();
        if (folderPath != null && folderPath.trim().length() != 0) {
            path = path + File.separator + folderPath;
        }

        LOGGER.info("Listing all nested documents inside {}", path);

        return getFiles(path, documentRepositoryName, new File(path));
    }

    private List<Document> getFiles(String path, String documentRepositoryName, File folder) {
        Document document;
        String filePath;
        List<Document> documents = new ArrayList<>();
        if (folder.isDirectory()) {
            for (File innerFile : folder.listFiles()) {
                if (innerFile.isFile()) {
                    document = new Document();
                    document.setRepositoryName(documentRepositoryName);
                    document.setFileName(innerFile.getName());
                    filePath = innerFile.getPath();
                    document.setPath(filePath.substring(filePath.indexOf(new File(path).getPath())));
                    documents.add(document);
                } else {
                    documents.addAll(getFiles(path, documentRepositoryName, innerFile));
                }
            }
        }
        return documents;
    }

}
