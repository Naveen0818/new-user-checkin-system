package com.example.checkin.service;

import com.example.checkin.model.Location;
import com.example.checkin.repository.LocationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public Optional<Location> getLocationByName(String name) {
        return locationRepository.findByName(name);
    }

    @Transactional
    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    @Transactional
    public Location updateLocation(Location location) {
        return locationRepository.save(location);
    }

    @Transactional
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return locationRepository.existsByName(name);
    }
}
