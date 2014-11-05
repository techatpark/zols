/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sample.app;

import java.io.File;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@EnableConfigurationProperties(ThymeleafProperties.class)
@Configuration
public class ThmeleafExtension {

    @Autowired
    private ThymeleafProperties properties;

    @Autowired
    private SpringTemplateEngine templateEngine;
    
    private String localTemplateFolder ="../zols-ui/app";

    @PostConstruct
    public void intializeTemplates() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        File file = new File(localTemplateFolder);
        resolver.setPrefix(file.getAbsolutePath() + File.separator);
        intializeResolver(resolver);
        templateEngine.addTemplateResolver(resolver);
    }

    private void intializeResolver(TemplateResolver resolver) {
        resolver.setSuffix(this.properties.getSuffix());
        resolver.setTemplateMode(this.properties.getMode());
        resolver.setCharacterEncoding(this.properties.getEncoding());
        resolver.setCacheable(this.properties.isCache());
    }
}
