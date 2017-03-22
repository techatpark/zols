/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sample.app;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import static org.elasticsearch.common.settings.Settings.settingsBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfiguration {

    @Bean
    public Client client() {
        Settings settings = settingsBuilder().put("index.number_of_shards", 1)
                .put("path.home", "/")
                .put("path.data", "data")
                .put("index.number_of_replicas", 0).build();
        return nodeBuilder().settings(settings).build().start().client();
    }
}
