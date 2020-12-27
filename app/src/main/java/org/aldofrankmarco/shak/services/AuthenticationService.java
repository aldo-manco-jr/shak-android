package org.aldofrankmarco.shak.services;

import com.google.gson.JsonObject;

import org.aldofrankmarco.shak.authentication.http.LoginRequest;
import org.aldofrankmarco.shak.authentication.http.LoginResponse;
import org.aldofrankmarco.shak.authentication.http.SignupRequest;
import org.aldofrankmarco.shak.authentication.http.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthenticationService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/signup")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    @GET("auth/login/face-authentication/{username}")
    Call<LoginResponse> loginFaceAuthentication(@Path("username") String username);

    // @Body JsonObject imageData
}
