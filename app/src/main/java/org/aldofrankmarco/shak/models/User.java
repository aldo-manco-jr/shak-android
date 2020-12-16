package org.aldofrankmarco.shak.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {

    @SerializedName("_id")
    private String _id;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("posts")
    private List<Post> arrayPosts;

    @SerializedName("following")
    private List<Following> arrayFollowing;

    @SerializedName("followers")
    private List<Follower> arrayFollowers;

    @SerializedName("notifications")
    private List<Notification> arrayNotifications;

    @SerializedName("images")
    private List<Image> arrayImages;

    @SerializedName("profileImageId")
    private String profileImageId;

    @SerializedName("profileImageVersion")
    private String profileImageVersion;

    @SerializedName("coverImageId")
    private String coverImageId;

    @SerializedName("coverImageVersion")
    private String coverImageVersion;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Post> getArrayPosts() {
        return arrayPosts;
    }

    public List<Following> getArrayFollowing() {
        return arrayFollowing;
    }

    public List<Follower> getArrayFollowers() {
        return arrayFollowers;
    }

    public String getProfileImageId() {
        return profileImageId;
    }

    public String getProfileImageVersion() {
        return profileImageVersion;
    }

    public String getCoverImageId() {
        return coverImageId;
    }

    public String getCoverImageVersion() {
        return coverImageVersion;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getId() {
        return _id;
    }

    public List<Notification> getArrayNotifications() {
        return arrayNotifications;
    }

    public List<Image> getArrayImages() {
        return arrayImages;
    }

    public boolean addFollowtoFollowings(String userId) {
        Following userFollowed = new Following(userId);
        boolean isAdded = arrayFollowing.add(userFollowed);
        return isAdded;
    }

    public boolean removeLikeFromArray(String userId) {
        Following userFollowRemoved = null;
        boolean isRemoved = false;

        for (Following following : arrayFollowing) {
            if (following.getFollowingId().equals(userId)) {
                userFollowRemoved = following;
            }
        }

        if (userFollowRemoved != null){
            isRemoved = arrayFollowing.remove(userFollowRemoved);
        }

        return isRemoved;
    }
}