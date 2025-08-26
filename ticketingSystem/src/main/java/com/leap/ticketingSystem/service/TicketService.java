package com.leap.ticketingSystem.service;


import com.leap.ticketingSystem.entity.Comment;
import com.leap.ticketingSystem.entity.Ticket;
import com.leap.ticketingSystem.entity.User;
import com.leap.ticketingSystem.entity.enums.Priority;
import com.leap.ticketingSystem.entity.enums.Role;
import com.leap.ticketingSystem.entity.enums.TicketStatus;
import com.leap.ticketingSystem.repository.CommentRepository;
import com.leap.ticketingSystem.repository.TicketRepository;
import com.leap.ticketingSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public Ticket createTicket(String subject, String description, Priority priority, User createdBy) {
        Ticket ticket = new Ticket(subject, description, priority, createdBy);
        Ticket savedTicket = ticketRepository.save(ticket);

        // Send notification email
        emailService.sendTicketCreatedNotification(savedTicket);

        return savedTicket;
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public Optional<Ticket> getTicketByNumber(String ticketNumber) {
        return ticketRepository.findByTicketNumber(ticketNumber);
    }

    public List<Ticket> getUserTickets(User user) {
        return ticketRepository.findByCreatedBy(user);
    }

    public List<Ticket> getAssignedTickets(User assignee) {
        return ticketRepository.findByAssignedTo(assignee);
    }

    public Page<Ticket> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    public Page<Ticket> searchTickets(TicketStatus status, Priority priority, User assignedTo,
                                      String searchTerm, Pageable pageable) {
        return ticketRepository.findTicketsWithFilters(status, priority, assignedTo, searchTerm, pageable);
    }

    public Ticket updateTicketStatus(Long ticketId, TicketStatus newStatus, User updatedBy) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check permissions
        if (!canUserModifyTicket(ticket, updatedBy)) {
            throw new RuntimeException("Access denied");
        }

        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        } else if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        Ticket updatedTicket = ticketRepository.save(ticket);

        // Send notification if status changed
        if (oldStatus != newStatus) {
            emailService.sendStatusChangeNotification(updatedTicket, oldStatus, newStatus);
        }

        return updatedTicket;
    }

    public Ticket assignTicket(Long ticketId, Long assigneeId, User assignedBy) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found"));

        // Check permissions
        if (!canUserAssignTicket(ticket, assignedBy)) {
            throw new RuntimeException("Access denied");
        }

        User previousAssignee = ticket.getAssignedTo();
        ticket.setAssignedTo(assignee);

        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);

        // Send assignment notification
        emailService.sendAssignmentNotification(updatedTicket, previousAssignee, assignee);

        return updatedTicket;
    }

    public Comment addComment(Long ticketId, String content, User commentBy, boolean isInternal) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check permissions
        if (!canUserCommentOnTicket(ticket, commentBy)) {
            throw new RuntimeException("Access denied");
        }

        Comment comment = new Comment(ticket, commentBy, content);
        comment.setIsInternal(isInternal);

        return commentRepository.save(comment);
    }

    public List<Comment> getTicketComments(Long ticketId, User requestedBy) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check permissions
        if (!canUserViewTicket(ticket, requestedBy)) {
            throw new RuntimeException("Access denied");
        }

        // Regular users can't see internal comments
        if (requestedBy.getRole() == Role.USER) {
            return commentRepository.findByTicketAndIsInternalFalseOrderByCreatedAtAsc(ticket);
        }

        return commentRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }

    // Permission helper methods
    private boolean canUserViewTicket(Ticket ticket, User user) {
        if (user.getRole() == Role.ADMIN) return true;
        if (user.getRole() == Role.SUPPORT_AGENT) return true;
        return ticket.getCreatedBy().equals(user);
    }

    private boolean canUserModifyTicket(Ticket ticket, User user) {
        if (user.getRole() == Role.ADMIN) return true;
        if (user.getRole() == Role.SUPPORT_AGENT && ticket.getAssignedTo() != null
                && ticket.getAssignedTo().equals(user)) return true;
        return ticket.getCreatedBy().equals(user) && ticket.getStatus() == TicketStatus.OPEN;
    }

    private boolean canUserAssignTicket(Ticket ticket, User user) {
        if (user.getRole() == Role.ADMIN) return true;
        return user.getRole() == Role.SUPPORT_AGENT;
    }

    private boolean canUserCommentOnTicket(Ticket ticket, User user) {
        return canUserViewTicket(ticket, user);
    }
}
