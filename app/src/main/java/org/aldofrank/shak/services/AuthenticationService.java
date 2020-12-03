package org.aldofrank.shak.services;

import org.aldofrank.shak.authentication.http.LoginRequest;
import org.aldofrank.shak.authentication.http.LoginResponse;
import org.aldofrank.shak.authentication.http.SignupRequest;
import org.aldofrank.shak.authentication.http.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationService {

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("register")
    Call<SignupResponse> register(@Body SignupRequest signupRequest);
}
