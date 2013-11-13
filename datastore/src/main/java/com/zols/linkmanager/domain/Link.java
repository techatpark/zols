package com.zols.linkmanager.domain;

import java.util.List;

import org.springframework.data.annotation.Id;

/**
 *
 * This describes the entity of the entity added to zols datastore.
 *
 * @author Sathish Kumar Thiyagarajan
 *
 */
public class Link {

    @Id
    private String name;

    private String label;

    private String description;
    
    private String parentLinkName;

    public String getParentLinkName() {
        return parentLinkName;
    }

    public void setParentLinkName(String parentLinkName) {
        this.parentLinkName = parentLinkName;
    }
    
    

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
