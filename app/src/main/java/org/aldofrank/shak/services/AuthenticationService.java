package org.aldofrank.shak.services;

import org.aldofrank.shak.authentication.http.login.LoginRequest;
import org.aldofrank.shak.authentication.http.login.LoginResponse;
import org.aldofrank.shak.authentication.http.signup.SignupRequest;
import org.aldofrank.shak.authentication.http.signup.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationService {

    @POST("login")
    Call<LoginResponse> login(
            @Body LoginRequest loginRequest
    );

    @POST("register")
    Call<SignupResponse> register(
            @Body SignupRequest signupRequest
    );
}
