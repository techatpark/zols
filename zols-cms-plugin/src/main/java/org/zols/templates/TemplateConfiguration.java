/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.templates;

import java.io.File;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;
import org.zols.datatore.exception.DataStoreException;
import org.zols.templates.domain.TemplateRepository;
import org.zols.templates.service.TemplateRepositoryService;

@Configuration
@EnableConfigurationProperties(ThymeleafProperties.class)
public class TemplateConfiguration {

    private static final Logger LOGGER = getLogger(TemplateConfiguration.class);

    @Autowired
    private ThymeleafProperties properties;

    @Autowired
    private TemplateRepositoryService templateRepositoryService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @PostConstruct
    public void intializeTemplates() {
        int retry = 0;
        LOGGER.debug("intialize Template Repositories");
        AbstractConfigurableTemplateResolver resolver;
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
                        case "url":
                            resolver = new UrlTemplateResolver();
                            resolver.setPrefix(templateRepository.getPath() + "/");
                            intializeResolver(resolver);
                            templateEngine.addTemplateResolver(resolver);
                            break;
                    }
                    LOGGER.info("Added Template Repository {}", templateRepository.getName());
                }
            }

        } catch (DataStoreException e) {
            LOGGER.error("Error intializing Template Repositories", e);
            retry++;
            if (retry < 2) {
                LOGGER.debug("Retring intializing Template Repositories {}", retry);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                }
                intializeTemplates();
            }

        }
        addZolsTemplates();
        LOGGER.debug("intialized Template Repositories");
    }

    private void addZolsTemplates() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        
        resolver.setPrefix("classpath:/zolstemplates/");

        intializeResolver(resolver);
        templateEngine.addTemplateResolver(resolver);
    }



    private void intializeResolver(AbstractConfigurableTemplateResolver resolver) {
//        resolver.setPrefix(this.properties.getPrefix());
        resolver.setSuffix(this.properties.getSuffix());
        resolver.setTemplateMode(this.properties.getMode());
        resolver.setCharacterEncoding(this.properties.getEncoding().toString());
        resolver.setCacheable(this.properties.isCache());
    }

}
