/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zols.datastore.DataStore;
import org.zols.datastore.service.DataService;
import org.zols.datastore.service.SchemaService;
import org.zols.documents.service.BrowseService;
import org.zols.documents.service.DocumentRepositoryService;
import org.zols.documents.service.DocumentService;


@Configuration
public class ZolsConfiguration implements WebMvcConfigurer {

    /**
     * dataStore.
     */
    @Autowired
    private DataStore dataStore;

    /**
     * this is schemaService.
     * @return new SchemaService of an dataStore
     */
    @Bean
    SchemaService schemaService() {
        return new SchemaService(dataStore);
    }
    /**
     * this is dataService.
     * @return new DataService of an dataStore
     */
    @Bean
    DataService dataService() {
        return new DataService(dataStore);
    }
    /**
     * this is browseService.
     * @return new BrowseService of an dataStore
     */
    @Bean
    BrowseService browseService() {
        return new BrowseService(dataService(), dataStore);
    }
    /**
     * this is documentService.
     * @return new documentRepositoryService of an dataStore
     */
    @Bean
    DocumentService documentService() {
        return new DocumentService(documentRepositoryService());
    }
    /**
     * this is documentRepositoryService.
     * @return new DocumentRepositoryService of an dataStore
     */
    @Bean
    DocumentRepositoryService documentRepositoryService() {
        return new DocumentRepositoryService(dataStore);
    }


}
