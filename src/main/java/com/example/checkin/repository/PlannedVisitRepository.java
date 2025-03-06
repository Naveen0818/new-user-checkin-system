package com.example.checkin.repository;

import com.example.checkin.model.PlannedVisit;
import com.example.checkin.model.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlannedVisitRepository extends JpaRepository<PlannedVisit, Long> {
    
    List<PlannedVisit> findByUserId(Long userId);
    
    List<PlannedVisit> findByLocationId(Long locationId);
    
    List<PlannedVisit> findByUserIdAndStatus(Long userId, VisitStatus status);
    
    List<PlannedVisit> findByUserIdAndPlannedStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT pv FROM PlannedVisit pv WHERE pv.user.manager.id = :managerId AND pv.plannedStartTime BETWEEN :start AND :end")
    List<PlannedVisit> findByManagerIdAndPlannedStartTimeBetween(
            @Param("managerId") Long managerId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT pv FROM PlannedVisit pv WHERE pv.user.id IN " +
           "(SELECT u.id FROM User u WHERE u.manager.id = " +
           "(SELECT u2.manager.id FROM User u2 WHERE u2.id = :userId)) " +
           "AND pv.plannedStartTime BETWEEN :start AND :end")
    List<PlannedVisit> findByPeersAndPlannedStartTimeBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
