package org.aldofrank.shak.streams.http.posts;

import com.google.gson.annotations.SerializedName;

import org.aldofrank.shak.models.Post;

import java.util.ArrayList;

public class PostsListResponse {

    @SerializedName("message")
    String message;

    @SerializedName("allPosts")
    ArrayList<Post> arrayPosts;

    @SerializedName("top")
    ArrayList<Post> favouritePosts;

    public String getMessage() {
        return message;
    }

    public ArrayList<Post> getArrayPosts() {
        return arrayPosts;
    }

    public ArrayList<Post> getFavouritePosts() {
        return favouritePosts;
    }
}
