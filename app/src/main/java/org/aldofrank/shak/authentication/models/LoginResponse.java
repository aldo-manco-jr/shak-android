package org.aldofrank.shak.authentication.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("statusCode")
    int statusCode;

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
