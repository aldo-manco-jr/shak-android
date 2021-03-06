package org.aldofrankmarco.shak.people.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.User;

public class GetUserByUsernameResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("userFoundByName")
    private User userFoundByUsername;

    public String getMessage() {
        return message;
    }

    public User getUserFoundByUsername() {
        return userFoundByUsername;
    }
}
