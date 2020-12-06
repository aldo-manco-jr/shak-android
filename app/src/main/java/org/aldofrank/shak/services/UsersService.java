package org.aldofrank.shak.services;

import org.aldofrank.shak.people.http.GetAllUsersResponse;
import org.aldofrank.shak.people.http.GetUserByIdResponse;
import org.aldofrank.shak.people.http.GetUserByUsernameResponse;
import org.aldofrank.shak.people.http.SetUserLocationRequest;
import org.aldofrank.shak.people.http.FollowOrUnfollowRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UsersService {

    @GET ("users")
    Call<GetAllUsersResponse> getAllUsers();

    @GET("username/{username}")
    Call<GetUserByUsernameResponse> getUserByUsername(@Path("username") String username);

    @GET("username/{id}")
    Call<GetUserByIdResponse> getUserById(@Path("id") String username);

    @POST("follow-user")
    Call<Object> followUser(@Body FollowOrUnfollowRequest followedUserId);

    @POST("unfollow-user")
    Call<Object> unfollowUser(@Body FollowOrUnfollowRequest followedUserId);

    @POST("user/location")
    Call<Object> setUserLocation(@Body SetUserLocationRequest setUserLocationRequest);

    @POST("user/view-profile")
    Call<Object> addViewProfileNotification(@Body String userId);
}
