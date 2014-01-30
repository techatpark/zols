package com.zols.templatemanager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import com.zols.datastore.ServiceMetaData;
import org.springframework.stereotype.Service;

@Service
public class TestServiceMetaData implements ServiceMetaData {
    
    public String getDatabaseName() {
        return "zols";        
    }
}
