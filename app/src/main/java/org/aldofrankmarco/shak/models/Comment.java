package org.aldofrankmarco.shak.models;

import com.google.gson.annotations.SerializedName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Comment{

    @SerializedName("_id")
    private Object commentId;

    @SerializedName("user_id")
    private Object userId;

    @SerializedName("username")
    private String usernamePublisher;

    @SerializedName("comment_text")
    private String commentContent;

    @SerializedName("created_at")
    private String createdAt;

    public Comment(String usernamePublisher, String commentContent) {
        this.usernamePublisher = usernamePublisher;
        this.commentContent = commentContent;
    }

    public Object getCommentId() {
        return commentId;
    }

    public String getUserId() {

        String jsonUser = userId.toString();

        //using a regex to decipher a _id object from DB

        Pattern regularExpression = Pattern.compile("_id=(.*?),");
        Matcher m = regularExpression.matcher(jsonUser);
        String objectId = "";

        if (m.find()){
            objectId = m.group(1);
        }

        return objectId;
    }

    public String getUsernamePublisher() {
        return usernamePublisher;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}