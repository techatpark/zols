package com.zols.templatemanager;

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

import com.zols.datastore.DataStore;
import com.zols.datastore.domain.NameLabel;
import com.zols.templatemanager.domain.TemplateRepository;
import com.zols.utils.GenericExtFilter;

@Service
public class TemplateRepositoryManager {

    @Autowired
    private DataStore dataStore;

    public TemplateRepository add(TemplateRepository templateRepository) {
        TemplateRepository repository = dataStore.create(templateRepository, TemplateRepository.class);
        return repository;
    }

    public void update(TemplateRepository templateRepository) {
        dataStore.update(templateRepository, TemplateRepository.class);
    }

    public void deleteTemplateRepository(String templateRepositoryName) {
        dataStore.delete(templateRepositoryName, TemplateRepository.class);
    }

    public TemplateRepository getTemplateRepository(String templateRepositoryName) {
        return dataStore.read(templateRepositoryName, TemplateRepository.class);
    }

    public Page<TemplateRepository> templateRepositoryList(Pageable page) {
        return dataStore.list(page, TemplateRepository.class);
    }

    public List<NameLabel> templatePaths() throws IOException {
        List<NameLabel> nameLabels = new ArrayList<NameLabel>();
        List<TemplateRepository> templateRepositorys = dataStore.list(TemplateRepository.class);
        for (TemplateRepository templateRepository : templateRepositorys) {
            if (templateRepository.getType().equals(TemplateRepository.FILE_SYSTEM)) {
                populateTemplatesFromFileSystem(nameLabels, new File(templateRepository.getPath()), null);
            } else {
                populateTemplatesFromFTP(nameLabels, templateRepository, null);
            }
        }
        return nameLabels;
    }

    private void populateTemplatesFromFTP(List<NameLabel> nameLabels, TemplateRepository templateRepository, String path) throws IOException {

        String filePath = null;
        if (path == null) {
            path = templateRepository.getRootFolder();
        }
        if (path == null) {
            path = "";
        }
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(templateRepository.getHost());
        ftpClient.login(templateRepository.getUserName(), templateRepository.getPassword());

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
                populateTemplatesFromFTP(nameLabels, templateRepository, path + File.separator + file.getName());
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
