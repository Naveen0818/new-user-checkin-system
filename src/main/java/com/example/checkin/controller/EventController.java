package com.example.checkin.controller;

import com.example.checkin.model.Event;
import com.example.checkin.model.EventStatus;
import com.example.checkin.model.User;
import com.example.checkin.service.EventService;
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
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/location/{locationId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE')")
    public ResponseEntity<Event> createEvent(
            @AuthenticationPrincipal User user,
            @PathVariable Long locationId,
            @Valid @RequestBody Event event) {
        try {
            Event createdEvent = eventService.createEvent(event, user.getId(), locationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{eventId}/status/{status}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE')")
    public ResponseEntity<Event> updateEventStatus(
            @PathVariable Long eventId,
            @PathVariable EventStatus status) {
        try {
            Event updatedEvent = eventService.updateEventStatus(eventId, status);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        try {
            eventService.deleteEvent(eventId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable Long eventId) {
        Event event = eventService.getEventById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return ResponseEntity.ok(event);
    }

    @GetMapping("/organizer")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE')")
    public ResponseEntity<List<Event>> getOrganizerEvents(@AuthenticationPrincipal User user) {
        List<Event> events = eventService.getEventsByOrganizer(user.getId());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<Event>> getLocationEvents(@PathVariable Long locationId) {
        List<Event> events = eventService.getEventsByLocation(locationId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Event>> getEventsByStatus(@PathVariable EventStatus status) {
        List<Event> events = eventService.getEventsByStatus(status);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/daterange")
    public ResponseEntity<List<Event>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Event> events = eventService.getEventsByDateRange(start, end);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/attendee")
    public ResponseEntity<List<Event>> getAttendeeEvents(@AuthenticationPrincipal User user) {
        List<Event> events = eventService.getEventsByAttendee(user.getId());
        return ResponseEntity.ok(events);
    }

    @PostMapping("/{eventId}/attendees/{userId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE')")
    public ResponseEntity<Event> addAttendee(
            @PathVariable Long eventId,
            @PathVariable Long userId) {
        try {
            Event event = eventService.addAttendee(eventId, userId);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{eventId}/attendees/{userId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('EXECUTIVE')")
    public ResponseEntity<Event> removeAttendee(
            @PathVariable Long eventId,
            @PathVariable Long userId) {
        try {
            Event event = eventService.removeAttendee(eventId, userId);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
