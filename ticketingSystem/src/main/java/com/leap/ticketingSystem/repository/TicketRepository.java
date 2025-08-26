package com.leap.ticketingSystem.repository;

import com.leap.ticketingSystem.entity.Ticket;
import com.leap.ticketingSystem.entity.User;
import com.leap.ticketingSystem.entity.enums.Priority;
import com.leap.ticketingSystem.entity.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    List<Ticket> findByCreatedBy(User createdBy);
    List<Ticket> findByAssignedTo(User assignedTo);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByPriority(Priority priority);

    Page<Ticket> findByCreatedBy(User createdBy, Pageable pageable);
    Page<Ticket> findByAssignedTo(User assignedTo, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.createdBy = :user OR t.assignedTo = :user")
    List<Ticket> findByCreatedByOrAssignedTo(@Param("user") User user);

    @Query("SELECT t FROM Ticket t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:assignedTo IS NULL OR t.assignedTo = :assignedTo) AND " +
            "(LOWER(t.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Ticket> findTicketsWithFilters(
            @Param("status") TicketStatus status,
            @Param("priority") Priority priority,
            @Param("assignedTo") User assignedTo,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    long countByStatus(TicketStatus status);
    long countByPriority(Priority priority);
    long countByCreatedBy(User createdBy);
    long countByAssignedTo(User assignedTo);
}
