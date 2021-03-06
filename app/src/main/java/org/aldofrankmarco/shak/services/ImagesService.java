package org.aldofrankmarco.shak.services;

import com.google.gson.JsonObject;

import org.aldofrankmarco.shak.profile.http.ImagesResponse;
import org.aldofrankmarco.shak.profile.http.GetImagesListResponse;
import org.aldofrankmarco.shak.profile.http.GetUserProfileImageResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ImagesService {

    @GET("image/list/{username}")
    Call<GetImagesListResponse> getAllUserImages(@Path("username") String username);

    @GET("image/profile/{username}")
    Call<GetUserProfileImageResponse> getUserProfileImage(@Path("username") String username);

    @POST("image")
    Call<ImagesResponse> uploadImage(@Body JsonObject imageData);

    @PUT("image/profile/{imageId}/{imageVersion}")
    Call<ImagesResponse> setUserProfilePhoto(@Path("imageId") String imageId, @Path("imageVersion") String imageVersion);

    @PUT("image/cover/{imageId}/{imageVersion}")
    Call<ImagesResponse> setUserCoverPhoto(@Path("imageId") String imageId, @Path("imageVersion") String imageVersion);
}
