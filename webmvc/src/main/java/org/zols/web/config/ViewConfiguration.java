package org.zols.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mobile.device.view.LiteDeviceDelegatingViewResolver;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

@Configuration
public class ViewConfiguration {

    public ClassLoaderTemplateResolver classLoaderTemplateResolver() {
        ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
        classLoaderTemplateResolver.setSuffix(".html");
        classLoaderTemplateResolver.setTemplateMode("HTML5");
        classLoaderTemplateResolver.setOrder(1);
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

    public UrlTemplateResolver urlTemplateResolver() {
        UrlTemplateResolver urlTemplateResolver = new UrlTemplateResolver();
        urlTemplateResolver.setPrefix("http://localhost:8081/zols/resources/");
        urlTemplateResolver.setSuffix(".html");
        urlTemplateResolver.setTemplateMode("HTML5");
        urlTemplateResolver.setOrder(1);
        urlTemplateResolver.setCacheable(false);
        return urlTemplateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.addTemplateResolver(servletContextTemplateResolver());
        engine.addTemplateResolver(classLoaderTemplateResolver());
        engine.addDialect("sec",new SpringSecurityDialect());
//        engine.addTemplateResolver(urlTemplateResolver());
        return engine;
    }
    

    @Bean
    public ViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver resolver = new org.zols.web.view.resolver.ViewResolver() ;
        resolver.setTemplateEngine(templateEngine());

        LiteDeviceDelegatingViewResolver delegateResolver = new LiteDeviceDelegatingViewResolver(
                resolver);
        delegateResolver.setMobilePrefix("mobile/");
        delegateResolver.setTabletPrefix("tablet/");

        return delegateResolver;
    }
}
