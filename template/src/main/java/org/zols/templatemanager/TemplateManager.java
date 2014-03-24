/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.templatemanager;

import org.zols.datastore.DataStore;
import org.zols.templatemanager.domain.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author rahul_ma
 */
@Service
public class TemplateManager {
    @Autowired
    private DataStore dataStore;

    public Template add(Template template) {
        return dataStore.create(template, Template.class);
    }
    
    public void update(Template template) {
        dataStore.update(template, Template.class);
    }
    
     public void deleteTemplate(String templateName) {
        dataStore.delete(templateName, Template.class);
    }
     
     public Template getTemplate(String templateName){
         return dataStore.read(templateName, Template.class);
     }
     
      public Page<Template> templateList(Pageable page) {
        return dataStore.list(page, Template.class);
    }

     
     

   

}
