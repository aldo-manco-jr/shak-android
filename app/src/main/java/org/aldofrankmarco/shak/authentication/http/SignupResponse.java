package org.aldofrankmarco.shak.authentication.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.User;

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
