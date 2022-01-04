/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zols.datastore.DataStore;
import org.zols.datastore.elasticsearch.ElasticSearchDataStorePersistence;

@Configuration
public class ZolsApplicationConfiguration {
    
    @Value("${spring.application.name}")
    private String indexName;

    @Autowired
    private RestHighLevelClient restHighLevelClient;
        
    @Bean
    public DataStore dataStore() {
        return new DataStore(new ElasticSearchDataStorePersistence(indexName,restHighLevelClient));
    }

}
