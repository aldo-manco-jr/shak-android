package org.aldofrank.shak.models;

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

    public class Following{

        @SerializedName("_id")
        private Object id;

        @SerializedName("userFollowed")
        private Object followingId;

        // TODO QUESTO GENERA UN ERRORE NON è USERFOLLOWED
        public Object getFollowingId() {
            return followingId;
        }
    }

    public class Follower{
        @SerializedName("follower")
        //JsonObject non funziona
        //TODO il dato offerto dal server è un JsonElement, ma in realtà è un JsonObject
        private JsonElement followerId;

        public JsonArray getFollowerId() {
            return followerId.getAsJsonObject().get("followers").getAsJsonArray();
            //return followerId.getAsJsonObject().get("followers").getAsJsonArray().get(0).getAsJsonArray();
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
