package org.zols.core.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zols.core.config.SpringMongoConfig;

@Configuration
@Import({SpringMongoConfig.class})
public class ControllerConfiguration {

    @Bean
    public JacksonObjectMapper jacksonObjectMapper() {
        return new JacksonObjectMapper();
    }
}
