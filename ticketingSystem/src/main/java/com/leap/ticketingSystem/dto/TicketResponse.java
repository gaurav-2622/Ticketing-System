package com.leap.ticketingSystem.dto;

import com.leap.ticketingSystem.entity.Ticket;
import com.leap.ticketingSystem.entity.enums.Priority;
import com.leap.ticketingSystem.entity.enums.TicketStatus;

import java.time.LocalDateTime;
public class TicketResponse {
    private Long id;
    private String ticketNumber;
    private String subject;
    private String description;
    private Priority priority;
    private TicketStatus status;
    private UserResponse createdBy;
    private UserResponse assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    public TicketResponse() {}

    public TicketResponse(Ticket ticket) {
        this.id = ticket.getId();
        this.ticketNumber = ticket.getTicketNumber();
        this.subject = ticket.getSubject();
        this.description = ticket.getDescription();
        this.priority = ticket.getPriority();
        this.status = ticket.getStatus();
        this.createdBy = new UserResponse(ticket.getCreatedBy());
        this.assignedTo = ticket.getAssignedTo() != null ? new UserResponse(ticket.getAssignedTo()) : null;
        this.createdAt = ticket.getCreatedAt();
        this.updatedAt = ticket.getUpdatedAt();
        this.resolvedAt = ticket.getResolvedAt();
        this.closedAt = ticket.getClosedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public UserResponse getCreatedBy() { return createdBy; }
    public void setCreatedBy(UserResponse createdBy) { this.createdBy = createdBy; }

    public UserResponse getAssignedTo() { return assignedTo; }
    public void setAssignedTo(UserResponse assignedTo) { this.assignedTo = assignedTo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
}
