package org.aldofrank.shak.streams.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrank.shak.models.Post;

import java.util.List;

public class GetNewPostsListResponse {

    @SerializedName("message")
    String message;

    @SerializedName("allNewPosts")
    List<Post> arrayNewPosts;

    public String getMessage() {
        return message;
    }

    public List<Post> getArrayPosts() {
        return arrayNewPosts;
    }
}
