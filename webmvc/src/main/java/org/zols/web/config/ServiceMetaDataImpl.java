/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.web.config;

import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceMetaDataImpl implements org.zols.datastore.ServiceMetaData {
    
    @Autowired
    private ServletContext servletContext;
    
    @Override
    public String getDatabaseName() {
        return servletContext.getContextPath().substring(1);        
    }
    
}
