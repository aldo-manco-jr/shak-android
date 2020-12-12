package org.aldofrankmarco.shak.services;

import org.aldofrankmarco.shak.authentication.http.LoginRequest;
import org.aldofrankmarco.shak.authentication.http.LoginResponse;
import org.aldofrankmarco.shak.authentication.http.SignupRequest;
import org.aldofrankmarco.shak.authentication.http.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationService {

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("register")
    Call<SignupResponse> register(@Body SignupRequest signupRequest);
}
