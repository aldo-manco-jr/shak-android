package org.aldofrankmarco.shak.notifications.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.User;

import java.util.List;

public class GetNotificationsListResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("notificationsList")
    private List<User.Notification> notificationsList;

    public String getMessage() {
        return message;
    }

    public List<User.Notification> getNotificationsList() {
        return notificationsList;
    }
}
