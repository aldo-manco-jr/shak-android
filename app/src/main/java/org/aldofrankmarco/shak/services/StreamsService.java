package org.aldofrankmarco.shak.services;

import com.google.gson.JsonObject;

import org.aldofrankmarco.shak.models.Post;
import org.aldofrankmarco.shak.streams.http.AddCommentRequest;
import org.aldofrankmarco.shak.streams.http.DeleteCommentRequest;
import org.aldofrankmarco.shak.streams.http.GetAllUserPostsResponse;
import org.aldofrankmarco.shak.streams.http.GetNewPostsListResponse;
import org.aldofrankmarco.shak.streams.http.GetPostResponse;
import org.aldofrankmarco.shak.streams.http.GetPostsListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StreamsService {

    @GET("posts")
    Call<GetPostsListResponse> getAllPosts();

    @GET("posts/new/{created_at}")
    Call<GetNewPostsListResponse> getAllNewPosts(@Path("created_at") String newPostData);

    @GET("posts/{username}")
    Call<GetAllUserPostsResponse> getAllUserPosts(@Path("username") String username);

    @GET("post/{id}")
    Call<GetPostResponse> getPost(@Path("id") String postId);

    @POST("post/add-post")
    Call<Object> submitPost(@Body JsonObject postData);

    @POST("post/remove-post")
    Call<Object> deletePost(@Body Post post);

    @POST("post/add-like")
    Call<Object> likePost(@Body Post post);

    @POST("post/remove-like")
    Call<Object> unlikePost(@Body Post post);

    @POST("post/add-comment")
    Call<Object> submitComment(@Body AddCommentRequest addCommentRequest);

    @POST("post/remove-comment")
    Call<Object> deleteComment(@Body DeleteCommentRequest deleteCommentRequest);
}