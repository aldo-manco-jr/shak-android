package org.aldofrankmarco.shak.profile.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.User;

import java.util.List;

public class GetImagesListResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("imagesList")
    private List<User.Image> imageList;

    public String getMessage() {
        return message;
    }

    public List<User.Image> getImageList() {
        return imageList;
    }
}
