package com.example.checkin.repository;

import com.example.checkin.model.Event;
import com.example.checkin.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByOrganizerId(Long organizerId);
    
    List<Event> findByLocationId(Long locationId);
    
    List<Event> findByStatus(EventStatus status);
    
    List<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT e FROM Event e JOIN e.attendees a WHERE a.id = :userId")
    List<Event> findByAttendeeId(@Param("userId") Long userId);
    
    @Query("SELECT e FROM Event e WHERE e.organizer.id = :organizerId AND e.startTime BETWEEN :start AND :end")
    List<Event> findByOrganizerIdAndStartTimeBetween(
            @Param("organizerId") Long organizerId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
