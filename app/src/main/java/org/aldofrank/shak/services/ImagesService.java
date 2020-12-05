package org.aldofrank.shak.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface ImagesService {

    @POST("upload-image")
    Call<Object> uploadImage(@Body String imageEncoded);

    @GET("set-default-image/{imageId}/{imageVersion})")
    Call<Object> setUserProfilePhoto(@Path("imageId") String imageId, @Path("imageVersion") String imageVersion);

    @GET("set-cover-image/{imageId}/{imageVersion})")
    Call<Object> setUserCoverPhoto(@Path("imageId") String imageId, @Path("imageVersion") String imageVersion);
}