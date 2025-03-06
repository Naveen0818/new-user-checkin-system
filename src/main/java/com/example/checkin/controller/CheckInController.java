package com.example.checkin.controller;

import com.example.checkin.model.CheckIn;
import com.example.checkin.model.User;
import com.example.checkin.service.CheckInService;
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
@RequestMapping("/checkins")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/location/{locationId}")
    public ResponseEntity<CheckIn> checkIn(
            @AuthenticationPrincipal User user,
            @PathVariable Long locationId) {
        try {
            CheckIn checkIn = checkInService.checkIn(user.getId(), locationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(checkIn);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{checkInId}/checkout")
    public ResponseEntity<CheckIn> checkOut(@PathVariable Long checkInId) {
        try {
            CheckIn checkIn = checkInService.checkOut(checkInId);
            return ResponseEntity.ok(checkIn);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<CheckIn> getActiveCheckIn(@AuthenticationPrincipal User user) {
        return checkInService.getActiveCheckIn(user.getId())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active check-in found"));
    }

    @GetMapping("/user")
    public ResponseEntity<List<CheckIn>> getUserCheckIns(@AuthenticationPrincipal User user) {
        List<CheckIn> checkIns = checkInService.getCheckInsByUser(user.getId());
        return ResponseEntity.ok(checkIns);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE') or #userId == authentication.principal.id")
    public ResponseEntity<List<CheckIn>> getUserCheckInsById(@PathVariable Long userId) {
        List<CheckIn> checkIns = checkInService.getCheckInsByUser(userId);
        return ResponseEntity.ok(checkIns);
    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE')")
    public ResponseEntity<List<CheckIn>> getLocationCheckIns(@PathVariable Long locationId) {
        List<CheckIn> checkIns = checkInService.getCheckInsByLocation(locationId);
        return ResponseEntity.ok(checkIns);
    }

    @GetMapping("/user/daterange")
    public ResponseEntity<List<CheckIn>> getUserCheckInsByDateRange(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<CheckIn> checkIns = checkInService.getCheckInsByUserAndDateRange(user.getId(), start, end);
        return ResponseEntity.ok(checkIns);
    }

    @GetMapping("/manager/daterange")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE')")
    public ResponseEntity<List<CheckIn>> getManagerCheckInsByDateRange(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<CheckIn> checkIns = checkInService.getCheckInsByManagerAndDateRange(user.getId(), start, end);
        return ResponseEntity.ok(checkIns);
    }

    @GetMapping("/count/user/daterange")
    public ResponseEntity<Long> countUserCheckInsByDateRange(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        long count = checkInService.countCheckInsByUserAndDateRange(user.getId(), start, end);
        return ResponseEntity.ok(count);
    }
}
