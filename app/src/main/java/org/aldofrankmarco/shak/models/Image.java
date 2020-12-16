package org.aldofrankmarco.shak.models;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class Image{

    @SerializedName("_id")
    private JsonElement imageDatabaseId;

    @SerializedName("imageId")
    private String imageId;

    @SerializedName("imageVersion")
    private String imageVersion;

    public String getImageDatabaseId() {
        return imageDatabaseId.getAsString();
    }

    public String getImageId() {
        return imageId;
    }

    public String getImageVersion() {
        return imageVersion;
    }
}