package org.aldofrank.shak.services;

import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.streams.http.posts.PostsListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface StreamsService {

    @GET("posts")
    Call<PostsListResponse> getAllPosts();

    @POST("post/remove-post")
    Call<Object> deletePost(@Body Post post);

    @POST("post/add-like")
    Call<Object> likePost(@Body Post post);

    @POST("post/remove-like")
    Call<Object> unlikePost(@Body Post post);


}
