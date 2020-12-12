package org.aldofrankmarco.shak.authentication.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.User;

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
