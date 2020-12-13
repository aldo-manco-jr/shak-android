package org.aldofrankmarco.shak.services;

import org.aldofrankmarco.shak.people.http.GetAllUsersResponse;
import org.aldofrankmarco.shak.people.http.GetFollowersResponse;
import org.aldofrankmarco.shak.people.http.GetFollowingResponse;
import org.aldofrankmarco.shak.people.http.GetUserByIdResponse;
import org.aldofrankmarco.shak.people.http.GetUserByUsernameResponse;
import org.aldofrankmarco.shak.people.http.IsFollowingResponse;
import org.aldofrankmarco.shak.people.http.SetUserLocationRequest;
import org.aldofrankmarco.shak.people.http.FollowOrUnfollowRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UsersService {

    @GET("users/")
    Call<GetAllUsersResponse> getAllUsers();

    @GET("username/{username}")
    Call<GetUserByUsernameResponse> getUserByUsername(@Path("username") String username);

    @POST("user/location")
    Call<Object> setUserLocation(@Body SetUserLocationRequest setUserLocationRequest);

    @GET("users/is-following/{username}")
    Call<IsFollowingResponse> isFollowing(@Path("username") String username);

    @GET("users/following/{username}")
    Call<GetFollowingResponse> getFollowing(@Path("username") String username);

    @GET("users/followers/{username}")
    Call<GetFollowersResponse> getFollowers(@Path("username") String username);

    @POST("follow-user")
    Call<Object> followUser(@Body FollowOrUnfollowRequest followedUserId);

    @POST("unfollow-user")
    Call<Object> unfollowUser(@Body FollowOrUnfollowRequest followedUserId);
}
