package com.example.checkin.controller;

import com.example.checkin.model.PlannedVisit;
import com.example.checkin.model.User;
import com.example.checkin.model.VisitStatus;
import com.example.checkin.service.PlannedVisitService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/visits")
public class PlannedVisitController {

    private final PlannedVisitService plannedVisitService;

    public PlannedVisitController(PlannedVisitService plannedVisitService) {
        this.plannedVisitService = plannedVisitService;
    }

    @PostMapping("/location/{locationId}")
    public ResponseEntity<PlannedVisit> createPlannedVisit(
            @AuthenticationPrincipal User user,
            @PathVariable Long locationId,
            @Valid @RequestBody PlannedVisit plannedVisit) {
        try {
            PlannedVisit createdVisit = plannedVisitService.createPlannedVisit(plannedVisit, user.getId(), locationId);
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
    public ResponseEntity<List<PlannedVisit>> getUserPlannedVisits(@AuthenticationPrincipal User user) {
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByUser(user.getId());
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE') or #userId == authentication.principal.id")
    public ResponseEntity<List<PlannedVisit>> getUserPlannedVisitsById(@PathVariable Long userId) {
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByUser(userId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE')")
    public ResponseEntity<List<PlannedVisit>> getLocationPlannedVisits(@PathVariable Long locationId) {
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByLocation(locationId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/user/status/{status}")
    public ResponseEntity<List<PlannedVisit>> getUserPlannedVisitsByStatus(
            @AuthenticationPrincipal User user,
            @PathVariable VisitStatus status) {
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByUserAndStatus(user.getId(), status);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/user/upcoming")
    public ResponseEntity<List<PlannedVisit>> getUserUpcomingVisits(@AuthenticationPrincipal User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoWeeksLater = now.plusWeeks(2);
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByUserAndDateRange(user.getId(), now, twoWeeksLater);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/manager/upcoming")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE')")
    public ResponseEntity<List<PlannedVisit>> getManagerUpcomingVisits(@AuthenticationPrincipal User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoWeeksLater = now.plusWeeks(2);
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByManagerAndDateRange(user.getId(), now, twoWeeksLater);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/peers/upcoming")
    public ResponseEntity<List<PlannedVisit>> getPeersUpcomingVisits(@AuthenticationPrincipal User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoWeeksLater = now.plusWeeks(2);
        List<PlannedVisit> visits = plannedVisitService.getPlannedVisitsByPeersAndDateRange(user.getId(), now, twoWeeksLater);
        return ResponseEntity.ok(visits);
    }
}
