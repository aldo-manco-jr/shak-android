package org.aldofrank.shak.people.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrank.shak.models.User;

public class GetUserByIdResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("userFoundById")
    private User userFoundById;

    public String getMessage() {
        return message;
    }

    public User getUserFoundById() {
        return userFoundById;
    }
}
