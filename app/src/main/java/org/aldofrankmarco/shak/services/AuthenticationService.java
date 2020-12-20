package org.aldofrankmarco.shak.services;

import org.aldofrankmarco.shak.authentication.http.LoginRequest;
import org.aldofrankmarco.shak.authentication.http.LoginResponse;
import org.aldofrankmarco.shak.authentication.http.SignupRequest;
import org.aldofrankmarco.shak.authentication.http.SignupResponse;
import org.aldofrankmarco.shak.settings.http.ChangePasswordRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/signup")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    @POST("auth/change-password")
    Call<Object> changePassword(@Body ChangePasswordRequest changePasswordRequest);
}
