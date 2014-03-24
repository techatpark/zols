package org.zols.web.config;

import org.zols.datastore.config.DataStoreConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration 
@Import({ServiceConfiguration.class, DataStoreConfiguration.class})
public class ControllerConfiguration {
	
	@Autowired
	private ServiceConfiguration serviceConfiguration;
	
     

}
