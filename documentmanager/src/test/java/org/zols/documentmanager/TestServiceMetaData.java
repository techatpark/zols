/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.documentmanager;

import org.springframework.context.annotation.Bean;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import org.zols.datastore.ServiceMetaData;

@Service
public class TestServiceMetaData implements ServiceMetaData {
    
    public String getDatabaseName() {
        return "testzols";        
    }
    
    @Bean
    public ExpressionParser expressionParser() {
        return new SpelExpressionParser();
    }
}
