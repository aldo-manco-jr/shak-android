package org.aldofrank.shak.authentication.http.signup;

import com.google.gson.annotations.SerializedName;

import org.aldofrank.shak.authentication.models.User;

public class SignupResponse {

    @SerializedName("user")
    User userRegistered;

    @SerializedName("message")
    String message;

    @SerializedName("token")
    String token;

    public User getUserRegistered() {
        return userRegistered;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
