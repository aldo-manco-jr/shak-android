package org.aldofrankmarco.shak.streams.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.Post;

import java.util.List;

public class GetPostsListResponse {

    @SerializedName("message")
    String message;

    @SerializedName("streamPosts")
    List<Post> streamPosts;

    @SerializedName("favouritePosts")
    List<Post> favouritePosts;

    public String getMessage() {
        return message;
    }

    public List<Post> getStreamPosts() {
        return streamPosts;
    }

    public List<Post> getFavouritePosts() {
        return favouritePosts;
    }
}
