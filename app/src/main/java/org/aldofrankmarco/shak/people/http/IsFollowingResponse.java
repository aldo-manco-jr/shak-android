package org.aldofrankmarco.shak.people.http;

import com.google.gson.annotations.SerializedName;

public class IsFollowingResponse {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}
