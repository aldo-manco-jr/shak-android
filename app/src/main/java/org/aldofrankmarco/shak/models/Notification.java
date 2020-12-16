package org.aldofrankmarco.shak.models;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class Notification{

    @SerializedName("_id")
    private JsonElement notificationId;

    @SerializedName("senderId")
    private JsonElement userId;

    @SerializedName("message")
    private String notificationContent;

    @SerializedName("viewProfile")
    private boolean isAboutViewedProfile;

    @SerializedName("created")
    private String createdAt;

    @SerializedName("read")
    private boolean isRead;

    public String getNotificationId() {
        return notificationId.getAsString();
    }

    public String getSenderUsername() {
        return notificationContent.split(" ")[0];
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public boolean isAboutViewedProfile() {
        return isAboutViewedProfile;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return isRead;
    }
}