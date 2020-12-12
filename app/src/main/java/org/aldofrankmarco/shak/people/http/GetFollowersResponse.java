package org.aldofrankmarco.shak.people.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.User;

import java.util.List;

public class GetFollowersResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("followersList")
    private List<User> followersList;

    public String getMessage() {
        return message;
    }

    public List<User> getFollowersList() {
        return followersList;
    }
}
