package com.example.checkin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity to store AI-generated recommendations for persistence and reference.
 */
@Data
@Entity
@Table(name = "ai_recommendations")
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Type of recommendation (e.g., MEETING, SPACE_OPTIMIZATION, ANOMALY_DETECTION)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendationType type;
    
    /**
     * The user this recommendation is for (if applicable)
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    /**
     * The location this recommendation is for (if applicable)
     */
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    
    /**
     * JSON string storing the detailed recommendation data
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String recommendationData;
    
    /**
     * Title or summary of the recommendation
     */
    @Column(length = 255, nullable = false)
    private String title;
    
    /**
     * Brief description of the recommendation
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * Confidence level of the recommendation (0.0 to 1.0)
     */
    private Double confidence;
    
    /**
     * Whether this recommendation has been viewed by the target user/admin
     */
    private boolean viewed = false;
    
    /**
     * Whether this recommendation has been implemented or acted upon
     */
    private boolean implemented = false;
    
    /**
     * Timestamp when this recommendation was generated
     */
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    /**
     * Expiration date of this recommendation (when it becomes outdated)
     */
    private LocalDateTime expiresAt;
    
    /**
     * Types of AI recommendations supported by the system
     */
    public enum RecommendationType {
        MEETING,
        SPACE_OPTIMIZATION,
        ANOMALY_DETECTION,
        PERSONAL_INSIGHT,
        TEAM_COLLABORATION,
        RESOURCE_ALLOCATION,
        SECURITY_ALERT
    }
}
