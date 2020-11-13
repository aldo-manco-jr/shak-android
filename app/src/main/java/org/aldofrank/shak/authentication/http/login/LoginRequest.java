package org.aldofrank.shak.authentication.http.login;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("username")
    String username;

    @SerializedName("password")
    String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
