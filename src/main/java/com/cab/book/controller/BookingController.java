package com.cab.book.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cab.book.entity.Booking;
import com.cab.book.entity.User;
import com.cab.book.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
	
	@Autowired
	private BookingService bookingService;
	
	@PostMapping
	public ResponseEntity<?> bookCab(
			@RequestParam String pickup,
			@RequestParam String drop,
			@RequestParam String date,
			@RequestParam String time,
			@AuthenticationPrincipal User user
			) {
		try {
			Booking booking = bookingService.createBooking(user, pickup, drop, LocalTime.parse(time), LocalDate.parse(date));
			return ResponseEntity.ok(booking);
		}
		catch(Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}