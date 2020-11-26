package org.aldofrank.shak.models;

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

    @SerializedName("profileImageId")
    private String profileImageId;

    @SerializedName("profileImageVersion")
    private String profileImageVersion;

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
        private Object followingId;

        public String getFollowingId() {

            String jsonUser = followingId.toString();

            //using a regex to decipher a _id object from DB

            Pattern regularExpression = Pattern.compile("_id=(.*?),");
            Matcher m = regularExpression.matcher(jsonUser);
            String objectId = "";

            if (m.find()){
                objectId = m.group(1);
            }

            return objectId;
        }
    }

    public class Follower{

        @SerializedName("follower")
        private Object followerId;

        public String getFollowerId() {

            String jsonUser = followerId.toString();

            //using a regex to decipher a _id object from DB

            Pattern regularExpression = Pattern.compile("_id=(.*?),");
            Matcher m = regularExpression.matcher(jsonUser);
            String objectId = "";

            if (m.find()){
                objectId = m.group(1);
            }

            return objectId;
        }
    }
}
