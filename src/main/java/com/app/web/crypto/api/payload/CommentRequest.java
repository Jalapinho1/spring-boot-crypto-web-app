package com.app.web.crypto.api.payload;

public class CommentRequest {

    private Long fileMetadataId;
    private String commentedBy;
    private String content;

    public Long getFileMetadataId() {
        return fileMetadataId;
    }

    public void setFileMetadataId(Long fileMetadataId) {
        this.fileMetadataId = fileMetadataId;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
