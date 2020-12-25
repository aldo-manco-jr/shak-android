package org.aldofrankmarco.shak.profile.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.Image;

import java.util.List;

public class ImagesResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("imageId")
    private String imageId;

    @SerializedName("imageVersion")
    private String imageVersion;

    public String getMessage() {
        return message;
    }

    public String getImageId() {
        return imageId;
    }

    public String getImageVersion() {
        return imageVersion;
    }
}
