package org.aldofrankmarco.shak.streams.http;

import org.aldofrankmarco.shak.models.Comment;
import org.aldofrankmarco.shak.models.Post;

public class DeleteCommentRequest {

    private String postId;

    private Comment comment;

    public DeleteCommentRequest(String postId, Comment comment) {
        this.postId = postId;
        this.comment = comment;
    }

    public String getPostId() {
        return postId;
    }

    public Comment getComment() {
        return comment;
    }
}
