package com.app.web.crypto.api.model;

public class CommentDTO {

    private String content;
    private String commentedBy;

    public CommentDTO() {
    }
    public CommentDTO(String content, String commentedBy) {
        this.content = content;
        this.commentedBy = commentedBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }
}
