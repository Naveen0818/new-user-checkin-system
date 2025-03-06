package com.example.checkin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "planned_visits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannedVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private LocalDateTime plannedStartTime;

    @Column(nullable = false)
    private LocalDateTime plannedEndTime;

    @Column
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitStatus status;
}
