package org.zols.documents.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Id;

/**
 * Document Storage contains storage information of Documents.
 */
public class DocumentRepository {

    /**
     * describes where the attributes to be stored.
     */
    public static final String FILE_SYSTEM = "file";
    /**
     * describes where the attributes to be stored.
     */
    public static final String FTP = "ftp";

    /**
     * Tells the name.
     */
    @Id
    @NotEmpty
    private String name;

    /**
     * Tells the label.
     */
    @NotEmpty
    private String label;

    /**
     * Tells the description.
     */
    private String description;

    /**
     * Tells the type.
     */
    private String type;
    /**
     * Tells the path.
     */
    private String path;
    /**
     * Tells the host.
     */
    private String host;
    /**
     * Tells the userName.
     */
    private String userName;
    /**
     * Tells the password.
     */
    private String password;
    /**
     * Tells the rootFolder.
     */
    private String rootFolder;
    /**
     * Tells the baseUrl.
     */
    @URL
    private String baseUrl;

    /**
     * gets label.
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }
    /**
     * sets a label.
     * @param aLabel aLabel
     */
    public void setLabel(final String aLabel) {
        this.label = aLabel;
    }
    /**
     * gets host.
     *
     * @return host
     */
    public String getHost() {
        return host;
    }
    /**
     * sets host.
     * @param anHost an host
     */
    public void setHost(final String anHost) {
        this.host = anHost;
    }

    /**
     * gets aUserName.
     *
     * @return aUserName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * sets aUserName.
     *
     * @param aUserName aUserName
     */
    public void setUserName(final String aUserName) {
        this.userName = aUserName;
    }
    /**
     * gets password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }
    /**
     * sets rootFolder.
     *
     * @param aPassword  aPassword
     */
    public void setPassword(final String aPassword) {
        this.password = aPassword;
    }
    /**
     * gets rootFolder.
     *
     * @return rootFolder
     */
    public String getRootFolder() {
        return rootFolder;
    }
    /**
     * sets rootFolder.
     *
     * @param aRootFolder a rootFolder
     */
    public void setRootFolder(final String aRootFolder) {
        this.rootFolder = aRootFolder;
    }
    /**
     * gets path.
     *
     * @return path
     */
    public String getPath() {
        return path;
    }
    /**
     * sets path.
     *
     * @param aPath a path
     */
    public void setPath(final String aPath) {
        this.path = aPath;
    }
    /**
     * gets name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }
    /**
     * sets name.
     *
     * @param theName the name
     */
    public void setName(final String theName) {
        this.name = theName;
    }
    /**
     * gets description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }
    /**
     * sets description.
     *
     * @param anDescription an description
     */
    public void setDescription(final String anDescription) {
        this.description = anDescription;
    }
    /**
     * gets type.
     *
     * @return type
     */
    public String getType() {
        return type;
    }
    /**
     * sets type.
     *
     * @param theType the type
     */
    public void setType(final String theType) {
        this.type = theType;
    }
    /**
     * gets baseUrl.
     *
     * @return baseUrl
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    /**
     * sets baseUrl.
     *
     * @param aBaseUrl  baseUrl
     */
    public void setBaseUrl(final String aBaseUrl) {
        this.baseUrl = aBaseUrl;
    }

}
