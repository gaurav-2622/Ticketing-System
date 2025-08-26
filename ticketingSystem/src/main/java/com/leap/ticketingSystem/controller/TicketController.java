package com.leap.ticketingSystem.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getUserTickets() {
        User currentUser = getCurrentUser();
        List<Ticket> tickets = ticketService.getUserTickets(currentUser);

        List<TicketResponse> ticketResponses = tickets.stream()
                .map(TicketResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ticketResponses);
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<TicketResponse>> getAssignedTickets() {
        User currentUser = getCurrentUser();
        List<Ticket> tickets = ticketService.getAssignedTickets(currentUser);

        List<TicketResponse> ticketResponses = tickets.stream()
                .map(TicketResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ticketResponses);
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketCreateRequest request) {
        try {
            User currentUser = getCurrentUser();

            Priority priority = Priority.valueOf(request.getPriority().toUpperCase());
            Ticket ticket = ticketService.createTicket(request.getSubject(),
                    request.getDescription(), priority, currentUser);

            return ResponseEntity.ok(new TicketResponse(ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicket(@PathVariable Long id) {
        try {
            Ticket ticket = ticketService.getTicketById(id)
                    .orElseThrow(() -> new RuntimeException("Ticket not found"));

            return ResponseEntity.ok(new TicketResponse(ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTicketStatus(@PathVariable Long id,
                                                @RequestBody StatusUpdateRequest request) {
        try {
            User currentUser = getCurrentUser();
            TicketStatus status = TicketStatus.valueOf(request.getStatus().toUpperCase());

            Ticket ticket = ticketService.updateTicketStatus(id, status, currentUser);
            return ResponseEntity.ok(new TicketResponse(ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignTicket(@PathVariable Long id,
                                          @RequestBody AssignTicketRequest request) {
        try {
            User currentUser = getCurrentUser();
            Ticket ticket = ticketService.assignTicket(id, request.getAssigneeId(), currentUser);
            return ResponseEntity.ok(new TicketResponse(ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id,
                                        @RequestBody CommentCreateRequest request) {
        try {
            User currentUser = getCurrentUser();
            Comment comment = ticketService.addComment(id, request.getContent(),
                    currentUser, request.isInternal());

            return ResponseEntity.ok(new CommentResponse(comment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getTicketComments(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            List<Comment> comments = ticketService.getTicketComments(id, currentUser);

            List<CommentResponse> commentResponses = comments.stream()
                    .map(CommentResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(commentResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}