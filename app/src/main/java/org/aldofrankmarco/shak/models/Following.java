package org.aldofrankmarco.shak.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class Following{
    /*
            @SerializedName("_id")
            private Object id;

            @SerializedName("userFollowed")
            private Object followingId;

            // TODO QUESTO GENERA UN ERRORE NON è USERFOLLOWED
            public Object getFollowingId() {
                return followingId;
            }*/
    @SerializedName("userFollowed")
    private JsonElement followingId;

    public Following(String followingId) {
        this.followingId = new Gson().fromJson(followingId, JsonElement.class);
    }

    public JsonArray getFollowingId() {
        if (followingId != null) {
            return followingId.getAsJsonObject().get("following").getAsJsonArray();
        } else {
            return null;
        }
    }
}