package org.zols.starter.payload.response;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private final List<String> roles;

    public JwtResponse(final String accessToken, final Long anId, final String anUsername,
                       final String anEmail, final List<String> aRoles) {
        this.token = accessToken;
        this.id = anId;
        this.username = anUsername;
        this.email = anEmail;
        this.roles = aRoles;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(final String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(final String tokenType) {
        this.type = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }
}
