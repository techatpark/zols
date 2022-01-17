package org.zols.documents.domain;

public class Document {

    private String repositoryName;
    private String path;
    private String fileName;
    private Boolean isDir;

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(final String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public Boolean isIsDir() {
        return isDir;
    }

    public void setIsDir(final Boolean isDir) {
        this.isDir = isDir;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }


}
