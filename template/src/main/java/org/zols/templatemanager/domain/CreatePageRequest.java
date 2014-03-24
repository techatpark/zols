/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.templatemanager.domain;

import org.zols.datastore.domain.BaseObject;
import java.util.HashMap;

/**
 *
 * @author sathish_ku
 */
public class CreatePageRequest {

    private Page page;
    private HashMap<String, String> data;

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

}
