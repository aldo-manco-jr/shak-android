package org.aldofrankmarco.shak.streams.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.Post;

public class GetPostResponse {

    @SerializedName("message")
    String message;

    @SerializedName("post")
    Post post;

    public String getMessage() {
        return message;
    }

    public Post getPost() {
        return post;
    }
}
