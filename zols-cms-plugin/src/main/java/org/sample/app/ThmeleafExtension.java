/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sample.app;

import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Configuration
@EnableConfigurationProperties(ThymeleafProperties.class)
public class ThmeleafExtension {

    @Autowired
    private ThymeleafProperties properties;

    @Bean
    public ITemplateResolver defaultTemplateResolver() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        File file = new File("../zols-ui/app");
        resolver.setPrefix(file.getAbsolutePath() + File.separator);
        intializeResolver(resolver);     
        return resolver;
    }

    private void intializeResolver(TemplateResolver resolver) {
//        resolver.setPrefix(this.properties.getPrefix());
        resolver.setSuffix(this.properties.getSuffix());
        resolver.setTemplateMode(this.properties.getMode());
        resolver.setCharacterEncoding(this.properties.getEncoding());
        resolver.setCacheable(this.properties.isCache());
    }
}
