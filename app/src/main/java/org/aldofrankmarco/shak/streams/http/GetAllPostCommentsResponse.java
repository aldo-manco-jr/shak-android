package org.aldofrankmarco.shak.streams.http;

import com.google.gson.annotations.SerializedName;

import org.aldofrankmarco.shak.models.Comment;
import org.aldofrankmarco.shak.models.Post;

import java.util.List;

public class GetAllPostCommentsResponse {

    @SerializedName("message")
    String message;

    @SerializedName("commentsList")
    List<Comment> commentsList;

    public String getMessage() {
        return message;
    }

    public List<Comment> getCommentsList() {
        return commentsList;
    }
}
