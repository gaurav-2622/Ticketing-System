package com.leap.ticketingSystem.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentCreateRequest {
    @NotBlank
    private String content;

    private boolean internal = false;

    public CommentCreateRequest() {}

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isInternal() { return internal; }
    public void setInternal(boolean internal) { this.internal = internal; }
}