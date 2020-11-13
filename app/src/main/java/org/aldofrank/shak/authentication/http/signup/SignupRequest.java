package org.aldofrank.shak.authentication.http.signup;

import com.google.gson.annotations.SerializedName;

public class SignupRequest {

    @SerializedName("email")
    String email;

    @SerializedName("username")
    String username;

    @SerializedName("password")
    String password;

    public SignupRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
