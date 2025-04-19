package com.example.checkin.controller;

import com.example.checkin.model.PlannedVisit;
import com.example.checkin.model.User;
import com.example.checkin.model.VisitStatus;
import com.example.checkin.service.PlannedVisitService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/visits")
public class PlannedVisitController {

    private final PlannedVisitService plannedVisitService;

    public PlannedVisitController(PlannedVisitService plannedVisitService) {
        this.plannedVisitService = plannedVisitService;
    }

    @PostMapping("/location/{locationId}")
    public ResponseEntity<PlannedVisit> createPlannedVisit(
            @PathVariable Long locationId,
            @Valid @RequestBody PlannedVisit plannedVisit) {
        try {
            PlannedVisit createdVisit = plannedVisitService.createPlannedVisit(plannedVisit, null, locationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVisit);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{visitId}/status/{status}")
    public ResponseEntity<PlannedVisit> updateVisitStatus(
            @PathVariable Long visitId,
            @PathVariable VisitStatus status) {
        try {
            PlannedVisit updatedVisit = plannedVisitService.updatePlannedVisitStatus(visitId, status);
            return ResponseEntity.ok(updatedVisit);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> deletePlannedVisit(@PathVariable Long visitId) {
        try {
            plannedVisitService.deletePlannedVisit(visitId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<PlannedVisit>> getUserPlannedVisits() {
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByUser(null);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlannedVisit>> getUserPlannedVisitsById(@PathVariable Long userId) {
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByUser(userId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<PlannedVisit>> getLocationPlannedVisits(@PathVariable Long locationId) {
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByLocation(locationId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/user/status/{status}")
    public ResponseEntity<List<PlannedVisit>> getUserPlannedVisitsByStatus(
            @PathVariable VisitStatus status) {
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByUserAndStatus(null, status);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/user/upcoming")
    public ResponseEntity<List<PlannedVisit>> getUserUpcomingVisits() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoWeeksLater = now.plusWeeks(2);
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByUserAndDateRange(null, now, twoWeeksLater);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/manager/upcoming")
    public ResponseEntity<List<PlannedVisit>> getManagerUpcomingVisits() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoWeeksLater = now.plusWeeks(2);
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByManagerAndDateRange(null, now, twoWeeksLater);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/peers/upcoming")
    public ResponseEntity<List<PlannedVisit>> getPeersUpcomingVisits() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoWeeksLater = now.plusWeeks(2);
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByPeersAndDateRange(null, now, twoWeeksLater);
        return ResponseEntity.ok(visits);
    }

    /**
     * Create a planned visit from an AI suggestion.
     * This endpoint streamlines turning an AI suggestion into an actual visit.
     *
     * @param locationId The location ID for the visit
     * @param requestBody The request containing visit details from the suggestion
     * @return The created planned visit
     */
    @PostMapping("/from-suggestion/location/{locationId}")
    public ResponseEntity<PlannedVisit> createFromSuggestion(
            @PathVariable Long locationId,
            @RequestBody Map<String, Object> requestBody) {
        
        try {
            // Parse the datetime strings from the request
            String startTimeStr = (String) requestBody.get("startTime");
            String endTimeStr = (String) requestBody.get("endTime");
            String purpose = (String) requestBody.get("purpose");
            
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr);
            
            PlannedVisit createdVisit = plannedVisitService.createPlannedVisitFromSuggestion(
                    null, locationId, startTime, endTime, purpose);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVisit);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Error creating visit from suggestion: " + e.getMessage());
        }
    }
}
