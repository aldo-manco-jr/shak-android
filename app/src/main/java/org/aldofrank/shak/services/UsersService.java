package org.aldofrank.shak.services;

import org.aldofrank.shak.people.http.GetAllUsersResponse;
import org.aldofrank.shak.people.http.GetUserByIdResponse;
import org.aldofrank.shak.people.http.GetUserByUsernameResponse;
import org.aldofrank.shak.settings.http.ChangePasswordRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UsersService {
//router.get('/users', AuthHelper.VerifyToken, UserCtrl.GetAllUsers);
    @GET ("users")
    Call<GetAllUsersResponse> getAllUsers();

    @GET("username/{username}")
    Call<GetUserByUsernameResponse> getUserByUsername(@Path("username") String username);

    @GET("username/{id}")
    Call<GetUserByIdResponse> getUserById(@Path("id") String username);

    @POST("user/view-profile")
    Call<Object> addViewProfileNotification(@Body String userId);

    @POST("change-password")
    Call<Object> changePassword(@Body ChangePasswordRequest changePasswordRequest);

    @POST("follow-user")
    Call<Object> followUser(@Body String followedUserId);

    @POST("unfollow-user")
    Call<Object> unfollowUser(@Body String unfollowedUserId);

    @POST("mark/{id}")
    Call<Object> markNotificationAsRead(@Body String notificationId, boolean isDeleted);

    @POST("mark-all")
    Call<Object> markAllNotificationsAsRead();
/*
TODO RIMETTERE
    @POST("upload-image")
    Call<Object> markAllNotificationsAsRead();

    @GET("set-default-image/{imageId}/{imageVersion}")
    Call<GetUserByIdResponse> getUserById(@Path("id") String username);
*/

    /*se il parametro varia occorre usare @Path, se invece non varia perchè è generale usare @body,
     se il dato varia occorre usare nomeparametro/{...}
    @GET("username/{username}")
    Call<GetUserByUsernameResponse> getUserByUsername(@Path("username") String username);
     */
}
