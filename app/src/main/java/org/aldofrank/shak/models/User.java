package org.aldofrank.shak.models;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import org.aldofrank.shak.models.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {

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

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public class Following{

        @SerializedName("userFollowed")
        private JsonElement followingId;

        public String getFollowingId() {
            return followingId.getAsString();
        }
    }

    public class Follower{

        @SerializedName("follower")
        private JsonElement followerId;

        public String getFollowerId() {
            return followerId.getAsString();
        }
    }

    public class Notification{

        @SerializedName("_id")
        private JsonElement notificationId;

        @SerializedName("senderId")
        private JsonElement userId;

        @SerializedName("message")
        private String notificationContent;

        @SerializedName("viewProfile")
        private boolean isAboutViewedProfile;

        @SerializedName("created")
        private String createdAt;

        @SerializedName("read")
        private boolean isRead;

        public String getNotificationId() {
            return notificationId.getAsString();
        }

        public String getUserId() {
            return userId.getAsString();
        }

        public String getNotificationContent() {
            return notificationContent;
        }

        public boolean isAboutViewedProfile() {
            return isAboutViewedProfile;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public boolean isRead() {
            return isRead;
        }
    }

    public class Image{

        @SerializedName("_id")
        private JsonElement imageDatabaseId;

        @SerializedName("imageId")
        private String imageId;

        @SerializedName("imageVersion")
        private String imageVersion;

        public String getImageDatabaseId() {
            return imageDatabaseId.getAsString();
        }

        public String getImageId() {
            return imageId;
        }

        public String getImageVersion() {
            return imageVersion;
        }
    }
}