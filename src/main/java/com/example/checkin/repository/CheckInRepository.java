package com.example.checkin.repository;

import com.example.checkin.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    
    List<CheckIn> findByUserId(Long userId);
    
    List<CheckIn> findByLocationId(Long locationId);
    
    List<CheckIn> findByUserIdAndCheckInTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT c FROM CheckIn c WHERE c.user.id = :userId AND c.checkOutTime IS NULL")
    Optional<CheckIn> findActiveCheckInByUserId(@Param("userId") Long userId);
    
    @Query("SELECT c FROM CheckIn c WHERE c.user.manager.id = :managerId AND c.checkInTime BETWEEN :start AND :end")
    List<CheckIn> findByManagerIdAndCheckInTimeBetween(
            @Param("managerId") Long managerId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(c) FROM CheckIn c WHERE c.user.id = :userId AND c.checkInTime BETWEEN :start AND :end")
    long countByUserIdAndCheckInTimeBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
