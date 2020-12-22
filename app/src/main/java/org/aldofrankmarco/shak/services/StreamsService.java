package org.aldofrankmarco.shak.services;

import com.google.gson.JsonObject;

import org.aldofrankmarco.shak.models.Post;
import org.aldofrankmarco.shak.streams.http.AddCommentRequest;
import org.aldofrankmarco.shak.streams.http.GetAllPostCommentsResponse;
import org.aldofrankmarco.shak.streams.http.GetAllUserPostsResponse;
import org.aldofrankmarco.shak.streams.http.GetNewPostsListResponse;
import org.aldofrankmarco.shak.streams.http.GetPostResponse;
import org.aldofrankmarco.shak.streams.http.GetPostsListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StreamsService {

    @GET("post/list/all")
    Call<GetPostsListResponse> getAllPosts();

    @GET("post/list/all/{post}")
    Call<GetPostsListResponse> getAllSearchedPosts(@Path("post") String postContent);

    @GET("post/list/new/{created_at}")
    Call<GetNewPostsListResponse> getAllNewPosts(@Path("created_at") String newPostData);

    @GET("post/list/{username}")
    Call<GetAllUserPostsResponse> getAllUserPosts(@Path("username") String username);

    @GET("post/{id}")
    Call<GetPostResponse> getPost(@Path("id") String postId);

    @POST("post")
    Call<Object> submitPost(@Body JsonObject postData);

    @DELETE("post/{post_id}")
    Call<Object> deletePost(@Path("post_id") Object postId);

    @POST("post/like")
    Call<Object> likePost(@Body Post post);

    @DELETE("post/like/{post_id}")
    Call<Object> unlikePost(@Path("post_id") Object postId);

    @GET("post/comment/list/{id}")
    Call<GetAllPostCommentsResponse> getAllPostComments(@Path("id") String postId);

    @POST("post/comment")
    Call<Object> submitComment(@Body AddCommentRequest addCommentRequest);

    @DELETE("post/comment/{post_id}/{comment_id}")
    Call<Object> deleteComment(@Path("post_id") String postId,
                               @Path("comment_id") Object comment_id);
}