package org.zols.starter.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "workspace",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "workspaceName")
        })
public class Workspace {

    /**
     * declares variable id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * declares variable workspaceName.
     */
    @NotBlank
    @Size(max = 100)
    private String workspaceName;

    /**
     * declares variable description.
     */
    @Size(max = 2000)
    private String description;

    /**
     * gets the workspaceName.
     *
     * @return workspaceName
     */
    public String getWorkspaceName() {
        return this.workspaceName;
    }

    /**
     * Sets workspaceName.
     *
     * @param anWorkspaceName an workspaceName
     */
    public void setWorkspaceName(final String anWorkspaceName) {
        this.workspaceName = anWorkspaceName;
    }
    /**
     * gets the description.
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets description.
     *
     * @param anDescription description
     */
    public void setDescription(final String anDescription) {
        this.description = anDescription;
    }


}
