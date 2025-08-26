package com.leap.ticketingSystem.repository;

import com.leap.ticketingSystem.entity.Comment;
import com.leap.ticketingSystem.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicketOrderByCreatedAtAsc(Ticket ticket);
    List<Comment> findByTicketAndIsInternalFalseOrderByCreatedAtAsc(Ticket ticket);
    long countByTicket(Ticket ticket);
}
