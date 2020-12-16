package org.aldofrankmarco.shak.chat.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.Conversation;
import org.aldofrankmarco.shak.models.Message;

import java.util.List;

class GetAllConversationMessagesResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("messages")
    private List<Message> arrayConversationMessages;

    public String getMessage() {
        return message;
    }

    public List<Message> getArrayConversationMessages() {
        return arrayConversationMessages;
    }
}
