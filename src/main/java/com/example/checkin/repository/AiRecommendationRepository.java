package com.example.checkin.repository;

import com.example.checkin.model.AiRecommendation;
import com.example.checkin.model.Location;
import com.example.checkin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for AI recommendation data access.
 */
@Repository
public interface AiRecommendationRepository extends JpaRepository<AiRecommendation, Long> {

    /**
     * Find all recommendations for a specific user.
     * 
     * @param user The user to find recommendations for
     * @return List of recommendations for the user
     */
    List<AiRecommendation> findByUser(User user);
    
    /**
     * Find all recommendations for a specific location.
     * 
     * @param location The location to find recommendations for
     * @return List of recommendations for the location
     */
    List<AiRecommendation> findByLocation(Location location);
    
    /**
     * Find all recommendations of a specific type.
     * 
     * @param type The recommendation type to filter by
     * @return List of recommendations of the specified type
     */
    List<AiRecommendation> findByType(AiRecommendation.RecommendationType type);
    
    /**
     * Find all active (non-expired) recommendations for a user.
     * 
     * @param user The user to find recommendations for
     * @param now The current time
     * @return List of active recommendations for the user
     */
    List<AiRecommendation> findByUserAndExpiresAtAfterOrExpiresAtIsNull(User user, LocalDateTime now);
    
    /**
     * Find all unviewed recommendations for a user.
     * 
     * @param user The user to find recommendations for
     * @param viewed The viewed status to filter by (typically false)
     * @return List of unviewed recommendations for the user
     */
    List<AiRecommendation> findByUserAndViewed(User user, boolean viewed);
    
    /**
     * Find all high-confidence recommendations.
     * 
     * @param confidenceThreshold The minimum confidence level
     * @return List of recommendations above the confidence threshold
     */
    @Query("SELECT r FROM AiRecommendation r WHERE r.confidence >= :confidenceThreshold")
    List<AiRecommendation> findHighConfidenceRecommendations(double confidenceThreshold);
    
    /**
     * Find all recent recommendations created within a specific time period.
     * 
     * @param startTime The start of the time period
     * @param endTime The end of the time period
     * @return List of recommendations created within the time period
     */
    List<AiRecommendation> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
}
