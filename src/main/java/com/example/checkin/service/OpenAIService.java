package com.example.checkin.service;

import com.example.checkin.model.AiRecommendation;
import com.example.checkin.model.CheckIn;
import com.example.checkin.model.Location;
import com.example.checkin.model.User;
import com.example.checkin.repository.AiRecommendationRepository;
import com.example.checkin.repository.CheckInRepository;
import com.example.checkin.repository.LocationRepository;
import com.example.checkin.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for AI-powered recommendations and insights.
 * In a production environment, this would connect to OpenAI's API.
 * Currently implemented with mock responses for demonstration purposes.
 */
@Service
public class OpenAIService {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final AiRecommendationRepository aiRecommendationRepository;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public OpenAIService(
            CheckInRepository checkInRepository,
            UserRepository userRepository,
            LocationRepository locationRepository,
            AiRecommendationRepository aiRecommendationRepository,
            ObjectMapper objectMapper) {
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.aiRecommendationRepository = aiRecommendationRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Generate meeting time recommendations based on check-in patterns.
     * @param userId The user ID for whom to generate recommendations
     * @return Map containing recommendations and additional context
     */
    public Map<String, Object> generateMeetingRecommendations(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<User> teamMembers = userRepository.findByManager(user);
        
        // In a production app, this would call OpenAI API with check-in patterns
        // For demo purposes, we're returning mock recommendations
        
        Map<String, Object> response = new HashMap<>();
        response.put("recommendedDay", "Wednesday");
        response.put("recommendedTimeSlot", "10:00 AM - 12:00 PM");
        response.put("recommendedLocation", "Conference Room A - Headquarters");
        response.put("reasoning", "Based on historical check-in patterns, 85% of your team is typically in the office on Wednesday mornings. Conference Room A has the capacity for your entire team and has the necessary AV equipment.");
        response.put("teamAvailability", "8 out of 10 team members are typically present during this time slot.");
        
        // Store the recommendation for persistence
        storeRecommendation(
            AiRecommendation.RecommendationType.MEETING,
            user, 
            null,
            response,
            "Optimal Team Meeting Time",
            "AI-recommended optimal time for team meetings based on check-in patterns",
            0.85,
            LocalDateTime.now().plusWeeks(2)
        );
        
        return response;
    }
    
    /**
     * Detect unusual check-in patterns that might indicate security concerns.
     * @return List of potential anomalies with context
     */
    public List<Map<String, Object>> detectAnomalies() {
        // In production, this would use OpenAI to analyze complex patterns
        // For demonstration, return mock anomalies
        
        Map<String, Object> anomaly1 = new HashMap<>();
        anomaly1.put("userId", 4L);
        anomaly1.put("username", "user1");
        anomaly1.put("anomalyType", "UNUSUAL_HOURS");
        anomaly1.put("description", "User checked in at 2:37 AM, outside their normal pattern of 8AM-6PM");
        anomaly1.put("checkInId", 12L);
        anomaly1.put("severityScore", 0.85);
        anomaly1.put("suggestedAction", "Review security footage and verify identity");
        
        User user1 = userRepository.findById(4L).orElse(null);
        if (user1 != null) {
            storeRecommendation(
                AiRecommendation.RecommendationType.SECURITY_ALERT,
                user1, 
                null,
                anomaly1,
                "Unusual Hours Check-In Alert",
                "User checked in at 2:37 AM, outside their normal pattern of 8AM-6PM",
                0.85,
                LocalDateTime.now().plusDays(2)
            );
        }
        
        Map<String, Object> anomaly2 = new HashMap<>();
        anomaly2.put("userId", 6L);
        anomaly2.put("username", "manager1");
        anomaly2.put("anomalyType", "UNUSUAL_LOCATION");
        anomaly2.put("description", "User checked in at Branch Office 3, but normally only visits Headquarters and Branch Office 1");
        anomaly2.put("checkInId", 27L);
        anomaly2.put("severityScore", 0.62);
        anomaly2.put("suggestedAction", "Verify with manager about travel plans");
        
        User user2 = userRepository.findById(6L).orElse(null);
        if (user2 != null) {
            storeRecommendation(
                AiRecommendation.RecommendationType.SECURITY_ALERT,
                user2, 
                null,
                anomaly2,
                "Unusual Location Check-In Alert",
                "User checked in at Branch Office 3, but normally only visits Headquarters and Branch Office 1",
                0.62,
                LocalDateTime.now().plusDays(2)
            );
        }
        
        return List.of(anomaly1, anomaly2);
    }
    
    /**
     * Generate space optimization recommendations based on office usage patterns.
     * @param locationId The location ID to analyze
     * @return Map containing recommendations for space optimization
     */
    public Map<String, Object> generateSpaceOptimizationRecommendations(Long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow();
        
        // This would use OpenAI to analyze check-in patterns and generate recommendations
        // For demo purposes, returning mock recommendations
        
        Map<String, Object> response = new HashMap<>();
        response.put("locationName", location.getName());
        response.put("averageDailyOccupancy", "63%");
        response.put("peakOccupancyDay", "Tuesday");
        response.put("peakOccupancyTime", "10:30 AM - 2:30 PM");
        response.put("spaceUtilizationScore", 0.72);
        
        Map<String, Object> recommendations = new HashMap<>();
        recommendations.put("deskArrangement", "Consider implementing hot-desking for 30% of workstations based on attendance patterns");
        recommendations.put("meetingRooms", "Small meeting rooms are underutilized. Consider converting 2 small rooms into 1 medium-sized collaborative space");
        recommendations.put("timing", "Encourage flexible working hours to distribute peak times");
        
        response.put("recommendations", recommendations);
        
        storeRecommendation(
            AiRecommendation.RecommendationType.SPACE_OPTIMIZATION,
            null, 
            location,
            response,
            "Space Optimization for " + location.getName(),
            "Recommendations for optimizing space usage at " + location.getName() + " based on check-in patterns",
            0.72,
            LocalDateTime.now().plusMonths(1)
        );
        
        return response;
    }
    
    /**
     * Generate personalized workplace insights for a specific user.
     * @param userId The user ID to analyze
     * @return Map containing personalized insights
     */
    public Map<String, Object> generatePersonalizedInsights(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // This would use OpenAI to analyze personal check-in patterns and meetings
        // For demo purposes, returning mock insights
        
        Map<String, Object> response = new HashMap<>();
        response.put("userName", user.getFirstName() + " " + user.getLastName());
        
        List<Map<String, String>> insights = List.of(
            Map.of("insight", "Productivity Pattern", 
                  "description", "You appear to be most productive on Tuesday and Wednesday mornings based on your meeting schedules and task completions."),
            Map.of("insight", "Collaboration",
                  "description", "You collaborate most frequently with the Marketing team (42% of your meetings)."),
            Map.of("insight", "Work-Life Balance",
                  "description", "You've been checking in earlier and staying later than usual this month compared to your historical patterns.")
        );
        
        response.put("insights", insights);
        
        List<Map<String, String>> suggestions = List.of(
            Map.of("suggestion", "Schedule focus time",
                  "description", "Block 10AM-12PM on Tuesdays and Wednesdays for deep work to leverage your peak productivity hours."),
            Map.of("suggestion", "Consider regular check-ins",
                  "description", "Schedule a standing weekly sync with Marketing to streamline your collaboration."),
            Map.of("suggestion", "Work-life boundaries",
                  "description", "Consider setting a reminder to leave the office by 5:30 PM at least 3 days per week.")
        );
        
        response.put("suggestions", suggestions);
        
        storeRecommendation(
            AiRecommendation.RecommendationType.PERSONAL_INSIGHT,
            user, 
            null,
            response,
            "Personal Workplace Insights",
            "AI-generated insights about your work patterns and productivity",
            0.78,
            LocalDateTime.now().plusWeeks(2)
        );
        
        return response;
    }
    
    /**
     * Store an AI recommendation in the database for persistence.
     */
    private void storeRecommendation(
            AiRecommendation.RecommendationType type,
            User user,
            Location location,
            Object data,
            String title,
            String description,
            double confidence,
            LocalDateTime expiresAt) {
        
        try {
            String jsonData = objectMapper.writeValueAsString(data);
            
            AiRecommendation recommendation = new AiRecommendation();
            recommendation.setType(type);
            recommendation.setUser(user);
            recommendation.setLocation(location);
            recommendation.setRecommendationData(jsonData);
            recommendation.setTitle(title);
            recommendation.setDescription(description);
            recommendation.setConfidence(confidence);
            recommendation.setExpiresAt(expiresAt);
            
            aiRecommendationRepository.save(recommendation);
        } catch (JsonProcessingException e) {
            // In a production app, this would be properly logged
            e.printStackTrace();
        }
    }
    
    /**
     * Get all stored recommendations for a specific user.
     * 
     * @param userId The user ID to get recommendations for
     * @return List of recommendation objects
     */
    public List<AiRecommendation> getUserRecommendations(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return aiRecommendationRepository.findByUser(user);
    }
    
    /**
     * Get all stored recommendations for a specific location.
     * 
     * @param locationId The location ID to get recommendations for
     * @return List of recommendation objects
     */
    public List<AiRecommendation> getLocationRecommendations(Long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow();
        return aiRecommendationRepository.findByLocation(location);
    }
    
    /**
     * Mark a recommendation as viewed.
     * 
     * @param recommendationId The recommendation ID to mark as viewed
     */
    public void markRecommendationAsViewed(Long recommendationId) {
        AiRecommendation recommendation = aiRecommendationRepository.findById(recommendationId).orElseThrow();
        recommendation.setViewed(true);
        aiRecommendationRepository.save(recommendation);
    }
    
    /**
     * Mark a recommendation as implemented.
     * 
     * @param recommendationId The recommendation ID to mark as implemented
     */
    public void markRecommendationAsImplemented(Long recommendationId) {
        AiRecommendation recommendation = aiRecommendationRepository.findById(recommendationId).orElseThrow();
        recommendation.setImplemented(true);
        aiRecommendationRepository.save(recommendation);
    }

    /**
     * Predict when a user is likely to check in next based on historical patterns.
     * This uses AI to analyze past user behavior and calendar data to predict their next visit.
     * 
     * @param userId The user ID to generate predictions for
     * @return Map containing visit prediction details
     */
    public Map<String, Object> predictUserVisit(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // Fetch user's historical check-in data
        List<CheckIn> userCheckIns = checkInRepository.findByUserId(userId);
        
        // In a production environment, this would:
        // 1. Process historical check-in patterns (day of week, time, duration)
        // 2. Consider calendar events the user has scheduled
        // 3. Look at team/peer attendance patterns 
        // 4. Consider location preferences and travel patterns
        // 5. Call a machine learning model API (OpenAI API or custom ML model)
        
        // For demonstration purposes, we'll return a mock prediction
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("userName", user.getFirstName() + " " + user.getLastName());
        prediction.put("mostLikelyNextVisit", LocalDateTime.now().plusDays(3).withHour(9).withMinute(30).toString());
        prediction.put("confidence", 0.87);
        
        // Predict most likely visit day and time
        prediction.put("mostLikelyDayOfWeek", "Wednesday");
        prediction.put("mostLikelyTimeRange", "9:00 AM - 11:30 AM");
        
        // Predict most likely location
        Location preferredLocation = user.getLocation() != null ? user.getLocation() : 
                                    locationRepository.findById(1L).orElse(null);
        prediction.put("mostLikelyLocation", preferredLocation != null ? preferredLocation.getName() : "Headquarters");
        
        // Additional context about the prediction
        Map<String, Object> patterns = new HashMap<>();
        patterns.put("regularAttendanceDays", List.of("Monday", "Wednesday", "Thursday"));
        patterns.put("averageVisitDuration", "6.2 hours");
        patterns.put("averageArrivalTime", "9:15 AM");
        patterns.put("visitFrequency", "3.2 days per week");
        prediction.put("observedPatterns", patterns);
        
        // Store the prediction as a recommendation
        storeRecommendation(
            AiRecommendation.RecommendationType.PERSONAL_INSIGHT,
            user,
            null,
            prediction,
            "Visit Prediction for " + user.getFirstName() + " " + user.getLastName(),
            "AI-predicted next visit time based on historical check-in patterns",
            0.87,
            LocalDateTime.now().plusDays(7)
        );
        
        return prediction;
    }

    /**
     * Generate suggested planned visits for a user based on their historical patterns.
     * This helps users pre-schedule their office visits based on AI-identified patterns.
     * 
     * @param userId The user ID to generate suggestions for
     * @return List of suggested visit times with context
     */
    public List<Map<String, Object>> suggestPlannedVisits(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // In a production app, this would analyze:
        // 1. Historical visit patterns (frequency, duration, preferred days)
        // 2. Team member schedules and meeting patterns
        // 3. Location capacity and optimal distribution
        
        // For demo purposes, generate mock suggestions
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        // Suggestion 1: Next week's regular pattern
        Map<String, Object> suggestion1 = new HashMap<>();
        suggestion1.put("startTime", LocalDateTime.now().plusDays(5).withHour(9).withMinute(0).toString());
        suggestion1.put("endTime", LocalDateTime.now().plusDays(5).withHour(17).withMinute(0).toString());
        suggestion1.put("location", user.getLocation() != null ? user.getLocation().getName() : "Headquarters");
        suggestion1.put("confidence", 0.91);
        suggestion1.put("reason", "Matches your regular attendance pattern on Mondays");
        suggestion1.put("teamContext", "6 team members typically work from this location on this day");
        suggestions.add(suggestion1);
        
        // Suggestion 2: Team collaboration opportunity
        Map<String, Object> suggestion2 = new HashMap<>();
        suggestion2.put("startTime", LocalDateTime.now().plusDays(8).withHour(13).withMinute(0).toString());
        suggestion2.put("endTime", LocalDateTime.now().plusDays(8).withHour(17).withMinute(0).toString());
        suggestion2.put("location", user.getLocation() != null ? user.getLocation().getName() : "Headquarters");
        suggestion2.put("confidence", 0.78);
        suggestion2.put("reason", "Three of your direct collaborators will be present");
        suggestion2.put("teamContext", "Opportunity to coordinate on current project milestones");
        suggestions.add(suggestion2);
        
        // Suggestion 3: Important meeting day
        Map<String, Object> suggestion3 = new HashMap<>();
        suggestion3.put("startTime", LocalDateTime.now().plusDays(10).withHour(9).withMinute(0).toString());
        suggestion3.put("endTime", LocalDateTime.now().plusDays(10).withHour(15).withMinute(0).toString());
        suggestion3.put("location", user.getLocation() != null ? user.getLocation().getName() : "Headquarters");
        suggestion3.put("confidence", 0.85);
        suggestion3.put("reason", "Monthly department sync scheduled");
        suggestion3.put("teamContext", "Most team leaders will be present this day");
        suggestions.add(suggestion3);
        
        // Store as a recommendation for persistence
        Map<String, Object> recommendationData = new HashMap<>();
        recommendationData.put("suggestedVisits", suggestions);
        recommendationData.put("generatedAt", LocalDateTime.now().toString());
        
        storeRecommendation(
            AiRecommendation.RecommendationType.PERSONAL_INSIGHT,
            user,
            null,
            recommendationData,
            "Suggested Office Visit Schedule",
            "AI-generated suggestions for optimal office visits over the next two weeks",
            0.85,
            LocalDateTime.now().plusWeeks(2)
        );
        
        return suggestions;
    }

    /**
     * Generate team distribution recommendations across locations.
     * This AI-powered feature recommends optimal team distributions based on 
     * collaboration patterns and space utilization.
     * 
     * @param managerId The manager ID requesting recommendations
     * @return Map containing team distribution recommendations
     */
    public Map<String, Object> generateTeamDistributionRecommendations(Long managerId) {
        User manager = userRepository.findById(managerId).orElseThrow();
        List<User> teamMembers = userRepository.findByManager(manager);
        
        // In a production system, this would:
        // 1. Analyze historical check-in patterns for all team members
        // 2. Identify collaboration clusters based on who works together
        // 3. Consider space utilization across available locations
        // 4. Use an ML model to optimize for collaboration while balancing location capacities
        
        // For demo purposes, return mock recommendations
        Map<String, Object> response = new HashMap<>();
        response.put("managerName", manager.getFirstName() + " " + manager.getLastName());
        response.put("teamSize", teamMembers.size());
        
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        // Recommendation 1: Headquarters allocation
        Map<String, Object> rec1 = new HashMap<>();
        rec1.put("locationName", "Headquarters");
        rec1.put("recommendedHeadcount", Math.round(teamMembers.size() * 0.6));
        rec1.put("recommendedDays", List.of("Monday", "Wednesday"));
        rec1.put("reasoningModel", "Core project team needs consistent collaboration space");
        rec1.put("suggestedMembers", List.of("Alice Smith", "Bob Johnson", "Carol Williams"));
        recommendations.add(rec1);
        
        // Recommendation 2: Satellite office allocation
        Map<String, Object> rec2 = new HashMap<>();
        rec2.put("locationName", "Branch Office 1");
        rec2.put("recommendedHeadcount", Math.round(teamMembers.size() * 0.3));
        rec2.put("recommendedDays", List.of("Tuesday", "Thursday"));
        rec2.put("reasoningModel", "UX and design sub-team collaborates most effectively in person");
        rec2.put("suggestedMembers", List.of("David Brown", "Eve Davis"));
        recommendations.add(rec2);
        
        // Recommendation 3: Remote work allocation
        Map<String, Object> rec3 = new HashMap<>();
        rec3.put("locationName", "Remote");
        rec3.put("recommendedHeadcount", Math.round(teamMembers.size() * 0.1));
        rec3.put("recommendedDays", List.of("Friday"));
        rec3.put("reasoningModel", "Development sub-team performs best with focus time on Fridays");
        rec3.put("suggestedMembers", List.of("Frank Miller"));
        recommendations.add(rec3);
        
        response.put("distributionRecommendations", recommendations);
        
        // Calculate expected collaboration improvement
        response.put("predictedCollaborationImprovement", "23%");
        response.put("predictedProductivityImprovement", "12%");
        response.put("predictedCommutingReduction", "9%");
        
        // Store the recommendation for persistence
        storeRecommendation(
            AiRecommendation.RecommendationType.TEAM_COLLABORATION,
            manager,
            null,
            response,
            "Optimal Team Distribution Strategy",
            "AI-generated recommendations for distributing your team across locations",
            0.82,
            LocalDateTime.now().plusWeeks(4)
        );
        
        return response;
    }
}
