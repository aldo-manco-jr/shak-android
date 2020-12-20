package org.aldofrankmarco.shak.services;

import org.aldofrankmarco.shak.notifications.http.GetNotificationsListResponse;
import org.aldofrankmarco.shak.profile.http.GetImagesListResponse;
import org.aldofrankmarco.shak.settings.http.ChangePasswordRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NotificationsService {

    @GET("notification/list")
    Call<GetNotificationsListResponse> getAllNotifications();

    @DELETE("notification/{id}")
    Call<Object> deleteNotification(@Path("id") String notificationId);

    @PUT("notification/{id}")
    Call<Object> markNotificationAsRead(@Path("id") String notificationId);

    @POST("notification/mark-all")
    Call<Object> markAllNotificationsAsRead();

    @POST("notification/profile-viewed/{id}")
    Call<Object> addNotificationProfileViewed(@Path("id") String userId);
}
