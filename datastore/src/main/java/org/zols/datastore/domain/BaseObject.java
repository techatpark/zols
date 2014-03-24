/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.domain;

import org.springframework.data.annotation.Id;

/**
 *
 * @author sathish_ku
 */
public class BaseObject {
    
    @Id
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
