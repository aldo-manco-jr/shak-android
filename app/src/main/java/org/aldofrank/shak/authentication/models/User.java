package org.aldofrank.shak.authentication.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class User {

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    //private ArrayList<Post> arrayPosts;
    private ArrayList<User> arrayFollowing;
    private ArrayList<User> arrayFollowers;

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

    public ArrayList<User> getArrayFollowing() {
        return arrayFollowing;
    }

    public ArrayList<User> getArrayFollowers() {
        return arrayFollowers;
    }

    public String getProfileImageId() {
        return profileImageId;
    }

    public String getProfileImageVersion() {
        return profileImageVersion;
    }
}
