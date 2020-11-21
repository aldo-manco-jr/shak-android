package org.aldofrank.shak.streams.http.posts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.aldofrank.shak.models.Post;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.POST;

public class PostsListResponse {

    @SerializedName("message")
    String message;

    @SerializedName("allPosts")
    List<Post> arrayPosts;

    @SerializedName("top")
    List<Post> favouritePosts;

    public String getMessage() {
        return message;
    }

    public List<Post> getArrayPosts() {
        return arrayPosts;
    }

    public List<Post> getFavouritePosts() {
        return favouritePosts;
    }
}
