package com.example.checkin.service;

import com.example.checkin.model.*;
import com.example.checkin.repository.*;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class DataInitializationService {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);
    
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CheckInRepository checkInRepository;
    private final PlannedVisitRepository plannedVisitRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker;
    private final Random random;

    public DataInitializationService(
            UserRepository userRepository,
            LocationRepository locationRepository,
            CheckInRepository checkInRepository,
            PlannedVisitRepository plannedVisitRepository,
            EventRepository eventRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.checkInRepository = checkInRepository;
        this.plannedVisitRepository = plannedVisitRepository;
        this.eventRepository = eventRepository;
        this.passwordEncoder = passwordEncoder;
        this.faker = new Faker();
        this.random = new Random();
    }

    @PostConstruct
    @Transactional
    public void initData() {
        try {
            logger.info("Starting data initialization...");
            
            // Create locations
            List<Location> locations = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                try {
                    Location location = Location.builder()
                            .name(faker.company().name() + " Office")
                            .address(faker.address().streetAddress())
                            .city(faker.address().city())
                            .state(faker.address().stateAbbr())
                            .zipCode(faker.address().zipCode())
                            .build();
                    locations.add(locationRepository.save(location));
                    logger.info("Created location: {}", location.getName());
                } catch (Exception e) {
                    logger.error("Error creating location: {}", e.getMessage());
                    throw e;
                }
            }

            // Create users with hierarchy
            List<User> users = new ArrayList<>();
            
            // Create executives (2)
            List<User> executives = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                try {
                    User executive = createUser(Role.EXECUTIVE, null, locations.get(i % locations.size()));
                    executives.add(executive);
                    users.add(executive);
                    userRepository.save(executive);
                    logger.info("Created executive: {}", executive.getUsername());
                } catch (Exception e) {
                    logger.error("Error creating executive: {}", e.getMessage());
                    throw e;
                }
            }

            // Create directors (4 per executive)
            List<User> directors = new ArrayList<>();
            for (User executive : executives) {
                for (int i = 0; i < 4; i++) {
                    try {
                        User director = createUser(Role.DIRECTOR, executive, locations.get(random.nextInt(locations.size())));
                        directors.add(director);
                        users.add(director);
                        userRepository.save(director);
                        logger.info("Created director: {} reporting to {}", director.getUsername(), executive.getUsername());
                    } catch (Exception e) {
                        logger.error("Error creating director: {}", e.getMessage());
                        throw e;
                    }
                }
            }

            // Create managers (3 per director)
            List<User> managers = new ArrayList<>();
            for (User director : directors) {
                for (int i = 0; i < 3; i++) {
                    try {
                        User manager = createUser(Role.MANAGER, director, locations.get(random.nextInt(locations.size())));
                        managers.add(manager);
                        users.add(manager);
                        userRepository.save(manager);
                        logger.info("Created manager: {} reporting to {}", manager.getUsername(), director.getUsername());
                    } catch (Exception e) {
                        logger.error("Error creating manager: {}", e.getMessage());
                        throw e;
                    }
                }
            }

            // Create regular users (4 per manager)
            for (User manager : managers) {
                for (int i = 0; i < 4; i++) {
                    try {
                        User user = createUser(Role.USER, manager, locations.get(random.nextInt(locations.size())));
                        users.add(user);
                        userRepository.save(user);
                        logger.info("Created user: {} reporting to {}", user.getUsername(), manager.getUsername());
                    } catch (Exception e) {
                        logger.error("Error creating user: {}", e.getMessage());
                        throw e;
                    }
                }
            }

            // Create check-ins (3 per user)
            for (User user : users) {
                for (int i = 0; i < 3; i++) {
                    try {
                        LocalDateTime checkInTime = LocalDateTime.now().minusDays(random.nextInt(30));
                        CheckIn checkIn = CheckIn.builder()
                                .user(user)
                                .location(user.getLocation())
                                .checkInTime(checkInTime)
                                .checkOutTime(checkInTime.plusHours(random.nextInt(8) + 4))
                                .build();
                        checkInRepository.save(checkIn);
                        logger.debug("Created check-in for user: {}", user.getUsername());
                    } catch (Exception e) {
                        logger.error("Error creating check-in: {}", e.getMessage());
                        throw e;
                    }
                }
            }

            // Create planned visits (2 per user)
            for (User user : users) {
                for (int i = 0; i < 2; i++) {
                    try {
                        LocalDateTime startTime = LocalDateTime.now().plusDays(random.nextInt(14));
                        PlannedVisit visit = PlannedVisit.builder()
                                .user(user)
                                .location(user.getLocation())
                                .plannedStartTime(startTime.withHour(9).withMinute(0))
                                .plannedEndTime(startTime.withHour(17).withMinute(0))
                                .purpose(faker.lorem().sentence())
                                .status(VisitStatus.PLANNED)
                                .build();
                        plannedVisitRepository.save(visit);
                        logger.debug("Created planned visit for user: {}", user.getUsername());
                    } catch (Exception e) {
                        logger.error("Error creating planned visit: {}", e.getMessage());
                        throw e;
                    }
                }
            }

            // Create events (2 per manager)
            for (User manager : managers) {
                for (int i = 0; i < 2; i++) {
                    try {
                        LocalDateTime startTime = LocalDateTime.now().plusDays(random.nextInt(14));
                        List<User> attendees = new ArrayList<>();
                        for (User user : users) {
                            if (user.getManager() == manager && random.nextBoolean()) {
                                attendees.add(user);
                            }
                        }
                        
                        Event event = Event.builder()
                                .title(faker.lorem().sentence(3))
                                .description(faker.lorem().paragraph())
                                .organizer(manager)
                                .location(manager.getLocation())
                                .startTime(startTime.withHour(10).withMinute(0))
                                .endTime(startTime.withHour(12).withMinute(0))
                                .attendees(attendees)
                                .status(EventStatus.SCHEDULED)
                                .build();
                        eventRepository.save(event);
                        logger.info("Created event: {} organized by {}", event.getTitle(), manager.getUsername());
                    } catch (Exception e) {
                        logger.error("Error creating event: {}", e.getMessage());
                        throw e;
                    }
                }
            }
            
            logger.info("Data initialization completed successfully");
        } catch (Exception e) {
            logger.error("Error during data initialization: {}", e.getMessage(), e);
            throw e;
        }
    }

    private User createUser(Role role, User manager, Location location) {
        try {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String baseUsername = (firstName.charAt(0) + lastName).toLowerCase();
            String username = baseUsername + random.nextInt(1000); // Add random number suffix
            
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode("password"))
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(faker.internet().emailAddress(firstName + "." + lastName))
                    .role(role)
                    .manager(manager)
                    .location(location)
                    .build();
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            throw e;
        }
    }
}
