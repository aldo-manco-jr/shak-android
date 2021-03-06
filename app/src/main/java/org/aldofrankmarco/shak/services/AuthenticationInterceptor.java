package org.aldofrankmarco.shak.services;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * L'interceptor prende in input la richiesta http e le allega il token, successicamente lo spedisce
 * al server
 */
public class AuthenticationInterceptor implements Interceptor {

    private String authToken;

    public AuthenticationInterceptor(String token) {
        this.authToken = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request httpRequest = chain.request();
        Request.Builder builder = httpRequest.newBuilder()
                .header("authorization", "bearer " + authToken);

        Request httpRequestPlusToken = builder.build();

        return chain.proceed(httpRequestPlusToken);
    }
}