package com.zols.templatemanager;

import com.zols.datastore.DataStore;
import com.zols.templatemanager.domain.TemplateRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

@Service
public class TemplateRepositoryManager {

    @Autowired
    private DataStore dataStore;

    @Autowired
    private SpringTemplateEngine templateEngine;   

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
    

}
