package org.aldofrankmarco.shak.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class Follower{
    @SerializedName("follower")
    //JsonObject non funziona
    //TODO il dato offerto dal server è un JsonElement, ma in realtà è un JsonObject
    private JsonElement followerId;

    public JsonArray getFollowerId() {
        return followerId.getAsJsonObject().get("followers").getAsJsonArray();
    }
}