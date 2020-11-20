package org.aldofrank.shak.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Post {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("username")
    private String usernamePublisher;

    @SerializedName("post")
    private String postContent;

    @SerializedName("imageVersion")
    private String imageVersion;

    @SerializedName("imageId")
    private String imageId;

    @SerializedName("comments")
    private ArrayList<Comment> arrayComments;

    @SerializedName("total_likes")
    private int totalLikes;

    @SerializedName("likes")
    private ArrayList<Like> arrayLikes;

    @SerializedName("created_at")
    private String createdAt;

    public String getUserId() {
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

    public ArrayList<Comment> getArrayComments() {
        return arrayComments;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public ArrayList<Like> getArrayLikes() {
        return arrayLikes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    class Comment{

        @SerializedName("user_id")
        private String userId;

        @SerializedName("username")
        private String usernamePublisher;

        @SerializedName("comment_text")
        private String commentContent;

        @SerializedName("created_at")
        private String createdAt;

        public String getUserId() {
            return userId;
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

    class Like{

        @SerializedName("username")
        private String usernamePublisher;

        public String getUsernamePublisher() {
            return usernamePublisher;
        }
    }
}
