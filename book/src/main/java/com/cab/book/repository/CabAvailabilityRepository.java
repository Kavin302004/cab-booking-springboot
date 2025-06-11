package com.cab.book.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cab.book.entity.CabAvailability;
import com.cab.book.entity.Cab;
import com.cab.book.entity.CabStatus;

public interface CabAvailabilityRepository extends JpaRepository<CabAvailability, Long> {
    Optional<CabAvailability> findByCabAndDate(Cab cab, LocalDate date);
    List<CabAvailability> findByDateAndStatus(LocalDate date, CabStatus status);
    List<CabAvailability> findByCabAndDateGreaterThanEqual(Cab cab, LocalDate date);
} 