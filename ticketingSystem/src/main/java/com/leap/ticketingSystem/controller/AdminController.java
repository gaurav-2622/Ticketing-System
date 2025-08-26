package com.leap.ticketingSystem.controller;

import com.leap.ticketingSystem.dto.*;
import com.leap.ticketingSystem.entity.Ticket;
import com.leap.ticketingSystem.entity.User;
import com.leap.ticketingSystem.entity.enums.Priority;
import com.leap.ticketingSystem.entity.enums.Role;
import com.leap.ticketingSystem.entity.enums.TicketStatus;
import com.leap.ticketingSystem.service.TicketService;
import com.leap.ticketingSystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userResponses);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            Role role = Role.valueOf(request.getRole().toUpperCase());
            User user = userService.createUser(request.getUsername(), request.getEmail(),
                    request.getPassword(), request.getFirstName(), request.getLastName(), role);

            return ResponseEntity.ok(new UserResponse(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody UpdateUserRequest request) {
        try {
            Role role = Role.valueOf(request.getRole().toUpperCase());
            User user = userService.updateUser(id, request.getFirstName(),
                    request.getLastName(), request.getEmail(), role, request.getIsActive());

            return ResponseEntity.ok(new UserResponse(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/tickets")
    public ResponseEntity<Page<TicketResponse>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) String search) {

        Sort sort = sortDir.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        TicketStatus ticketStatus = status != null ? TicketStatus.valueOf(status.toUpperCase()) : null;
        Priority ticketPriority = priority != null ? Priority.valueOf(priority.toUpperCase()) : null;
        User assignedUser = assignedTo != null ? userService.getUserById(assignedTo).orElse(null) : null;

        Page<Ticket> tickets = ticketService.searchTickets(ticketStatus, ticketPriority,
                assignedUser, search, pageable);

        Page<TicketResponse> ticketResponses = tickets.map(TicketResponse::new);

        return ResponseEntity.ok(ticketResponses);
    }

    @GetMapping("/support-agents")
    public ResponseEntity<List<UserResponse>> getSupportAgents() {
        List<User> agents = userService.getActiveSupportAgents();
        List<UserResponse> agentResponses = agents.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(agentResponses);
    }
}
