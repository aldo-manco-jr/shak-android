package org.aldofrank.shak.authentication.http.login;

import com.google.gson.annotations.SerializedName;

import org.aldofrank.shak.models.User;

public class LoginResponse {

    @SerializedName("userFound")
    User userFound;

    @SerializedName("message")
    String message;

    @SerializedName("token")
    String token;

    public User getUserFound() {
        return userFound;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
