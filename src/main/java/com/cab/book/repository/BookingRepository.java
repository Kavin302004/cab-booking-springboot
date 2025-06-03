package com.cab.book.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cab.book.entity.Booking;
import com.cab.book.entity.Cab;
import com.cab.book.entity.TimeSlot;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	
	List<Booking> findByDateAndTimeSlot(LocalDate date, TimeSlot timeSlot);
	boolean existsByCabAndDateAndTimeSlot(Cab cab, LocalDate date, TimeSlot timeSlot);
}