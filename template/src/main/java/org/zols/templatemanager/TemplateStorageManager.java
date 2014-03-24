package org.zols.templatemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.zols.datastore.DataStore;
import org.zols.datastore.domain.NameLabel;
import org.zols.templatemanager.domain.TemplateStorage;
import org.zols.templatemanager.util.GenericExtFilter;


@Service
public class TemplateStorageManager {

    @Autowired
    private DataStore dataStore;

    public TemplateStorage add(TemplateStorage templateStorage) {
        TemplateStorage Storage = dataStore.create(templateStorage, TemplateStorage.class);
        return Storage;
    }

    public void update(TemplateStorage templateStorage) {
        dataStore.update(templateStorage, TemplateStorage.class);
    }

    public void deleteTemplateStorage(String templateStorageName) {
        dataStore.delete(templateStorageName, TemplateStorage.class);
    }

    public TemplateStorage getTemplateStorage(String templateStorageName) {
        return dataStore.read(templateStorageName, TemplateStorage.class);
    }

    public Page<TemplateStorage> templateStorageList(Pageable page) {
        return dataStore.list(page, TemplateStorage.class);
    }

    public List<NameLabel> templatePaths() throws IOException {
        List<NameLabel> nameLabels = new ArrayList<NameLabel>();
        List<TemplateStorage> templateStorages = dataStore.list(TemplateStorage.class);
        for (TemplateStorage templateStorage : templateStorages) {
            if (templateStorage.getType().equals(TemplateStorage.FILE_SYSTEM)) {
                populateTemplatesFromFileSystem(nameLabels, new File(templateStorage.getPath()), null);
            } else {
                populateTemplatesFromFTP(nameLabels, templateStorage, null);
            }
        }
        return nameLabels;
    }

    private void populateTemplatesFromFTP(List<NameLabel> nameLabels, TemplateStorage templateStorage, String path) throws IOException {

        String filePath = null;
        if (path == null) {
            path = templateStorage.getRootFolder();
        }
        if (path == null) {
            path = "";
        }
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(templateStorage.getHost());
        ftpClient.login(templateStorage.getUserName(), templateStorage.getPassword());

        // lists files and directories in the current working directory
        FTPFile[] files = ftpClient.listFiles(path, new FTPFileFilter() {

            @Override
            public boolean accept(FTPFile file) {
                return ((!file.getName().equals("mobile") && !file.getName().equals("tablet")) && (file.getName().endsWith(".html")
                        || file.isDirectory()));
            }
        });

        for (FTPFile file : files) {

            if (file.isDirectory()) {
                populateTemplatesFromFTP(nameLabels, templateStorage, path + File.separator + file.getName());
            } else {
                filePath = path + File.separator + file.getName();

                NameLabel nameLabel = new NameLabel();
                nameLabel.setLabel(file.getName());
                nameLabel.setName(filePath.replaceFirst("\\\\", "").replaceAll("\\\\", "").replace(".html", ""));
                nameLabels.add(nameLabel);
            }

        }

        ftpClient.logout();
        ftpClient.disconnect();
    }

    private void populateTemplatesFromFileSystem(List<NameLabel> nameLabels, File dir, String rootPath) {
        GenericExtFilter filter = new GenericExtFilter(".html");
        if (rootPath == null) {
            rootPath = dir.getAbsolutePath();
        }
        rootPath = rootPath.replaceAll("\\\\", "/");

        if (dir.isDirectory()) {
            // list out all the file name and filter by the extension
            File[] list = dir.listFiles(filter);
            for (File file : list) {
                if (file.isDirectory()) {
                    populateTemplatesFromFileSystem(nameLabels, file, dir.getAbsolutePath());
                } else {
                    NameLabel nameLabel = new NameLabel();
                    nameLabel.setLabel(file.getName());
                    nameLabel.setName(file.getAbsolutePath().replaceAll("\\\\", "/").replaceFirst(rootPath, "").replaceAll(filter.getExt(), "").replaceFirst("/", ""));
                    nameLabels.add(nameLabel);
                }
            }
        }

    }

}
