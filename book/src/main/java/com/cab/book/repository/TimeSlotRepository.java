package com.cab.book.repository;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cab.book.entity.TimeSlot;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
	Optional<TimeSlot> findBySlotTime(LocalTime slotTime);
}