package com.app.web.crypto.api.model;

public class CommentDTO {

    private String content;
    private String commentedBy;
    private String commentedAt;

    public CommentDTO() {
    }
    public CommentDTO(String content, String commentedBy) {
        this.content = content;
        this.commentedBy = commentedBy;
    }

    public CommentDTO(String content, String commentedBy, String commentedAt) {
        this.content = content;
        this.commentedBy = commentedBy;
        this.commentedAt = commentedAt;
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

    public String getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(String commentedAt) {
        this.commentedAt = commentedAt;
    }
}
