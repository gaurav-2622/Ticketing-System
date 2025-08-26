package com.leap.ticketingSystem.repository;

import com.leap.ticketingSystem.entity.User;
import com.leap.ticketingSystem.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByIsActiveTrue();

    @Query("SELECT u FROM User u WHERE u.role = 'SUPPORT_AGENT' AND u.isActive = true")
    List<User> findActiveSupportAgents();
}
