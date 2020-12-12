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

    public class Message{

        @SerializedName("_id")
        private JsonElement objectId;

        @SerializedName("senderId")
        private JsonElement senderId;

        @SerializedName("receiverId")
        private JsonElement receiverId;

        @SerializedName("senderName")
        private String senderUsername;

        @SerializedName("receiverName")
        private String receiverUsername;

        @SerializedName("body")
        private String messageContent;

        @SerializedName("isRead")
        private boolean isMessageRead;

        @SerializedName("createdAt")
        private String createdAt;

        public String getSenderId() {
            return senderId.getAsString();
        }

        public String getReceiverId() {
            return receiverId.getAsString();
        }

        public String getSenderUsername() {
            return senderUsername;
        }

        public String getReceiverUsername() {
            return receiverUsername;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public boolean isMessageRead() {
            return isMessageRead;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
