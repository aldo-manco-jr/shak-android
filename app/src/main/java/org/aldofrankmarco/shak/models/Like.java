package org.aldofrankmarco.shak.models;

import com.google.gson.annotations.SerializedName;

public class Like{

    @SerializedName("_id")
    private Object likeId;

    @SerializedName("username")
    private String usernamePublisher;

    public Like(String usernamePublisher){
        this.usernamePublisher = usernamePublisher;
        this.likeId = -1;
    }

    public Object getLikeId() {
        return likeId;
    }

    public String getUsernamePublisher() {
        return usernamePublisher;
    }
}