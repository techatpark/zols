package org.zols.starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zols.starter.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finding the User by name.
     *
     * @param username the username
     * @return user
     */
    Optional<User> findByUsername(String username);
    /**
     * Finding the User exists by username.
     *
     * @param username the username
     * @return username
     */
    Boolean existsByUsername(String username);
    /**
     * Finding the User exists by email.
     *
     * @param email the email
     * @return email
     */
    Boolean existsByEmail(String email);
}
