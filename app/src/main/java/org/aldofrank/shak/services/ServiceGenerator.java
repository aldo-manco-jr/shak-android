package org.aldofrank.shak.services;

import android.text.TextUtils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    // create an instance of an http client
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    // build a new instance of Retrofit
    // setting the URL of the web server
    // creating and setting an instance of GSON converter
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:3000/api/shak/")
                    .addConverterFactory(GsonConverterFactory.create());

    // Retrofit adapts a Java interface to HTTP calls by using annotations on the declared methods to define how requests are made.
    // Create instances using Builder and pass your interface to {@link #create} to generate an implementation.
    private static Retrofit retrofit;

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, "");
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken) {
        if (!authToken.isEmpty()) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                // add the interceptor to the http client
                httpClient.addInterceptor(interceptor);

                // assign to built retrofit instance the http client with interceptor
                builder.client(httpClient.build());

                // create the retrofit instance using the configured values in built retrofit instance
                retrofit = builder.build();
            }
        }else {
            // aggiunta client in retrofit
            builder.client(httpClient.build());

            retrofit = builder.build();
        }

        // create an implementation of the API endpoints defined by the service's interface
        return retrofit.create(serviceClass);
    }
}
