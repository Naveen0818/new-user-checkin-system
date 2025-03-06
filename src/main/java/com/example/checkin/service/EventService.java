package com.example.checkin.service;

import com.example.checkin.model.Event;
import com.example.checkin.model.EventStatus;
import com.example.checkin.model.Location;
import com.example.checkin.model.User;
import com.example.checkin.repository.EventRepository;
import com.example.checkin.repository.LocationRepository;
import com.example.checkin.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public List<Event> getEventsByLocation(Long locationId) {
        return eventRepository.findByLocationId(locationId);
    }

    public List<Event> getEventsByStatus(EventStatus status) {
        return eventRepository.findByStatus(status);
    }

    public List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByStartTimeBetween(start, end);
    }

    public List<Event> getEventsByAttendee(Long userId) {
        return eventRepository.findByAttendeeId(userId);
    }

    public List<Event> getEventsByOrganizerAndDateRange(Long organizerId, LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByOrganizerIdAndStartTimeBetween(organizerId, start, end);
    }

    @Transactional
    public Event createEvent(Event event, Long organizerId, Long locationId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        Event newEvent = Event.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .organizer(organizer)
                .location(location)
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .status(EventStatus.SCHEDULED)
                .build();
        
        return eventRepository.save(newEvent);
    }

    @Transactional
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEventStatus(Long eventId, EventStatus status) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        event.setStatus(status);
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Transactional
    public Event addAttendee(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!event.getAttendees().contains(user)) {
            event.getAttendees().add(user);
            eventRepository.save(event);
        }
        
        return event;
    }

    @Transactional
    public Event removeAttendee(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        event.getAttendees().remove(user);
        return eventRepository.save(event);
    }
}
