package com.example.checkin.controller;

import com.example.checkin.model.Location;
import com.example.checkin.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        Location location = locationService.getLocationById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
        return ResponseEntity.ok(location);
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@Valid @RequestBody Location location) {
        if (locationService.existsByName(location.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location with this name already exists");
        }
        
        Location createdLocation = locationService.createLocation(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody Location location) {
        
        if (!locationService.getLocationById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found");
        }
        
        location.setId(id);
        Location updatedLocation = locationService.updateLocation(location);
        return ResponseEntity.ok(updatedLocation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        if (!locationService.getLocationById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found");
        }
        
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
