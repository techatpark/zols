package org.zols.starter.models;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {
    /**
     * id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * username.
     */
    @NotBlank
    @Size(max = 20)
    private String username;

    /**
     * email.
     */
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    /**
     * password.
     */
    @NotBlank
    @Size(max = 120)
    private String password;

    /**
     * roles.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * User Method.
     */
    public User() {
    }

    /**
     * Instantiates a user.
     *
     * @param anUsername an username
     * @param anEmail    an email
     * @param anPassword an password
     */
    public User(final String anUsername, final String anEmail,
                final String anPassword) {
        this.username = anUsername;
        this.email = anEmail;
        this.password = anPassword;
    }

    /**
     * gets the id.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }
    /**
     * Sets id.
     *
     * @param anId an id
     */
    public void setId(final Long anId) {
        this.id = anId;
    }
    /**
     * gets the username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }
    /**
     * Sets username.
     *
     * @param anUsername username
     */
    public void setUsername(final String anUsername) {
        this.username = anUsername;
    }
    /**
     * gets the email.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }
    /**
     * Sets email.
     *
     * @param anEmail an email
     */
    public void setEmail(final String anEmail) {
        this.email = anEmail;
    }
    /**
     * gets the password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }
    /**
     * Sets id.
     *
     * @param anPassword  Password
     */
    public void setPassword(final String anPassword) {
        this.password = anPassword;
    }
    /**
     * gets the roles.
     *
     * @return roles
     */
    public Set<Role> getRoles() {
        return roles;
    }
    /**
     * Sets id.
     *
     * @param aRoles a Roles
     */
    public void setRoles(final Set<Role> aRoles) {
        this.roles = aRoles;
    }
}
