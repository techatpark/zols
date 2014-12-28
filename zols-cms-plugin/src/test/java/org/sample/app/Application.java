package org.sample.app;

import com.mangofactory.swagger.plugin.EnableSwagger;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.boot.SpringApplication.run;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.zols.plugin.EnableCMS;

@ComponentScan
@EnableAutoConfiguration
@EnableCMS
@EnableSwagger
@EnableWebMvcSecurity
public class Application extends WebSecurityConfigurerAdapter  {    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests().anyRequest()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }
    
//org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration
//org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration
//org.springframework.security.config.annotation.web.servlet.configuration.WebMvcSecurityConfiguration
    
    public static void main(String[] args) {
        ApplicationContext ctx = run(Application.class, args);
        
        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }
}
