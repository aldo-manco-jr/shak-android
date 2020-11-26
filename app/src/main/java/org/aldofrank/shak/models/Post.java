package org.aldofrank.shak.models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Post {

    @SerializedName("_id")
    private Object postId;

    @SerializedName("user_id")
    private User userId;

    @SerializedName("username")
    private String usernamePublisher;

    @SerializedName("post")
    private String postContent;

    @SerializedName("imageVersion")
    private String imageVersion;

    @SerializedName("imageId")
    private String imageId;

    @SerializedName("comments")
    private List<Comment> arrayComments;

    @SerializedName("total_likes")
    private int totalLikes;

    @SerializedName("likes")
    private List<Like> arrayLikes;

    @SerializedName("created_at")
    private String createdAt;

    public Object getPostId() {
        return postId;
    }

    public User getUserId() {

        /*String jsonUser = userId.toString();

        //using a regex to decipher a _id object from DB

        Pattern regularExpression = Pattern.compile("_id=(.*?),");
        Matcher m = regularExpression.matcher(jsonUser);
        String objectId = "";

        if (m.find()){
             objectId = m.group(1);
        }

        return objectId;*/

        // return userId.get("_id").getAsString();
        return userId;
    }

    public String getUsernamePublisher() {
        return usernamePublisher;
    }

    public String getPostContent() {
        return postContent;
    }

    public String getImageVersion() {
        return imageVersion;
    }

    public String getImageId() {
        return imageId;
    }

    public List<Comment> getArrayComments() {
        return arrayComments;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public List<Like> getArrayLikes() {
        return arrayLikes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public class Comment{

        @SerializedName("user_id")
        private Object userId;

        @SerializedName("username")
        private String usernamePublisher;

        @SerializedName("comment_text")
        private String commentContent;

        @SerializedName("created_at")
        private String createdAt;

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

    public class Like{

        @SerializedName("username")
        private String usernamePublisher;

        public String getUsernamePublisher() {
            return usernamePublisher;
        }
    }
}
