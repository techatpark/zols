package org.zols.starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zols.starter.models.Workspace;

import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    /**
     * Finding the workspace by Name.
     *
     * @param workspaceName the workspaceName
     * @return workspaceName
     */
    Optional<Workspace> findByWorkspaceName(String workspaceName);
    /**
     * Exists the workspace by Name.
     *
     * @param workspaceName the workspaceName
     * @return workspaceName
     */
    Boolean existsByWorkspaceName(String workspaceName);

}
