package org.aldofrank.shak.people.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrank.shak.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class IsFollowingResponse {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}
