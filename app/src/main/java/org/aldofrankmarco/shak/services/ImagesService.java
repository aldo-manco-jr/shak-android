package org.aldofrankmarco.shak.services;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ImagesService {

    @POST("upload-image")
    Call<Object> uploadImage(@Body JsonObject imageData);

    @GET("set-default-image/{imageId}/{imageVersion}")
    Call<Object> setUserProfilePhoto(@Path("imageId") String imageId, @Path("imageVersion") String imageVersion);

    @GET("set-cover-image/{imageId}/{imageVersion}")
    Call<Object> setUserCoverPhoto(@Path("imageId") String imageId, @Path("imageVersion") String imageVersion);
}
