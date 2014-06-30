/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.config;

import org.jodel.store.DataStore;
import org.jodel.store.mongo.MongoDataStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.zols")
public class ZolsConfiguration {

    @Bean
    public DataStore dataStore() {
        return new MongoDataStore();
    }
}
