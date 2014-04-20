package org.zols.documentmanager.domain;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class Document {

    private List<MultipartFile> files;

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }

    private String fileName;
    private boolean isDir;
    private String path;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isIsDir() {
        return isDir;
    }

    public void setIsDir(boolean isDir) {
        this.isDir = isDir;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        hash = 53 * hash + (this.isDir ? 1 : 0);
        hash = 53 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Document other = (Document) obj;
        if ((this.fileName == null) ? (other.fileName != null) : !this.fileName.equals(other.fileName)) {
            return false;
        }
        if (this.isDir != other.isDir) {
            return false;
        }
        if ((this.path == null) ? (other.path != null) : !this.path.equals(other.path)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Document{" + "fileName=" + fileName + ", isDir=" + isDir + ", path=" + path + '}';
    }
    
    

}
