package org.aldofrank.shak.models;

import com.google.gson.annotations.SerializedName;

import org.aldofrank.shak.models.Post;

import java.util.ArrayList;

public class User {

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("posts")
    private ArrayList<Post> arrayPosts;

    @SerializedName("following")
    private ArrayList<Following> arrayFollowing;

    @SerializedName("followers")
    private ArrayList<Follower> arrayFollowers;

    @SerializedName("profileImageId")
    private String profileImageId;

    @SerializedName("profileImageVersion")
    private String profileImageVersion;

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Post> getArrayPosts() {
        return arrayPosts;
    }

    public ArrayList<Following> getArrayFollowing() {
        return arrayFollowing;
    }

    public ArrayList<Follower> getArrayFollowers() {
        return arrayFollowers;
    }

    public String getProfileImageId() {
        return profileImageId;
    }

    public String getProfileImageVersion() {
        return profileImageVersion;
    }

    class Following{

        @SerializedName("userFollowed")
        private String followingId;

        public String getFollowingId() {
            return followingId;
        }
    }

    class Follower{

        @SerializedName("follower")
        private String followerId;

        public String getFollowerId() {
            return followerId;
        }
    }
}
