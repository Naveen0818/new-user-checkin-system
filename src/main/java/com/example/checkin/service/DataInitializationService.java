package com.example.checkin.service;

import com.example.checkin.model.*;
import com.example.checkin.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class DataInitializationService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CheckInRepository checkInRepository;
    private final PlannedVisitRepository plannedVisitRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

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
    }

    @PostConstruct
    @Transactional
    public void initData() {
        // Create locations
        Location hq = Location.builder()
                .name("Headquarters")
                .address("123 Main St")
                .city("San Francisco")
                .state("CA")
                .zipCode("94105")
                .build();
        locationRepository.save(hq);

        Location branch1 = Location.builder()
                .name("Branch Office 1")
                .address("456 Market St")
                .city("San Francisco")
                .state("CA")
                .zipCode("94103")
                .build();
        locationRepository.save(branch1);

        // Create users with hierarchy
        User executive = User.builder()
                .username("executive")
                .password(passwordEncoder.encode("password"))
                .firstName("John")
                .lastName("Executive")
                .email("john.executive@example.com")
                .role(Role.EXECUTIVE)
                .location(hq)
                .build();
        userRepository.save(executive);

        User director1 = User.builder()
                .username("director1")
                .password(passwordEncoder.encode("password"))
                .firstName("Jane")
                .lastName("Director")
                .email("jane.director@example.com")
                .role(Role.DIRECTOR)
                .manager(executive)
                .location(hq)
                .build();
        userRepository.save(director1);

        User director2 = User.builder()
                .username("director2")
                .password(passwordEncoder.encode("password"))
                .firstName("Bob")
                .lastName("Director")
                .email("bob.director@example.com")
                .role(Role.DIRECTOR)
                .manager(executive)
                .location(branch1)
                .build();
        userRepository.save(director2);

        User manager1 = User.builder()
                .username("manager1")
                .password(passwordEncoder.encode("password"))
                .firstName("Alice")
                .lastName("Manager")
                .email("alice.manager@example.com")
                .role(Role.MANAGER)
                .manager(director1)
                .location(hq)
                .build();
        userRepository.save(manager1);

        User manager2 = User.builder()
                .username("manager2")
                .password(passwordEncoder.encode("password"))
                .firstName("Charlie")
                .lastName("Manager")
                .email("charlie.manager@example.com")
                .role(Role.MANAGER)
                .manager(director2)
                .location(branch1)
                .build();
        userRepository.save(manager2);

        User user1 = User.builder()
                .username("user1")
                .password(passwordEncoder.encode("password"))
                .firstName("David")
                .lastName("User")
                .email("david.user@example.com")
                .role(Role.USER)
                .manager(manager1)
                .location(hq)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("password"))
                .firstName("Eva")
                .lastName("User")
                .email("eva.user@example.com")
                .role(Role.USER)
                .manager(manager1)
                .location(hq)
                .build();
        userRepository.save(user2);

        User user3 = User.builder()
                .username("user3")
                .password(passwordEncoder.encode("password"))
                .firstName("Frank")
                .lastName("User")
                .email("frank.user@example.com")
                .role(Role.USER)
                .manager(manager2)
                .location(branch1)
                .build();
        userRepository.save(user3);

        // Create check-ins
        CheckIn checkIn1 = CheckIn.builder()
                .user(user1)
                .location(hq)
                .checkInTime(LocalDateTime.now().minusDays(1))
                .checkOutTime(LocalDateTime.now().minusDays(1).plusHours(8))
                .build();
        checkInRepository.save(checkIn1);

        CheckIn checkIn2 = CheckIn.builder()
                .user(user2)
                .location(hq)
                .checkInTime(LocalDateTime.now().minusDays(1))
                .checkOutTime(LocalDateTime.now().minusDays(1).plusHours(7))
                .build();
        checkInRepository.save(checkIn2);

        // Create planned visits
        PlannedVisit plannedVisit1 = PlannedVisit.builder()
                .user(user1)
                .location(hq)
                .plannedStartTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0))
                .plannedEndTime(LocalDateTime.now().plusDays(1).withHour(17).withMinute(0))
                .purpose("Regular work day")
                .status(VisitStatus.PLANNED)
                .build();
        plannedVisitRepository.save(plannedVisit1);

        PlannedVisit plannedVisit2 = PlannedVisit.builder()
                .user(user2)
                .location(hq)
                .plannedStartTime(LocalDateTime.now().plusDays(2).withHour(9).withMinute(0))
                .plannedEndTime(LocalDateTime.now().plusDays(2).withHour(17).withMinute(0))
                .purpose("Regular work day")
                .status(VisitStatus.PLANNED)
                .build();
        plannedVisitRepository.save(plannedVisit2);

        // Create events
        Event event1 = Event.builder()
                .title("Team Meeting")
                .description("Weekly team meeting")
                .organizer(manager1)
                .location(hq)
                .startTime(LocalDateTime.now().plusDays(3).withHour(10).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(3).withHour(11).withMinute(0))
                .attendees(Arrays.asList(user1, user2))
                .status(EventStatus.SCHEDULED)
                .build();
        eventRepository.save(event1);

        Event event2 = Event.builder()
                .title("Project Kickoff")
                .description("New project kickoff meeting")
                .organizer(manager2)
                .location(branch1)
                .startTime(LocalDateTime.now().plusDays(4).withHour(14).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(4).withHour(16).withMinute(0))
                .attendees(Arrays.asList(user3))
                .status(EventStatus.SCHEDULED)
                .build();
        eventRepository.save(event2);
    }
}
