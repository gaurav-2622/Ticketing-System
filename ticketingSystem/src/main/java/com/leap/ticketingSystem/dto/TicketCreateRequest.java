package com.leap.ticketingSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TicketCreateRequest {
    @NotBlank
    @Size(max = 200)
    private String subject;

    @NotBlank
    private String description;

    @NotBlank
    private String priority;

    public TicketCreateRequest() {}

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
