package org.aldofrankmarco.shak.models;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

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