package org.aldofrankmarco.shak.chat.http;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {

    @GET("chat-messages/{senderId}/{receiverId}")
    Call<GetAllConversationMessagesResponse> getAllConversationMessages(@Path("senderId") String senderId, @Path("receiverId") String receiverId);

    @GET("receiver-messages/{sender}/{receiver}")
    Call<Object> markReceiverMessageAsRead(@Path("sender") String senderUsername, @Path("receiver") String receiverUsername);

    @GET("mark-all-messages")
    Call<Object> markAllReceiverMessagesAsRead();

    @POST("chat-messages/{senderId}/{receiverId}")
    Call<Object> sendMessage(@Body SendMessageResponse sendMessageResponse);
}
