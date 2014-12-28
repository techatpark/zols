/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.templates;

import java.io.File;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.zols.templates.domain.TemplateRepository;
import org.zols.templates.service.TemplateRepositoryService;

@Configuration
public class TemplateConfiguration {

    @Autowired
    private TemplateRepositoryService templateRepositoryService;

    @Autowired
    private SpringTemplateEngine templateEngine;
    
    @PostConstruct
    public void intializeTemplates() {        
        TemplateResolver resolver;
        File file;
        try {
            List<TemplateRepository> templateRepositories = templateRepositoryService.list();
            if (templateRepositories != null) {
                for (TemplateRepository templateRepository : templateRepositories) {
                    switch (templateRepository.getType()) {
                        case "file":
                            resolver = new FileTemplateResolver();
                            file = new File(templateRepository.getPath());
                            resolver.setPrefix(file.getAbsolutePath() + File.separator);
                            intializeResolver(resolver);
                            templateEngine.addTemplateResolver(resolver);
                            break;
                    }
                }
            }

        } catch (Exception e) {
        }
    }
    
    private void intializeResolver(TemplateResolver resolver) {
//        resolver.setSuffix(this.properties.getSuffix());
//        resolver.setTemplateMode(this.properties.getMode());
//        resolver.setCharacterEncoding(this.properties.getEncoding());
//        resolver.setCacheable(this.properties.isCache());
    }
    
    
}
