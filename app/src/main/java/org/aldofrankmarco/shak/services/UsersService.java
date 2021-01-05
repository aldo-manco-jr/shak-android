package org.aldofrankmarco.shak.services;

import org.aldofrankmarco.shak.people.http.GetAllUsersResponse;
import org.aldofrankmarco.shak.people.http.GetFollowersResponse;
import org.aldofrankmarco.shak.people.http.GetFollowingResponse;
import org.aldofrankmarco.shak.people.http.GetUserByUsernameResponse;
import org.aldofrankmarco.shak.people.http.IsFollowingResponse;
import org.aldofrankmarco.shak.people.http.SetUserLocationRequest;
import org.aldofrankmarco.shak.settings.http.ChangePasswordRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsersService {

    @GET("user/list/all")
    Call<GetAllUsersResponse> getAllUsers();

    @GET("user/{username}")
    Call<GetUserByUsernameResponse> getUserByUsername(@Path("username") String username);

    @PUT("user/location/{id}")
    Call<Object> setUserLocation(@Path("id") String idUser, @Body SetUserLocationRequest setUserLocationRequest);

    @GET("user/follow/{username}")
    Call<IsFollowingResponse> isFollowing(@Path("username") String username);

    @GET("user/list/following/{username}")
    Call<GetFollowingResponse> getFollowing(@Path("username") String username);

    @GET("user/list/followers/{username}")
    Call<GetFollowersResponse> getFollowers(@Path("username") String username);

    @POST("user/follow/{userFollowed}")
    Call<Object> followUser(@Path("userFollowed") String username);

    @DELETE("user/follow/{userFollowed}")
    Call<Object> unfollowUser(@Path("userFollowed") String username);

    @POST("auth/change-password")
    Call<Object> changePassword(@Body ChangePasswordRequest changePasswordRequest);

    @DELETE("auth/delete")
    Call<Object> deleteUser();
}
