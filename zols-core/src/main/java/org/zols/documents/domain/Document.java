package org.zols.documents.domain;

public class Document {

    /**
     * Tells the repositoryName id.
     */
    private String repositoryName;
    /**
     * Tells the path.
     */
    private String path;

    /**
     * Tells the fileName.
     */
    private String fileName;

    /**
     * Tells the isDir.
     */
    private Boolean isDir;

    /**
     * gets Repository Name.
     *
     * @return getRepository Name
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    /**
     * sets Repository Name.
     *
     * @param anRepositoryName repository name
     */
    public void setRepositoryName(final String anRepositoryName) {
        this.repositoryName = anRepositoryName;
    }

    /**
     * gets file Name.
     *
     * @return file Name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * sets file Name.
     *
     * @param aFileName a fileName
     */
    public void setFileName(final String aFileName) {
        this.fileName = aFileName;
    }

    /**
     * gets dir.
     *
     * @return isDir dir
     */
    public Boolean isIsDir() {
        return isDir;
    }

    /**
     * sets dir.
     *
     * @param isADir is dir
     */
    public void setIsDir(final Boolean isADir) {
        this.isDir = isADir;
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


}
