package com.cab.book.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cab.book.entity.Booking;
import com.cab.book.entity.User;
import com.cab.book.repository.UserRepository;
import com.cab.book.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
	
	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping
	public ResponseEntity<List<Booking>> getUserBookings(
			
			@AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser
			)
	{
		List<Booking> bookings=bookingService.getBookingsByUserEmail(securityUser.getUsername());
		
		return ResponseEntity.ok(bookings);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Booking> getBooking(
			@PathVariable("id") Long id,
			@AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser
			) {
		
		Booking booking=bookingService.getBookingsByIdAndUserEmail(id,securityUser.getUsername());
		
		return ResponseEntity.ok(booking);
	}
	
	
	
	
	
	
	
	@PostMapping
	public ResponseEntity<?> bookCab(
			@RequestParam String pickup,
			@RequestParam String drop,
			@RequestParam String date,
			@RequestParam String time,
			@AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser
			) {
		try {
			User user = userRepository.findByEmail(securityUser.getUsername())
		            .orElseThrow(() -> new Exception(securityUser.getUsername()));
			
			System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			Booking booking = bookingService.createBooking(user, pickup, drop, LocalTime.parse(time), LocalDate.parse(date));
			
			return ResponseEntity.ok(booking);
		}
		catch(Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	
	
	@DeleteMapping("/{id}")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser) throws Exception {
		User user = userRepository.findByEmail(securityUser.getUsername()).orElseThrow(()-> new Exception(securityUser.getUsername()));
        bookingService.cancelBooking(id, user.getEmail());
        return ResponseEntity.ok("Booking cancelled successfully");
    }
}