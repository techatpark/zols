package com.zols.templatemanager;

import com.zols.datastore.DataStore;
import com.zols.datastore.domain.NameLabel;
import com.zols.templatemanager.domain.TemplateRepository;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public List<NameLabel> templatePaths() {
        List<NameLabel> nameLabels = new ArrayList<NameLabel>();
        GenericExtFilter filter = new GenericExtFilter(".html");
        List<TemplateRepository> templateRepositorys = dataStore.list(TemplateRepository.class);
        for (TemplateRepository templateRepository : templateRepositorys) {
            populateTemplates(filter, nameLabels, new File(templateRepository.getPath()), null);
        }
        return nameLabels;
    }

    private void populateTemplates(GenericExtFilter filter, List<NameLabel> nameLabels, File dir, String rootPath) {

        if (rootPath == null) {
            rootPath = dir.getAbsolutePath();
        }
        rootPath = rootPath.replaceAll("\\\\", "/");

        if (dir.isDirectory()) {
            // list out all the file name and filter by the extension
            File[] list = dir.listFiles(filter);
            for (File file : list) {
                if (file.isDirectory()) {
                    populateTemplates(filter, nameLabels, file, dir.getAbsolutePath());
                } else {
                    NameLabel nameLabel = new NameLabel();
                    nameLabel.setLabel(file.getName());
                    nameLabel.setName(file.getAbsolutePath().replaceAll("\\\\", "/").replaceFirst(rootPath, "").replaceAll(filter.getExt(), "").replaceFirst("/", ""));
                    nameLabels.add(nameLabel);
                }
            }
        }

    }

    // inner class, generic extension filter
    private class GenericExtFilter implements FilenameFilter {

        private String ext;

        public GenericExtFilter(String ext) {
            this.ext = ext;
        }

        public String getExt() {
            return ext;
        }

        public boolean accept(File dir, String name) {
            return (( !dir.getName().equals("mobile") && !dir.getName().equals("tablet")) && (name.endsWith(ext)
                    || new File(dir.getAbsolutePath() + File.separator + name).isDirectory()));
        }
    }

}
