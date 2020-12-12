package org.aldofrankmarco.shak.chat.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.Conversation;

import java.util.List;

class GetAllConversationMessagesResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("messages")
    private List<Conversation.Message> arrayConversationMessages;

    public String getMessage() {
        return message;
    }

    public List<Conversation.Message> getArrayConversationMessages() {
        return arrayConversationMessages;
    }
}
