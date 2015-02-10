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
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zols.datatore.exception.DataStoreException;
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
     * @param documentRepositoryName name of the repository
     * @param upload documents to be uploaded
     * @param rootFolderPath source path of the document
     */
    public void upload(String documentRepositoryName, Upload upload, String rootFolderPath) throws DataStoreException {
        DocumentRepository documentRepository = documentRepositoryService.read(documentRepositoryName);
        String folderPath = documentRepository.getPath();
        if (rootFolderPath != null && rootFolderPath.trim().length() != 0) {
            folderPath = folderPath + File.separator + rootFolderPath;
        }
        List<MultipartFile> multipartFiles = upload.getFiles();
        if (null != multipartFiles && multipartFiles.size() > 0) {
            for (MultipartFile multipartFile : multipartFiles) {
                //Handle file content - multipartFile.getInputStream()
                byte[] bytes;
                try {
                    bytes = multipartFile.getBytes();

                    BufferedOutputStream stream
                            = new BufferedOutputStream(new FileOutputStream(new File(folderPath + File.separator + multipartFile.getOriginalFilename())));
                    stream.write(bytes);
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
        DocumentRepository documentRepository = documentRepositoryService.read(documentRepositoryName);
        String folderPath = documentRepository.getPath();
        if (rootFolderPath != null && rootFolderPath.trim().length() != 0) {
            folderPath = folderPath + File.separator + rootFolderPath;
        }
        File newFolder = new File(folderPath + File.separator + directoryName);
        newFolder.mkdirs();
    }

    public void delete(String documentRepositoryName, String filePath) throws DataStoreException {
        DocumentRepository documentRepository = documentRepositoryService.read(documentRepositoryName);
        String path = documentRepository.getPath();
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
            document.setPath(innerFile.getPath().replaceAll(new File(path).getPath(), ""));
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
        DocumentRepository documentRepository = documentRepositoryService.read(documentRepositoryName);
        String path = documentRepository.getPath();
        if (folderPath != null && folderPath.trim().length() != 0) {
            path = path + File.separator + folderPath;
        }

        LOGGER.info("Listing all nested documents inside {}", path);

        return getFiles(path, documentRepositoryName, new File(path));
    }

    private List<Document> getFiles(String path, String documentRepositoryName, File folder) {
        Document document;
        String filePath ;
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
