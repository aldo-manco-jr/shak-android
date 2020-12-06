package org.aldofrank.shak.models;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConversationList {

    @SerializedName("_id")
    private JsonElement objectId;

    @SerializedName("partecipants")
    private List<ConversationPartecipants> arrayConversationsPartecipants;

    public List<ConversationPartecipants> getArrayConversationsPartecipants() {
        return arrayConversationsPartecipants;
    }

    public class ConversationPartecipants{

        @SerializedName("_id")
        private JsonElement objectId;

        @SerializedName("senderId")
        private JsonElement senderId;

        @SerializedName("receiverId")
        private JsonElement receiverId;

        public String getSenderId() {
            return senderId.getAsString();
        }

        public String getReceiverId() {
            return receiverId.getAsString();
        }
    }
}
