package com.leap.ticketingSystem.dto;

import jakarta.validation.constraints.NotNull;

public class AssignTicketRequest {
    @NotNull
    private Long assigneeId;

    public AssignTicketRequest() {}

    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
}