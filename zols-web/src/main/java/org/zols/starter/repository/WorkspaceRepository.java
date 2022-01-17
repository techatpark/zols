package org.zols.starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zols.starter.models.Workspace;

import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    Optional<Workspace> findByWorkspaceName(String workspaceName);

    Boolean existsByWorkspaceName(String workspaceName);

}
