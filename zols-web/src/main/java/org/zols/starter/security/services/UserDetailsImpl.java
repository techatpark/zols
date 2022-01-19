package org.zols.starter.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.zols.starter.models.User;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    /**
     * declares variable id.
     */
    private final Long id;

    /**
     * declares variable username.
     */
    private final String username;

    /**
     * declares variable email.
     */
    private final String email;

    /**
     * declares variable password.
     */
    @JsonIgnore
    private final String password;

    /**
     * declares variable authorities.
     */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Instantiates a new User DetailsImpl.
     *
     * @param anId an id
     * @param aUsername a username
     * @param anEmail an email
     * @param anPassword  password
     * @param anAuthorities an Authorities
     */
    public UserDetailsImpl(final Long anId, final String aUsername,
                                            final String anEmail,
                                            final String anPassword,
                final Collection<? extends GrantedAuthority> anAuthorities) {
        this.id = anId;
        this.username = aUsername;
        this.email = anEmail;
        this.password = anPassword;
        this.authorities = anAuthorities;
    }

    /**
     * Build the UserDetails Implementation.
     *
     * @param user the user
     * @return userDetail
     */
    public static UserDetailsImpl build(final User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    /**
     * gets the authorities.
     *
     * @return authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
     * gets the email.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }
    /**
     * gets the password.
     *
     * @return password
     */
    @Override
    public String getPassword() {
        return password;
    }
    /**
     * gets the username.
     *
     * @return username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Check the AccountNonExpired.
     *
     * @return true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Check the AccountNonLocked.
     *
     * @return true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Check the CredentialsNonExpired.
     *
     * @return true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Check the user details is Enabled.
     *
     * @return true
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Check the user details equals.
     *
     * @return boolean
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
