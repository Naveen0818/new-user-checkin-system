package com.example.checkin.util;

import com.example.checkin.model.CheckIn;
import com.example.checkin.model.Location;
import com.example.checkin.model.User;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for building well-structured prompts for OpenAI.
 */
public class OpenAIPromptBuilder {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Builds a prompt for meeting time recommendations.
     * 
     * @param manager The manager requesting the recommendation
     * @param teamMembers List of team members
     * @param checkIns List of recent check-ins for analysis
     * @return Formatted prompt for OpenAI
     */
    public static String buildMeetingRecommendationPrompt(User manager, List<User> teamMembers, List<CheckIn> checkIns) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("You are an AI assistant helping to find the optimal meeting time based on office check-in patterns.\n\n");
        
        // Add manager info
        sb.append("## Manager Information\n");
        sb.append("Manager: ").append(manager.getFirstName()).append(" ").append(manager.getLastName()).append("\n");
        sb.append("Email: ").append(manager.getEmail()).append("\n");
        sb.append("Role: ").append(manager.getRole()).append("\n\n");
        
        // Add team members information
        sb.append("## Team Members\n");
        for (User member : teamMembers) {
            sb.append("- ").append(member.getFirstName()).append(" ").append(member.getLastName())
              .append(" (").append(member.getEmail()).append(")\n");
        }
        sb.append("\n");
        
        // Add check-in patterns for analysis
        sb.append("## Recent Check-in Patterns\n");
        for (CheckIn checkIn : checkIns) {
            sb.append("- User: ").append(checkIn.getUser().getFirstName()).append(" ").append(checkIn.getUser().getLastName())
              .append(", Location: ").append(checkIn.getLocation().getName())
              .append(", Time: ").append(checkIn.getCheckInTime().format(DATE_TIME_FORMATTER))
              .append(", Check-out: ").append(checkIn.getCheckOutTime() != null ? 
                     checkIn.getCheckOutTime().format(DATE_TIME_FORMATTER) : "Still checked in")
              .append("\n");
        }
        sb.append("\n");
        
        // Add the request
        sb.append("## Request\n");
        sb.append("Based on the check-in patterns for these team members, please recommend:\n");
        sb.append("1. The best day of the week for a team meeting\n");
        sb.append("2. The best time slot during that day\n");
        sb.append("3. A suitable location for the meeting\n");
        sb.append("4. A brief explanation of why this time and location are optimal\n");
        sb.append("5. Information about team availability during the recommended time\n");
        sb.append("\n");
        sb.append("Please format your response as a JSON object with the following keys: recommendedDay, recommendedTimeSlot, recommendedLocation, reasoning, teamAvailability");
        
        return sb.toString();
    }
    
    /**
     * Builds a prompt for anomaly detection in check-in patterns.
     * 
     * @param checkIns List of all check-ins for analysis
     * @param users List of all users
     * @return Formatted prompt for OpenAI
     */
    public static String buildAnomalyDetectionPrompt(List<CheckIn> checkIns, List<User> users) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("You are a security AI assistant analyzing check-in patterns to detect potential security anomalies.\n\n");
        
        // Add users information
        sb.append("## Users\n");
        for (User user : users) {
            sb.append("- ID: ").append(user.getId())
              .append(", Name: ").append(user.getFirstName()).append(" ").append(user.getLastName())
              .append(", Username: ").append(user.getUsername())
              .append(", Role: ").append(user.getRole())
              .append("\n");
        }
        sb.append("\n");
        
        // Add check-in data for analysis
        sb.append("## Check-in Data\n");
        for (CheckIn checkIn : checkIns) {
            sb.append("- ID: ").append(checkIn.getId())
              .append(", User: ").append(checkIn.getUser().getFirstName()).append(" ").append(checkIn.getUser().getLastName())
              .append(" (ID: ").append(checkIn.getUser().getId()).append(")")
              .append(", Location: ").append(checkIn.getLocation().getName())
              .append(", Time: ").append(checkIn.getCheckInTime().format(DATE_TIME_FORMATTER))
              .append(", Check-out: ").append(checkIn.getCheckOutTime() != null ? 
                     checkIn.getCheckOutTime().format(DATE_TIME_FORMATTER) : "Still checked in")
              .append("\n");
        }
        sb.append("\n");
        
        // Add the request
        sb.append("## Request\n");
        sb.append("Analyze the check-in patterns and identify potential anomalies that could indicate security concerns. Consider factors such as:\n");
        sb.append("1. Unusual check-in times (very early morning or late night)\n");
        sb.append("2. Users checking in at locations they don't typically visit\n");
        sb.append("3. Unusual patterns in check-in duration\n");
        sb.append("4. Multiple check-ins from different locations in a short time period\n");
        sb.append("\n");
        sb.append("Please format your response as a JSON array of objects. Each object should have these keys: userId, username, anomalyType, description, checkInId, severityScore (0.0-1.0), suggestedAction");
        
        return sb.toString();
    }
    
    /**
     * Builds a prompt for space optimization recommendations.
     * 
     * @param location The location to analyze
     * @param checkIns List of check-ins at this location
     * @return Formatted prompt for OpenAI
     */
    public static String buildSpaceOptimizationPrompt(Location location, List<CheckIn> checkIns) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("You are a workplace optimization AI assistant analyzing office usage patterns to recommend space optimizations.\n\n");
        
        // Add location info
        sb.append("## Location Information\n");
        sb.append("Location Name: ").append(location.getName()).append("\n");
        sb.append("Address: ").append(location.getAddress()).append("\n");
        sb.append("Capacity: ").append(location.getCapacity() != null ? location.getCapacity() : "N/A").append(location.getCapacity() != null ? " people" : "").append("\n");
        sb.append("Type: ").append(location.getType() != null ? location.getType() : "N/A").append("\n\n");
        
        // Add check-in data for analysis
        sb.append("## Check-in Data\n");
        for (CheckIn checkIn : checkIns) {
            sb.append("- User: ").append(checkIn.getUser().getFirstName()).append(" ").append(checkIn.getUser().getLastName())
              .append(", Time: ").append(checkIn.getCheckInTime().format(DATE_TIME_FORMATTER))
              .append(", Check-out: ").append(checkIn.getCheckOutTime() != null ? 
                     checkIn.getCheckOutTime().format(DATE_TIME_FORMATTER) : "Still checked in")
              .append("\n");
        }
        sb.append("\n");
        
        // Add the request
        sb.append("## Request\n");
        sb.append("Analyze the check-in patterns for this location and provide recommendations for space optimization. Include:\n");
        sb.append("1. Average daily occupancy percentage\n");
        sb.append("2. Peak occupancy day and time\n");
        sb.append("3. Space utilization score (0.0-1.0)\n");
        sb.append("4. Specific recommendations for:\n");
        sb.append("   - Desk arrangement\n");
        sb.append("   - Meeting room usage\n");
        sb.append("   - Timing adjustments to distribute peak times\n");
        sb.append("\n");
        sb.append("Please format your response as a JSON object with the following structure:\n");
        sb.append("{\n");
        sb.append("  \"locationName\": \"name\",\n");
        sb.append("  \"averageDailyOccupancy\": \"percentage\",\n");
        sb.append("  \"peakOccupancyDay\": \"day\",\n");
        sb.append("  \"peakOccupancyTime\": \"time range\",\n");
        sb.append("  \"spaceUtilizationScore\": number,\n");
        sb.append("  \"recommendations\": {\n");
        sb.append("    \"deskArrangement\": \"recommendation\",\n");
        sb.append("    \"meetingRooms\": \"recommendation\",\n");
        sb.append("    \"timing\": \"recommendation\"\n");
        sb.append("  }\n");
        sb.append("}");
        
        return sb.toString();
    }
    
    /**
     * Builds a prompt for personalized workplace insights.
     * 
     * @param user The user to generate insights for
     * @param checkIns List of the user's check-ins
     * @return Formatted prompt for OpenAI
     */
    public static String buildPersonalizedInsightsPrompt(User user, List<CheckIn> checkIns) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("You are a workplace insights AI assistant providing personalized insights based on check-in patterns.\n\n");
        
        // Add user info
        sb.append("## User Information\n");
        sb.append("Name: ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
        sb.append("Email: ").append(user.getEmail()).append("\n");
        sb.append("Role: ").append(user.getRole()).append("\n\n");
        
        // Add check-in data for analysis
        sb.append("## Check-in History\n");
        for (CheckIn checkIn : checkIns) {
            sb.append("- Date: ").append(checkIn.getCheckInTime().format(DATE_TIME_FORMATTER))
              .append(", Location: ").append(checkIn.getLocation().getName())
              .append(", Duration: ").append(checkIn.getCheckOutTime() != null ? 
                     calculateDuration(checkIn) : "Still checked in")
              .append("\n");
        }
        sb.append("\n");
        
        // Add the request
        sb.append("## Request\n");
        sb.append("Based on this user's check-in patterns, please provide personalized workplace insights. Include:\n");
        sb.append("1. Productivity patterns (days/times when they appear most productive)\n");
        sb.append("2. Collaboration patterns (if data suggests frequent interactions with specific teams)\n");
        sb.append("3. Work-life balance observations\n");
        sb.append("4. Personalized suggestions for optimizing their workplace experience\n");
        sb.append("\n");
        sb.append("Please format your response as a JSON object with the following structure:\n");
        sb.append("{\n");
        sb.append("  \"userName\": \"name\",\n");
        sb.append("  \"insights\": [\n");
        sb.append("    { \"insight\": \"Productivity Pattern\", \"description\": \"description\" },\n");
        sb.append("    { \"insight\": \"Collaboration\", \"description\": \"description\" },\n");
        sb.append("    { \"insight\": \"Work-Life Balance\", \"description\": \"description\" }\n");
        sb.append("  ],\n");
        sb.append("  \"suggestions\": [\n");
        sb.append("    { \"suggestion\": \"title\", \"description\": \"description\" },\n");
        sb.append("    { \"suggestion\": \"title\", \"description\": \"description\" },\n");
        sb.append("    { \"suggestion\": \"title\", \"description\": \"description\" }\n");
        sb.append("  ]\n");
        sb.append("}");
        
        return sb.toString();
    }
    
    /**
     * Calculate duration between check-in and check-out.
     * Helper function for building prompts.
     */
    private static String calculateDuration(CheckIn checkIn) {
        if (checkIn.getCheckOutTime() == null) {
            return "N/A";
        }
        
        long minutes = java.time.Duration.between(checkIn.getCheckInTime(), checkIn.getCheckOutTime()).toMinutes();
        
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            return hours + " hours " + (remainingMinutes > 0 ? remainingMinutes + " minutes" : "");
        }
    }
}
