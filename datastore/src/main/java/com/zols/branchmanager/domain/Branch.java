/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zols.branchmanager.domain;
import org.springframework.data.annotation.Id;

/**
 *
 * @author monendra_s
 */
public class Branch {
    
    @Id
    private String name;
    
    private String description;
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
    
}
