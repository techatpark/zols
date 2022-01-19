package org.zols.starter.payload.request;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
    /**
     * declares variable username.
     */
    @NotBlank
    private String username;
    /**
     * declares variable password.
     */
    @NotBlank
    private String password;

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
     * gets the password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }
    /**
     * Sets password.
     *
     * @param anPassword  password
     */
    public void setPassword(final String anPassword) {
        this.password = anPassword;
    }
}
