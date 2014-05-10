package org.zols.documentmanager;

import org.springframework.context.annotation.Bean;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import org.zols.datastore.ServiceMetaData;

@Service
public class TestServiceMetaData implements ServiceMetaData {
    
    @Override
    public String getDatabaseName() {
        return "testzolsdocumentmanager";        
    }
    
    @Bean
    public ExpressionParser expressionParser() {
        return new SpelExpressionParser();
    }
}
