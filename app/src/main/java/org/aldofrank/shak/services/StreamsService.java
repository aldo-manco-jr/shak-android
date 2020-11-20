package org.aldofrank.shak.services;

import org.aldofrank.shak.streams.http.posts.PostsListResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StreamsService {

    @GET("posts")
    Call<PostsListResponse> getAllPosts();
}
