/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documentmanager.domain;

import org.springframework.data.annotation.Id;

/**
 * Describes the attributes that should be included in document storage
 *
 * @author Praveen pvn.
 */
public class DocumentStorage {

    /**
     * describes where the attributes to be stored
     *
     */
    public static final String FILE_SYSTEM = "file";
    public static final String FTP = "ftp";
    public static final String CLASSPATH = "classpath";
    
    /**
     * attirbutes of the document storage usecase
     */
    @Id
    private String name;
    private String description;
    private String type;
    private String path;
    private String host;
    private String userName;
    private String password;
    private String rootFolder;
    private String baseUrl;

    /**
     * *
     * get the host name
     *
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * set host name
     *
     * @param host set host name as the user's current host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * get the user name given by user
     *
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * set user name given by user
     *
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    /**
     * get path
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * set path where to be stored
     *
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    
}
