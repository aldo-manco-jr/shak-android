package org.aldofrank.shak.streams.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrank.shak.models.Post;

import java.util.List;

public class GetAllUserPostsResponse {

    @SerializedName("message")
    String message;

    @SerializedName("userPosts")
    List<Post> arrayUserPosts;

    public String getMessage() {
        return message;
    }

    public List<Post> getArrayUserPosts() {
        return arrayUserPosts;
    }
}
