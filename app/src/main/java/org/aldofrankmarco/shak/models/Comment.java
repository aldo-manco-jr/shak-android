package org.aldofrankmarco.shak.models;

import com.google.gson.annotations.SerializedName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Comment implements Comparable<Comment> {

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

    @Override
    public int hashCode() {
        return this.usernamePublisher.hashCode() + this.createdAt.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Comment))
            return false;
        Comment anotherElement = (Comment) o;
        return this.createdAt.equals(anotherElement.createdAt)
                && this.usernamePublisher.equals(anotherElement.usernamePublisher);
    }

    @Override
    public int compareTo(Comment comment) {
        int usernameComparationResult = this.usernamePublisher.compareTo(comment.usernamePublisher);
        int createdAtComparationResult = this.createdAt.compareTo(comment.createdAt);

        if (usernameComparationResult == 0 && createdAtComparationResult == 0)
            return 0;
        else
            return createdAtComparationResult;
    }
}