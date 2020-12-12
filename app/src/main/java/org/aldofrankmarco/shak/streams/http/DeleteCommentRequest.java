package org.aldofrankmarco.shak.streams.http;

import org.aldofrankmarco.shak.models.Post;

public class DeleteCommentRequest {

    private String postId;

    private Post.Comment comment;

    public DeleteCommentRequest(String postId, Post.Comment comment) {
        this.postId = postId;
        this.comment = comment;
    }

    public String getPostId() {
        return postId;
    }

    public Post.Comment getComment() {
        return comment;
    }
}
