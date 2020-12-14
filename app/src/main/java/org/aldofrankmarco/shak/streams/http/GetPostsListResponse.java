package org.aldofrankmarco.shak.streams.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.Post;

import java.util.List;

public class GetPostsListResponse {

    @SerializedName("message")
    String message;

    @SerializedName("allPosts")
    List<Post> arrayPosts;

    @SerializedName("top")
    List<Post> favouritePosts;

    public String getMessage() {
        return message;
    }

    public List<Post> getStreamPosts() {
        return arrayPosts;
    }

    public List<Post> getFavouritePosts() {
        return favouritePosts;
    }
}
