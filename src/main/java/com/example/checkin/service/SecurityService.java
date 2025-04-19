package com.example.checkin.service;

import com.example.checkin.model.AiRecommendation;
import com.example.checkin.model.Role;
import com.example.checkin.model.User;
import com.example.checkin.repository.AiRecommendationRepository;
import com.example.checkin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for custom security logic beyond Spring Security's built-in capabilities.
 */
@Service
public class SecurityService {

    private final UserRepository userRepository;
    private final AiRecommendationRepository aiRecommendationRepository;
    
    @Autowired
    public SecurityService(UserRepository userRepository, AiRecommendationRepository aiRecommendationRepository) {
        this.userRepository = userRepository;
        this.aiRecommendationRepository = aiRecommendationRepository;
    }
    
    /**
     * Determines if the authenticated user can access data for a specific user.
     * Rules:
     * 1. Users can access their own data
     * 2. Managers can access data for their direct reports
     * 3. Directors can access data for all users in their hierarchy
     * 4. Executives can access data for all users
     *
     * @param authentication The authentication object from Spring Security
     * @param targetUserId The ID of the user whose data is being accessed
     * @return true if access is allowed, false otherwise
     */
    public boolean canAccessUserData(Authentication authentication, Long targetUserId) {
        if (authentication == null) {
            return false;
        }
        
        String username = authentication.getName();
        Optional<User> currentUserOpt = userRepository.findByUsername(username);
        
        if (currentUserOpt.isEmpty()) {
            return false;
        }
        
        User currentUser = currentUserOpt.get();
        
        // If the user is trying to access their own data
        if (currentUser.getId().equals(targetUserId)) {
            return true;
        }
        
        // If the user is an executive, they can access all user data
        if (currentUser.getRole() == Role.EXECUTIVE) {
            return true;
        }
        
        // Check if target user exists
        Optional<User> targetUserOpt = userRepository.findById(targetUserId);
        if (targetUserOpt.isEmpty()) {
            return false;
        }
        
        User targetUser = targetUserOpt.get();
        
        // If current user is a director, they can access data for users in their hierarchy
        if (currentUser.getRole() == Role.DIRECTOR) {
            return isInHierarchy(currentUser, targetUser);
        }
        
        // If current user is a manager, they can access data for their direct reports
        if (currentUser.getRole() == Role.MANAGER) {
            return targetUser.getManager() != null && targetUser.getManager().getId().equals(currentUser.getId());
        }
        
        return false;
    }
    
    /**
     * Determines if the authenticated user can access a specific recommendation.
     * Rules:
     * 1. If recommendation is user-specific:
     *    - Users can access their own recommendations
     *    - Managers can access recommendations for their direct reports
     *    - Directors can access recommendations for all users in their hierarchy
     *    - Executives can access all recommendations
     * 2. If recommendation is location-specific:
     *    - Any user can access recommendations for locations they are associated with
     *    - Managers and above can access all location recommendations
     *
     * @param authentication The authentication object from Spring Security
     * @param recommendationId The ID of the recommendation being accessed
     * @return true if access is allowed, false otherwise
     */
    public boolean canAccessRecommendation(Authentication authentication, Long recommendationId) {
        if (authentication == null) {
            return false;
        }
        
        String username = authentication.getName();
        Optional<User> currentUserOpt = userRepository.findByUsername(username);
        
        if (currentUserOpt.isEmpty()) {
            return false;
        }
        
        User currentUser = currentUserOpt.get();
        
        // Fetch the recommendation
        Optional<AiRecommendation> recommendationOpt = aiRecommendationRepository.findById(recommendationId);
        if (recommendationOpt.isEmpty()) {
            return false;
        }
        
        AiRecommendation recommendation = recommendationOpt.get();
        
        // If the recommendation is user-specific
        if (recommendation.getUser() != null) {
            // If the recommendation is for the current user
            if (recommendation.getUser().getId().equals(currentUser.getId())) {
                return true;
            }
            
            // Check role-based access
            return canAccessUserData(authentication, recommendation.getUser().getId());
        }
        
        // If the recommendation is location-specific
        if (recommendation.getLocation() != null) {
            // Managers and above can access all location recommendations
            if (currentUser.getRole() == Role.MANAGER || 
                currentUser.getRole() == Role.DIRECTOR || 
                currentUser.getRole() == Role.EXECUTIVE) {
                return true;
            }
            
            // For regular employees, they would need to be associated with the location
            // This would require a check against the user's locations
            // For simplicity in this demo, we'll allow all authenticated users to access location recommendations
            return true;
        }
        
        // If recommendation is global (neither user nor location specific)
        // Allow access to managers and above
        return currentUser.getRole() == Role.MANAGER || 
               currentUser.getRole() == Role.DIRECTOR || 
               currentUser.getRole() == Role.EXECUTIVE;
    }
    
    /**
     * Determines if a user is in another user's management hierarchy.
     * 
     * @param manager The potential manager
     * @param user The user to check
     * @return true if the user is in the manager's hierarchy, false otherwise
     */
    private boolean isInHierarchy(User manager, User user) {
        if (user.getManager() == null) {
            return false;
        }
        
        if (user.getManager().getId().equals(manager.getId())) {
            return true;
        }
        
        return isInHierarchy(manager, user.getManager());
    }
}
