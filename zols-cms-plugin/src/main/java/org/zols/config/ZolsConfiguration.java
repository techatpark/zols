/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.zols.datastore.DataStore;
import org.zols.datastore.elasticsearch.ElasticSearchDataStore;
import org.zols.web.interceptor.PagePopulationInterceptor;

@Configuration
@ComponentScan("org.zols")
public class ZolsConfiguration extends WebMvcConfigurerAdapter{

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pagePopulationInterceptor());
    }
    
    @Bean
    public PagePopulationInterceptor pagePopulationInterceptor() {
        return new PagePopulationInterceptor();
    }
    
    @Bean
    public DataStore dataStore() {
        return new ElasticSearchDataStore();
    }
    

}
