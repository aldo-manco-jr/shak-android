package org.aldofrank.shak.people.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrank.shak.models.User;

import java.util.List;

public class GetAllUsersResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("allUsers")
    private List<User> allUsers;

    public String getMessage() {
        return message;
    }

    public List<User> getAllUsers() {
        return allUsers;
    }
}
