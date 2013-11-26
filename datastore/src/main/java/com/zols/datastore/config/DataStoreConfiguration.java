/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zols.datastore.config;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

@Configuration
@ComponentScan(basePackages = "com.zols")
public class DataStoreConfiguration {

    public @Bean
    MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(), "zolsdb");
    }

    @Bean
    FieldNamingStrategy fieldNamingStrategy() {
        return new FieldNamingStrategy();
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        ((MongoMappingContext) mongoTemplate.getConverter().getMappingContext())
                .setFieldNamingStrategy(fieldNamingStrategy());
        return mongoTemplate;
    }

    private static class FieldNamingStrategy 
    implements org.springframework.data.mongodb.core.mapping.FieldNamingStrategy {

        @Override
        public String getFieldName(MongoPersistentProperty property) {
            return property.getName().replaceAll("\\$cglib_prop_", "");
        }

    }
}
