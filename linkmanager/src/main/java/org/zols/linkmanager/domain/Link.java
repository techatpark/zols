package org.zols.linkmanager.domain;

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
     * children of the link.
     */
    private List<Link> children;
    /**
     * category of the link.
     */
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Link> getChildren() {
        return children;
    }

    public void setChildren(List<Link> children) {
        this.children = children;
    }

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

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
    
    

}
