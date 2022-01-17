package org.zols.documents.domain;

import java.io.File;
import java.util.List;

public class Upload {

    /**
     * list the files.
     */
    private List<File> files;

    /**
     * gets files.
     *
     * @return files
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * sets path.
     *
     * @param anFiles files
     */
    public void setFiles(final List<File> anFiles) {
        this.files = anFiles;
    }

}
