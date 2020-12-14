package org.aldofrankmarco.shak.services;

import org.aldofrankmarco.shak.notifications.http.GetNotificationsListResponse;
import org.aldofrankmarco.shak.profile.http.GetImagesListResponse;
import org.aldofrankmarco.shak.settings.http.ChangePasswordRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationsService {

    @GET("notifications-list")
    Call<GetNotificationsListResponse> getAllNotifications();

    @POST("notification/delete/{id}")
    Call<Object> deleteNotification(@Path("id") String notificationId);

    @POST("notification/mark/{id}")
    Call<Object> markNotificationAsRead(@Path("id") String notificationId);

    @POST("mark-all")
    Call<Object> markAllNotificationsAsRead();

    @POST("user/view-profile")
    Call<Object> addNotificationProfileViewed(@Body String userId);
}
