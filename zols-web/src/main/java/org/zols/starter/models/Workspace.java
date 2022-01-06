package org.zols.starter.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "workspace",
    uniqueConstraints = { 
      @UniqueConstraint(columnNames = "workspaceName")
    })
public class Workspace {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Size(max = 100)
  private String workspaceName;

  @Size(max = 2000)
  private String description;

  public void setWorkspaceName(String workspaceName) {
      this.workspaceName = workspaceName;
  }

  public String getWorkspaceName() {
      return this.workspaceName;
  }

  public void setDescription(String description) {
      this.description = description;
  }

  public String getDescription() {
      return this.description;
  }


}
