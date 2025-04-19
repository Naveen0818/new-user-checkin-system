package com.example.checkin.controller;

import com.example.checkin.model.AiRecommendation;
import com.example.checkin.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for AI-powered insights and recommendations.
 * Provides endpoints for intelligent features throughout the check-in system.
 */
@RestController
@RequestMapping("/ai")
public class AIRecommendationController {

    private final OpenAIService openAIService;
    
    @Autowired
    public AIRecommendationController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }
    
    /**
     * Get AI-generated meeting time recommendations based on team check-in patterns.
     * 
     * @param userId The user ID (typically a manager) requesting recommendations
     * @return ResponseEntity containing meeting recommendations
     */
    @GetMapping("/meeting-recommendations")
    public ResponseEntity<Map<String, Object>> getMeetingRecommendations(@RequestParam Long userId) {
        return ResponseEntity.ok(openAIService.generateMeetingRecommendations(userId));
    }
    
    /**
     * Get AI-detected anomalies in check-in patterns for security monitoring.
     * 
     * @return ResponseEntity containing detected anomalies
     */
    @GetMapping("/security/anomalies")
    public ResponseEntity<List<Map<String, Object>>> getAnomalyDetection() {
        return ResponseEntity.ok(openAIService.detectAnomalies());
    }
    
    /**
     * Get AI-generated space optimization recommendations for a specific location.
     * 
     * @param locationId The location ID to analyze
     * @return ResponseEntity containing space optimization recommendations
     */
    @GetMapping("/space-optimization/{locationId}")
    public ResponseEntity<Map<String, Object>> getSpaceOptimizationRecommendations(
            @PathVariable Long locationId) {
        return ResponseEntity.ok(openAIService.generateSpaceOptimizationRecommendations(locationId));
    }
    
    /**
     * Get personalized workplace insights for a specific user.
     * 
     * @param userId The user ID to get insights for
     * @return ResponseEntity containing personalized insights
     */
    @GetMapping("/personal-insights/{userId}")
    public ResponseEntity<Map<String, Object>> getPersonalizedInsights(@PathVariable Long userId) {
        return ResponseEntity.ok(openAIService.generatePersonalizedInsights(userId));
    }
    
    /**
     * Get all stored recommendations for a specific user.
     * 
     * @param userId The user ID to get recommendations for
     * @return ResponseEntity containing a list of recommendations
     */
    @GetMapping("/recommendations/user/{userId}")
    public ResponseEntity<List<AiRecommendation>> getUserRecommendations(@PathVariable Long userId) {
        return ResponseEntity.ok(openAIService.getUserRecommendations(userId));
    }
    
    /**
     * Get all stored recommendations for a specific location.
     * 
     * @param locationId The location ID to get recommendations for
     * @return ResponseEntity containing a list of recommendations
     */
    @GetMapping("/recommendations/location/{locationId}")
    public ResponseEntity<List<AiRecommendation>> getLocationRecommendations(@PathVariable Long locationId) {
        return ResponseEntity.ok(openAIService.getLocationRecommendations(locationId));
    }
    
    /**
     * Mark a recommendation as viewed.
     * 
     * @param recommendationId The recommendation ID to mark as viewed
     * @return ResponseEntity with success message
     */
    @PostMapping("/recommendations/{recommendationId}/viewed")
    public ResponseEntity<String> markRecommendationAsViewed(@PathVariable Long recommendationId) {
        openAIService.markRecommendationAsViewed(recommendationId);
        return ResponseEntity.ok("Recommendation marked as viewed");
    }
    
    /**
     * Mark a recommendation as implemented.
     * 
     * @param recommendationId The recommendation ID to mark as implemented
     * @return ResponseEntity with success message
     */
    @PostMapping("/recommendations/{recommendationId}/implemented")
    public ResponseEntity<String> markRecommendationAsImplemented(@PathVariable Long recommendationId) {
        openAIService.markRecommendationAsImplemented(recommendationId);
        return ResponseEntity.ok("Recommendation marked as implemented");
    }
    
    /**
     * Get AI-predicted visit time based on user's historical patterns.
     * 
     * @param userId The user ID to analyze
     * @return ResponseEntity containing visit prediction details
     */
    @GetMapping("/visit-prediction")
    public ResponseEntity<Map<String, Object>> getVisitPrediction(@RequestParam Long userId) {
        return ResponseEntity.ok(openAIService.predictUserVisit(userId));
    }
    
    /**
     * Get AI-suggested planned visits based on user patterns and team schedules.
     * 
     * @param userId The user ID to generate suggestions for
     * @return ResponseEntity containing list of suggested visit times
     */
    @GetMapping("/suggest-visits")
    public ResponseEntity<List<Map<String, Object>>> getSuggestedVisits(@RequestParam Long userId) {
        return ResponseEntity.ok(openAIService.suggestPlannedVisits(userId));
    }
    
    /**
     * Get AI-powered recommendations for optimal team distribution across locations.
     * 
     * @param managerId The manager ID requesting recommendations
     * @return ResponseEntity containing team distribution recommendations
     */
    @GetMapping("/team-distribution")
    public ResponseEntity<Map<String, Object>> getTeamDistributionRecommendations(@RequestParam Long managerId) {
        return ResponseEntity.ok(openAIService.generateTeamDistributionRecommendations(managerId));
    }
}
