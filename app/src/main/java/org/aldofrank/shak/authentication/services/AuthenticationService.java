package org.aldofrank.shak.authentication.services;

import org.aldofrank.shak.authentication.models.LoginRequest;
import org.aldofrank.shak.authentication.models.LoginResponse;
import org.aldofrank.shak.authentication.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationService {

    @POST("loginRequest")
    Call<LoginResponse> login(
            @Body LoginRequest loginRequest
    );
}
