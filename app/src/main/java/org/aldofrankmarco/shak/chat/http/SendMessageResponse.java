package org.aldofrankmarco.shak.chat.http;

class SendMessageResponse {

    private String receiverId;

    private String receiverUsername;

    private String messageContent;

    public SendMessageResponse(String receiverId, String receiverUsername, String messageContent) {
        this.receiverId = receiverId;
        this.receiverUsername = receiverUsername;
        this.messageContent = messageContent;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public String getMessageContent() {
        return messageContent;
    }
}
