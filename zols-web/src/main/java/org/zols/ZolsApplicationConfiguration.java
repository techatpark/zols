/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zols.datastore.DataStore;
import org.zols.datastore.elasticsearch.ElasticSearchDataStorePersistence;

@Configuration
public class ZolsApplicationConfiguration {

    /**
     * declares variable indexName.
     */
    @Value("${spring.application.name}")
    private String indexName;

    /**
     * declares variable restHighLevelClient.
     */
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * dataStore of an application.
     * @return dataStore
     */
    @Bean
    public DataStore dataStore() {
        return new DataStore(new ElasticSearchDataStorePersistence(indexName,
                restHighLevelClient));
    }

}
