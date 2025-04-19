package com.example.checkin.service;

import com.example.checkin.model.Location;
import com.example.checkin.model.PlannedVisit;
import com.example.checkin.model.User;
import com.example.checkin.model.VisitStatus;
import com.example.checkin.repository.LocationRepository;
import com.example.checkin.repository.PlannedVisitRepository;
import com.example.checkin.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlannedVisitService {

    private final PlannedVisitRepository plannedVisitRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public PlannedVisitService(PlannedVisitRepository plannedVisitRepository, UserRepository userRepository, LocationRepository locationRepository) {
        this.plannedVisitRepository = plannedVisitRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    public List<PlannedVisit> getAllPlannedVisits() {
        return plannedVisitRepository.findAll();
    }

    public Optional<PlannedVisit> getPlannedVisitById(Long id) {
        return plannedVisitRepository.findById(id);
    }

    public List<PlannedVisit> getPlannedVisitsByUser(Long userId) {
        return plannedVisitRepository.findByUserId(userId);
    }

    public List<PlannedVisit> getPlannedVisitsByLocation(Long locationId) {
        return plannedVisitRepository.findByLocationId(locationId);
    }

    public List<PlannedVisit> getPlannedVisitsByUserAndStatus(Long userId, VisitStatus status) {
        return plannedVisitRepository.findByUserIdAndStatus(userId, status);
    }

    public List<PlannedVisit> getPlannedVisitsByUserAndDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return plannedVisitRepository.findByUserIdAndPlannedStartTimeBetween(userId, start, end);
    }

    public List<PlannedVisit> getPlannedVisitsByManagerAndDateRange(Long managerId, LocalDateTime start, LocalDateTime end) {
        return plannedVisitRepository.findByManagerIdAndPlannedStartTimeBetween(managerId, start, end);
    }

    public List<PlannedVisit> getPlannedVisitsByPeersAndDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return plannedVisitRepository.findByPeersAndPlannedStartTimeBetween(userId, start, end);
    }

    @Transactional
    public PlannedVisit createPlannedVisit(PlannedVisit plannedVisit, Long userId, Long locationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        plannedVisit.setUser(user);
        plannedVisit.setLocation(location);
        plannedVisit.setStatus(VisitStatus.PLANNED);
        
        return plannedVisitRepository.save(plannedVisit);
    }

    @Transactional
    public PlannedVisit updatePlannedVisit(PlannedVisit plannedVisit) {
        return plannedVisitRepository.save(plannedVisit);
    }

    @Transactional
    public PlannedVisit updatePlannedVisitStatus(Long visitId, VisitStatus status) {
        PlannedVisit plannedVisit = plannedVisitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Planned visit not found"));
        
        plannedVisit.setStatus(status);
        return plannedVisitRepository.save(plannedVisit);
    }

    @Transactional
    public void deletePlannedVisit(Long id) {
        plannedVisitRepository.deleteById(id);
    }

    /**
     * Create a planned visit from an AI suggestion.
     * This streamlines the process of turning an AI recommendation into an actual scheduled visit.
     *
     * @param userId The user ID
     * @param locationId The location ID
     * @param startTime The planned start time
     * @param endTime The planned end time
     * @param purpose Optional purpose of the visit
     * @return The created planned visit
     */
    @Transactional
    public PlannedVisit createPlannedVisitFromSuggestion(
            Long userId, Long locationId, 
            LocalDateTime startTime, LocalDateTime endTime, 
            String purpose) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        PlannedVisit plannedVisit = PlannedVisit.builder()
                .user(user)
                .location(location)
                .plannedStartTime(startTime)
                .plannedEndTime(endTime)
                .purpose(purpose)
                .status(VisitStatus.PLANNED)
                .build();
        
        return plannedVisitRepository.save(plannedVisit);
    }
}
