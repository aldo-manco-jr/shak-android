package org.aldofrankmarco.shak.streams.http;

public class AddCommentRequest {

    private String postId;

    private String comment;

    public AddCommentRequest(String postId, String comment) {
        this.postId = postId;
        this.comment = comment;
    }

    public String getPostId() {
        return postId;
    }

    public String getComment() {
        return comment;
    }
}
