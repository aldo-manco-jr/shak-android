package org.aldofrank.shak.services;

import com.google.gson.JsonObject;

import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.people.http.GetUserByUsernameResponse;
import org.aldofrank.shak.streams.http.AddCommentRequest;
import org.aldofrank.shak.streams.http.DeleteCommentRequest;
import org.aldofrank.shak.streams.http.GetAllUserPostsResponse;
import org.aldofrank.shak.streams.http.GetNewPostsListResponse;
import org.aldofrank.shak.streams.http.GetPostResponse;
import org.aldofrank.shak.streams.http.GetPostsListResponse;

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

    @GET("username/{username}")
    Call<GetUserByUsernameResponse> getUserByUsername(@Path("username") String username);
}