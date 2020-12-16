package org.aldofrankmarco.shak.models;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Conversation {

    @SerializedName("_id")
    private JsonElement objectId;

    @SerializedName("conversationId")
    private JsonElement conversationId;

    @SerializedName("sender")
    private String usernameFirstMember;

    @SerializedName("receiver")
    private String usernameSecondMember;

    @SerializedName("message")
    private List<Message> arrayMessages;

    public String getConversationId() {
        return conversationId.getAsString();
    }

    public String getUsernameFirstMember() {
        return usernameFirstMember;
    }

    public String getUsernameSecondMember() {
        return usernameSecondMember;
    }

    public List<Message> getArrayMessages() {
        return arrayMessages;
    }
}
