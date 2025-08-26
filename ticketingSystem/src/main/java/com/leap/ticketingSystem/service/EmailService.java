package com.leap.ticketingSystem.service;

import com.leap.ticketingSystem.entity.Ticket;
import com.leap.ticketingSystem.entity.User;
import com.leap.ticketingSystem.entity.enums.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendTicketCreatedNotification(Ticket ticket) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(ticket.getCreatedBy().getEmail());
            message.setSubject("Ticket Created - " + ticket.getTicketNumber());
            message.setText(String.format(
                    "Dear %s,\n\nYour ticket has been created successfully.\n\n" +
                            "Ticket Number: %s\nSubject: %s\nPriority: %s\nStatus: %s\n\n" +
                            "We will get back to you soon.\n\nBest regards,\nSupport Team",
                    ticket.getCreatedBy().getFirstName(),
                    ticket.getTicketNumber(),
                    ticket.getSubject(),
                    ticket.getPriority(),
                    ticket.getStatus()
            ));

            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw exception to avoid breaking the ticket creation
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }

    public void sendAssignmentNotification(Ticket ticket, User previousAssignee, User newAssignee) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(newAssignee.getEmail());
            message.setSubject("Ticket Assigned - " + ticket.getTicketNumber());
            message.setText(String.format(
                    "Dear %s,\n\nA ticket has been assigned to you.\n\n" +
                            "Ticket Number: %s\nSubject: %s\nPriority: %s\nCreated by: %s %s\n\n" +
                            "Please review and take appropriate action.\n\nBest regards,\nSupport Team",
                    newAssignee.getFirstName(),
                    ticket.getTicketNumber(),
                    ticket.getSubject(),
                    ticket.getPriority(),
                    ticket.getCreatedBy().getFirstName(),
                    ticket.getCreatedBy().getLastName()
            ));

            mailSender.send(message);

            // Notify ticket creator about assignment
            SimpleMailMessage creatorMessage = new SimpleMailMessage();
            creatorMessage.setFrom(fromEmail);
            creatorMessage.setTo(ticket.getCreatedBy().getEmail());
            creatorMessage.setSubject("Ticket Assignment Update - " + ticket.getTicketNumber());
            creatorMessage.setText(String.format(
                    "Dear %s,\n\nYour ticket has been assigned to a support agent.\n\n" +
                            "Ticket Number: %s\nAssigned to: %s %s\n\n" +
                            "You will be notified of any updates.\n\nBest regards,\nSupport Team",
                    ticket.getCreatedBy().getFirstName(),
                    ticket.getTicketNumber(),
                    newAssignee.getFirstName(),
                    newAssignee.getLastName()
            ));

            mailSender.send(creatorMessage);
        } catch (Exception e) {
            System.err.println("Failed to send assignment notification: " + e.getMessage());
        }
    }

    public void sendStatusChangeNotification(Ticket ticket, TicketStatus oldStatus, TicketStatus newStatus) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(ticket.getCreatedBy().getEmail());
            message.setSubject("Ticket Status Update - " + ticket.getTicketNumber());
            message.setText(String.format(
                    "Dear %s,\n\nYour ticket status has been updated.\n\n" +
                            "Ticket Number: %s\nSubject: %s\nOld Status: %s\nNew Status: %s\n\n" +
                            "Thank you for your patience.\n\nBest regards,\nSupport Team",
                    ticket.getCreatedBy().getFirstName(),
                    ticket.getTicketNumber(),
                    ticket.getSubject(),
                    oldStatus,
                    newStatus
            ));

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send status change notification: " + e.getMessage());
        }
    }
}
