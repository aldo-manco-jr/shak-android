package org.aldofrankmarco.shak.services;

import org.aldofrankmarco.shak.settings.http.ChangePasswordRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationsService {

    // frank
    @POST("change-password")
    Call<Object> changePassword(@Body ChangePasswordRequest changePasswordRequest);

    // frank
    @POST("mark/{id}")
    Call<Object> changeNotificationSetting(@Path("id") String notificationId, @Body boolean isDeleted);

    // frank
    @POST("mark-all")
    Call<Object> markAllNotificationsAsRead();
}
