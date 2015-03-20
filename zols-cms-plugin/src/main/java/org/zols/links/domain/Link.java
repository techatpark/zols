/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import javax.persistence.Id;

/**
 *
 * @author sathish_ku
 */
@JsonIgnoreProperties({"children"})
public class Link {

    @Id
    private String name;

    /**
     * Label of the link.
     */
    private String label;

    /**
     * Description of the link.
     */
    private String description;

    /**
     * Parent name of the link.
     */
    private String parentLinkName;

    /**
     * Target Url.
     */
    private String targetUrl;

    /**
     * Icon Url.
     */
    private String iconUrl;

    /**
     * Childern of Link
     */
    private List<Link> children;

    /**
     * group of the link.
     */
    private String groupName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentLinkName() {
        return parentLinkName;
    }

    public void setParentLinkName(String parentLinkName) {
        this.parentLinkName = parentLinkName;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public List<Link> getChildren() {
        return children;
    }

    public void setChildren(List<Link> children) {
        this.children = children;
    }

}
