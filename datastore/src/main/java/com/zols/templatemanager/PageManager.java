/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this page file, choose Tools | Pages
 * and open the page in the editor.
 */
package com.zols.templatemanager;

import com.zols.datastore.DataStore;
import com.zols.datastore.domain.Entity;
import com.zols.datastore.util.DynamicBeanGenerator;
import com.zols.templatemanager.domain.Page;
import com.zols.templatemanager.domain.PageDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author sathish_ku
 */
@Service
public class PageManager {
    
    @Autowired
    private DataStore dataStore;
    
    @Autowired
    private TemplateManager templateManager;
    
    @Autowired
    private DynamicBeanGenerator dynamicBeanGenerator;
    
    public Page add(Page page) {
        return dataStore.create(page, Page.class);
    }
    
    public void update(Page page) {
        dataStore.update(page, Page.class);
    }
    
    public void deletePage(String pageName) {
        dataStore.delete(pageName, Page.class);
    }
    
    public Page getPage(String pageName) {
        return dataStore.read(pageName, Page.class);
    }
    
    public PageDetail getPageDetail(String pageName) {
        PageDetail pageDetail = new PageDetail();
        pageDetail.setPage(dataStore.read(pageName, Page.class));
        pageDetail.setTemplate(templateManager.getTemplate(pageDetail.getPage().getTemplateName()));
        pageDetail.setEntity(dataStore.read(pageDetail.getTemplate().getDataType(), Entity.class));
        pageDetail.setData(dataStore.read(pageDetail.getPage().getDataName(), dynamicBeanGenerator.getBeanClass(pageDetail.getTemplate().getDataType())));
        return pageDetail;
    }
    
    public org.springframework.data.domain.Page<Page> pageList(Pageable page) {
        return dataStore.list(page, Page.class);
    }
}
