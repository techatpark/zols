package org.zols.cms;

import java.util.Date;
import java.util.Map;
import static org.springframework.boot.SpringApplication.run;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.zols.plugin.EnableCMS;

@ComponentScan
@EnableAutoConfiguration
@EnableCMS
@EnableGlobalMethodSecurity(securedEnabled = true)
public class Application extends WebMvcConfigurerAdapter {

    @Controller
    protected static class HomeController {

        @RequestMapping("/")
        @Secured("ROLE_ADMIN")
        public String home(Map<String, Object> model) {
            model.put("message", "Hello World");
            model.put("title", "Hello Home");
            model.put("date", new Date());
            return "home";
        }

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/access").setViewName("access");
    }

    @Bean
    public ApplicationSecurity applicationSecurity() {
        return new ApplicationSecurity();
    }

    public static void main(String[] args) {
        run(Application.class, args);
    }
}
