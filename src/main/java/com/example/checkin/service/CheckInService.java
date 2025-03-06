package com.example.checkin.service;

import com.example.checkin.model.CheckIn;
import com.example.checkin.model.Location;
import com.example.checkin.model.User;
import com.example.checkin.repository.CheckInRepository;
import com.example.checkin.repository.LocationRepository;
import com.example.checkin.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public CheckInService(CheckInRepository checkInRepository, UserRepository userRepository, LocationRepository locationRepository) {
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    public List<CheckIn> getAllCheckIns() {
        return checkInRepository.findAll();
    }

    public Optional<CheckIn> getCheckInById(Long id) {
        return checkInRepository.findById(id);
    }

    public List<CheckIn> getCheckInsByUser(Long userId) {
        return checkInRepository.findByUserId(userId);
    }

    public List<CheckIn> getCheckInsByLocation(Long locationId) {
        return checkInRepository.findByLocationId(locationId);
    }

    public List<CheckIn> getCheckInsByUserAndDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return checkInRepository.findByUserIdAndCheckInTimeBetween(userId, start, end);
    }

    public List<CheckIn> getCheckInsByManagerAndDateRange(Long managerId, LocalDateTime start, LocalDateTime end) {
        return checkInRepository.findByManagerIdAndCheckInTimeBetween(managerId, start, end);
    }

    public Optional<CheckIn> getActiveCheckIn(Long userId) {
        return checkInRepository.findActiveCheckInByUserId(userId);
    }

    @Transactional
    public CheckIn checkIn(Long userId, Long locationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        // Check if user already has an active check-in
        Optional<CheckIn> activeCheckIn = checkInRepository.findActiveCheckInByUserId(userId);
        if (activeCheckIn.isPresent()) {
            throw new RuntimeException("User already has an active check-in");
        }
        
        CheckIn checkIn = CheckIn.builder()
                .user(user)
                .location(location)
                .checkInTime(LocalDateTime.now())
                .build();
        
        return checkInRepository.save(checkIn);
    }

    @Transactional
    public CheckIn checkOut(Long checkInId) {
        CheckIn checkIn = checkInRepository.findById(checkInId)
                .orElseThrow(() -> new RuntimeException("Check-in not found"));
        
        if (checkIn.getCheckOutTime() != null) {
            throw new RuntimeException("User has already checked out");
        }
        
        checkIn.setCheckOutTime(LocalDateTime.now());
        return checkInRepository.save(checkIn);
    }

    public long countCheckInsByUserAndDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return checkInRepository.countByUserIdAndCheckInTimeBetween(userId, start, end);
    }
}
