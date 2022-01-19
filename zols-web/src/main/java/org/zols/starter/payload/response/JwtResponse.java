package org.zols.starter.payload.response;

import java.util.List;

public class JwtResponse {

    /**
     * declares variable list of roles.
     */
    private final List<String> roles;
    /**
     * declares variable token.
     */
    private String token;
    /**
     * declares variable type of String Bearer.
     */
    private String type = "Bearer";
    /**
     * declares variable id.
     */
    private Long id;
    /**
     * declares variable userName.
     */
    private String username;
    /**
     * declares variable email.
     */
    private String email;

    /**
     * Instantiates a new Jwt Response.
     *
     * @param accessToken the access Token
     * @param anId an id
     * @param aUsername a userName
     * @param anEmail an email
     * @param aRoles a roles
     */
    public JwtResponse(final String accessToken, final Long anId,
                       final String aUsername,
                       final String anEmail, final List<String> aRoles) {
        this.token = accessToken;
        this.id = anId;
        this.username = aUsername;
        this.email = anEmail;
        this.roles = aRoles;
    }

    /**
     * gets the access token.
     *
     * @return token
     */
    public String getAccessToken() {
        return token;
    }
    /**
     * Sets accessToken.
     *
     * @param accessToken accessToken
     */
    public void setAccessToken(final String accessToken) {
        this.token = accessToken;
    }
    /**
     * gets the tokenType.
     *
     * @return type
     */
    public String getTokenType() {
        return type;
    }
    /**
     * Sets tokenType.
     *
     * @param tokenType tokenType
     */
    public void setTokenType(final String tokenType) {
        this.type = tokenType;
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
     * gets the roles.
     *
     * @return roles
     */
    public List<String> getRoles() {
        return roles;
    }
}
