package org.zols.core.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zols.core.config.SpringMongoConfig;

@Configuration
@Import({ SpringMongoConfig.class })
public class ControllerConfiguration {

}
