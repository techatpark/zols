package org.zols.starter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.zols.starter.models.Workspace;
@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
  Optional<Workspace> findByWorkspaceName(String workspaceName);

  Boolean existsByWorkspaceName(String workspaceName);    
    
}
