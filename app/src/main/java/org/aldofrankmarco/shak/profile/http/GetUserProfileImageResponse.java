package org.aldofrankmarco.shak.profile.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.User;

import java.util.List;

public class GetUserProfileImageResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("userProfileImageId")
    private String userProfileImageId;

    @SerializedName("userProfileImageVersion")
    private String userProfileImageVersion;

    public String getMessage() {
        return message;
    }

    public String getUserProfileImageId() {
        return userProfileImageId;
    }

    public String getUserProfileImageVersion() {
        return userProfileImageVersion;
    }
}
