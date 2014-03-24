package org.zols.datastore.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

/**
 *
 * This describes the entity of the entity added to zols datastore.
 *
 * @author Sathish Kumar Thiyagarajan
 *
 */
public class Entity {

    @Id
    private String name;

    private String label;

    private String description;

    private Date createdDate;

    private List<Attribute> attributes;

    /**
     * gets the list of attributes
     * @return The attributes
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * sets name to the attributes
     * @param attributes The attributes to be got
     */
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * get the name of the entity
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * sets entity name
     *
     * @param name name of the entity
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets description about entity
     *
     * @return description of the entity
     */

    public String getDescription() {
        return description;
    }

    /**
     * @param description description to be got
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get label for the entity
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * set the label
     *
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * get the date when this entity created
     *
     * @return the date if user specified otherwise null
     */
    public Date getCreatedDate() {
        if (createdDate != null) {
            return (Date) (createdDate.clone());
        } else {
            return null;
        }
    }

    /**
     *
     * @param createdDate set the date to the database
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = (Date) (createdDate.clone());
    }

}
