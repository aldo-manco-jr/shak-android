package org.aldofrankmarco.shak.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Post implements Comparable<Post> {

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

    @SerializedName("total_comments")
    private int totalComments;

    @SerializedName("likes")
    private List<Like> arrayLikes;

    @SerializedName("is_liked")
    private boolean isLiked;

    @SerializedName("created_at")
    private String createdAt;

    public boolean addLiketoArray(String usernamePublisher) {
        Like like = new Like(usernamePublisher);
        boolean isAdded = arrayLikes.add(like);
        if (isAdded){
            totalLikes++;
        }

        return isAdded;
    }

    public boolean getIsLiked() {
        return isLiked;
    }

    public void putIsLiked(boolean newState) {
        if (newState){
            totalLikes++;
        } else {
            totalLikes--;
        }
        
        isLiked = newState;
    }

    public boolean removeLikeFromArray(String usernamePublisher) {
        Like removeLike = null;
        boolean isRemoved = false;

        for (Like like: arrayLikes) {
            if (like.getUsernamePublisher().equals(usernamePublisher)) {
                removeLike = like;
            }
        }

        if (removeLike != null){
            isRemoved = arrayLikes.remove(removeLike);

            if (isRemoved){
                totalLikes--;
            }
        }

        return isRemoved;
    }

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

    public int getTotalComments() {
        return totalComments;
    }

    public List<Like> getArrayLikes() {
        return arrayLikes;
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
        if (!(o instanceof Post))
            return false;
        Post anotherElement = (Post) o;
        return this.createdAt.equals(anotherElement.createdAt)
                && this.usernamePublisher.equals(anotherElement.usernamePublisher);
    }

    @Override
    public int compareTo(Post post) {
        int usernameComparationResult = this.usernamePublisher.compareTo(post.usernamePublisher);
        int createdAtComparationResult = this.createdAt.compareTo(post.createdAt);

        if (usernameComparationResult == 0 && createdAtComparationResult == 0)
            return 0;
        else
            return createdAtComparationResult;
    }
}
