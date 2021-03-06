package org.aldofrankmarco.shak.people.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.User;

import java.util.List;

public class GetFollowingResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("followingList")
    private List<User> followingList;

    public String getMessage() {
        return message;
    }

    public List<User> getFollowingList() {
        return followingList;
    }
}
