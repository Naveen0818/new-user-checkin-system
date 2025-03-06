package com.example.checkin.controller;

import com.example.checkin.dto.AuthRequest;
import com.example.checkin.dto.AuthResponse;
import com.example.checkin.dto.UserDto;
import com.example.checkin.dto.UserRegistrationRequest;
import com.example.checkin.model.Location;
import com.example.checkin.model.Role;
import com.example.checkin.model.User;
import com.example.checkin.security.JwtTokenProvider;
import com.example.checkin.service.LocationService;
import com.example.checkin.service.UserService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final LocationService locationService;

    public AuthController(@Lazy AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                          UserService userService, LocationService locationService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.locationService = locationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authRequest.getUsername());
            
            User user = (User) authentication.getPrincipal();
            
            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole().name()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        if (userService.existsByUsername(registrationRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        
        if (userService.existsByEmail(registrationRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(registrationRequest.getPassword());
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setEmail(registrationRequest.getEmail());
        user.setRole(Role.USER); // Default role for new registrations
        
        if (registrationRequest.getLocationId() != null) {
            Location location = locationService.getLocationById(registrationRequest.getLocationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
            user.setLocation(location);
        }
        
        if (registrationRequest.getManagerId() != null) {
            User manager = userService.getUserById(registrationRequest.getManagerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found"));
            user.setManager(manager);
        }
        
        User savedUser = userService.createUser(user);
        
        UserDto userDto = new UserDto();
        userDto.setId(savedUser.getId());
        userDto.setUsername(savedUser.getUsername());
        userDto.setFirstName(savedUser.getFirstName());
        userDto.setLastName(savedUser.getLastName());
        userDto.setEmail(savedUser.getEmail());
        userDto.setRole(savedUser.getRole());
        
        if (savedUser.getManager() != null) {
            userDto.setManagerId(savedUser.getManager().getId());
            userDto.setManagerName(savedUser.getManager().getFirstName() + " " + savedUser.getManager().getLastName());
        }
        
        if (savedUser.getLocation() != null) {
            userDto.setLocationId(savedUser.getLocation().getId());
            userDto.setLocationName(savedUser.getLocation().getName());
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }
}
