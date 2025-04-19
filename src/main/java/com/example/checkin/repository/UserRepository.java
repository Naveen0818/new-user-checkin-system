package com.example.checkin.repository;

import com.example.checkin.model.Role;
import com.example.checkin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByManagerId(Long managerId);
    
    /**
     * Find all users managed by a specific manager
     * @param manager The manager user entity
     * @return List of users that report to the manager
     */
    List<User> findByManager(User manager);
    
    List<User> findByLocationId(Long locationId);
    
    List<User> findByRole(Role role);
}
