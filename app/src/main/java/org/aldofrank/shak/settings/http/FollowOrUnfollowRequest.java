package org.aldofrank.shak.settings.http;

public class FollowOrUnfollowRequest {
    private String userFollowed;

    public FollowOrUnfollowRequest(String userFollowed) {
        this.userFollowed = userFollowed;
    }

    public String getUserFollowed() {
        return userFollowed;
    }
}