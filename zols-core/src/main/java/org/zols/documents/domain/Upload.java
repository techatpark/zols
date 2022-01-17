package org.zols.documents.domain;

import java.io.File;
import java.util.List;

public class Upload {

    private List<File> files;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(final List<File> files) {
        this.files = files;
    }

}
