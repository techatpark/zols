package org.zols.starter.models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
     * MAXSIZE1.
     */
    public static final int MAXSIZE1 = 20;
    /**
     * MAXSIZE2.
     */
    public static final int MAXSIZE2 = 8;
    /**
     * MAXSIZE3.
     */
    public static final int MAXSIZE3 = 120;
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
    @Size(max = MAXSIZE1)
    private String username;

    /**
     * email.
     */
    @NotBlank
    @Size(max = MAXSIZE1)
    @Email
    private String email;

    /**
     * password.
     */
    @NotBlank
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
     * @param anUsername  username
     * @param anEmail     email
     * @param anPassword  password
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
