package org.zols.documents.domain;

import javax.persistence.Id;

/**
 * Document Storage contains storage information of Documents
 */
public class DocumentRepository {

    /**
     * describes where the attributes to be stored
     */
    public static final String FILE_SYSTEM = "file";
    public static final String FTP = "ftp";

    @Id
    private String name;
    private String label;
    private String description;
    private String type;
    private String path;
    private String host;
    private String userName;
    private String password;
    private String rootFolder;
    private String baseUrl;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * get the user name given by user
     *
     * @return user name
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

    public String getPath() {
        return path;
    }

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
