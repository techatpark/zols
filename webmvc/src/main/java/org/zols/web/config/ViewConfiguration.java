package org.zols.web.config;

import com.zols.datastore.DataStore;
import com.zols.templatemanager.domain.TemplateRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mobile.device.view.LiteDeviceDelegatingViewResolver;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

@Configuration
public class ViewConfiguration {

    @Autowired
    private DataStore dataStore;

    public ClassLoaderTemplateResolver classLoaderTemplateResolver() {
        ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
        classLoaderTemplateResolver.setSuffix(".html");
        classLoaderTemplateResolver.setTemplateMode("HTML5");
        classLoaderTemplateResolver.setOrder(100);
        classLoaderTemplateResolver.setCacheable(false);
        return classLoaderTemplateResolver;
    }

    public ServletContextTemplateResolver servletContextTemplateResolver() {
        ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
        resolver.setPrefix("/WEB-INF/resources/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(1);
        resolver.setCacheable(false);
        return resolver;
    }

//    public FileTemplateResolver fileTemplateResolver() {
//        FileTemplateResolver resolver = new FileTemplateResolver();
//        resolver.setPrefix("D:/Projects/MarketThoughts/");
//        resolver.setSuffix(".html");
//        resolver.setTemplateMode("HTML5");
//        resolver.setOrder(2);
//        resolver.setCacheable(false);
//        return resolver;
//    }  
    private void addTemplateRepositories(SpringTemplateEngine templateEngine) {
        List<TemplateRepository> templateRepositories = dataStore.list(TemplateRepository.class);
        if (templateRepositories != null) {
            for (TemplateRepository templateRepository : templateRepositories) {
                if (templateRepository.getType().equals(TemplateRepository.FILE_SYSTEM)) {
                    addFileSystemTemplateResolver(templateRepository,templateEngine);
                }
                else {
                    addFTPTemplateResolver(templateRepository,templateEngine);
                }
                
            }

        }
    }

    private void addFileSystemTemplateResolver(TemplateRepository templateRepository, SpringTemplateEngine templateEngine) {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(templateRepository.getPath());
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(templateEngine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        templateEngine.addTemplateResolver(resolver);
    }
    
    private void addFTPTemplateResolver(TemplateRepository templateRepository, SpringTemplateEngine templateEngine) {
        UrlTemplateResolver resolver = new UrlTemplateResolver();
        resolver.setPrefix(templateRepository.getPath());
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(templateEngine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        templateEngine.addTemplateResolver(resolver);
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.addTemplateResolver(servletContextTemplateResolver());
        engine.addTemplateResolver(classLoaderTemplateResolver());

        engine.addDialect("sec", new SpringSecurityDialect());
//        engine.addTemplateResolver(urlTemplateResolver());
        return engine;
    }

    @Bean
    public ViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver resolver = new org.zols.web.view.resolver.ViewResolver();
        resolver.setTemplateEngine(templateEngine());

        LiteDeviceDelegatingViewResolver delegateResolver = new LiteDeviceDelegatingViewResolver(
                resolver);
        delegateResolver.setMobilePrefix("mobile/");
        delegateResolver.setTabletPrefix("tablet/");
        
        addTemplateRepositories(resolver.getTemplateEngine());

        return delegateResolver;
    }
}
