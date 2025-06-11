package com.cab.book.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cab.book.entity.Booking;
import com.cab.book.entity.Cab;
import com.cab.book.entity.CabStatus;
import com.cab.book.entity.TimeSlot;
import com.cab.book.entity.User;
import com.cab.book.entity.CabAvailability;
import com.cab.book.repository.BookingRepository;
import com.cab.book.repository.CabRepository;
import com.cab.book.repository.TimeSlotRepository;
import com.cab.book.repository.CabAvailabilityRepository;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class BookingService {
	
	private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private TimeSlotRepository timeSlotRepository;
	
	@Autowired
	private CabService cabService;
	
	@Autowired
	private CabRepository cabRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private CabAvailabilityRepository cabAvailabilityRepository;
	
	
	
	public Booking createBooking(User user, String pickup, String drop, LocalTime time, LocalDate date) throws Exception {
		logger.info("Creating new booking for user: {}", user.getEmail());
		
		TimeSlot slot = timeSlotRepository.findBySlotTime(time)
			.orElseGet(() -> {
				TimeSlot newSlot = new TimeSlot();
				newSlot.setSlotTime(time);
				newSlot.setBooked(false);
				return timeSlotRepository.save(newSlot);
			});
		
		if (slot.isBooked()) {
			logger.warn("Time slot {} is already booked", time);
			throw new Exception("Time Slot already booked");
		}
		
		// Get available cabs for the specific date
		List<Cab> availableCabs = cabService.getAvailableCabsForDate(date);
		
		if (availableCabs.isEmpty()) {
			logger.warn("No available cabs found for date: {}", date);
			throw new Exception("No available cabs for the selected date");
		}
		
		// For now, select the first available cab
		Cab cab = availableCabs.get(0);
		
		logger.debug("Found available cab: {}", cab.getRegistrationNumber());
		
		Booking booking = new Booking();
		booking.setUser(user);
		booking.setCab(cab);
		booking.setPickupLocation(pickup);
		booking.setDropLocation(drop);
		booking.setDate(date);
		booking.setTimeSlot(slot);
		
		// Create cab availability record for this date
		CabAvailability cabAvailability = new CabAvailability(cab, date, CabStatus.BOOKED);
		cabAvailabilityRepository.save(cabAvailability);
		
		slot.setBooked(true);
		timeSlotRepository.save(slot);
		
		Booking saved = bookingRepository.save(booking);
		logger.info("Booking created successfully with ID: {}", saved.getId());
		
		try {
			// Send confirmation email to customer
			logger.debug("Sending booking confirmation email to customer...");
			emailService.sendBookingConfirmation(user.getEmail(), saved);
			
			// Send notification email to driver
			logger.debug("Sending booking notification email to driver...");
			emailService.sendDriverBookingNotification(saved);
			
			logger.info("All notification emails sent successfully");
		} catch (Exception e) {
			logger.error("Failed to send notification emails: {}", e.getMessage());
			// Don't throw the exception as booking is still valid
		}
		
		return saved;
	}
	
	public List<Booking> getBookingsByUserEmail(String email) {
		return bookingRepository.findByUserEmail(email);
	}
	
	public Booking getBookingsByIdAndUserEmail(Long id, String email) {
		return bookingRepository.findByIdAndUserEmail(id, email);
	}
	
	@Transactional
	public void cancelBooking(Long id, String userEmail) throws Exception {
		logger.info("Cancelling booking {} for user {}", id, userEmail);
		
		Booking booking = bookingRepository.findByIdAndUserEmail(id, userEmail);
		if (booking == null) {
			logger.warn("Booking {} not found for user {}", id, userEmail);
			throw new Exception("Booking not found");
		}
		
		// Release the time slot
		TimeSlot slot = booking.getTimeSlot();
		slot.setBooked(false);
		timeSlotRepository.save(slot);
		
		// Update cab availability
		Cab cab = booking.getCab();
		CabAvailability cabAvailability = cabAvailabilityRepository
			.findByCabAndDate(cab, booking.getDate())
			.orElse(new CabAvailability(cab, booking.getDate(), CabStatus.AVAILABLE));
		cabAvailability.setStatus(CabStatus.AVAILABLE);
		cabAvailabilityRepository.save(cabAvailability);
		
		// Delete the booking
		bookingRepository.delete(booking);
		
		try {
			// Send cancellation email to customer
			logger.debug("Sending cancellation email to customer...");
			emailService.sendBookingCancellation(userEmail, booking);
			
			// Send cancellation notification to driver
			logger.debug("Sending cancellation notification to driver...");
			emailService.sendDriverCancellationNotification(booking);
			
			logger.info("All cancellation notifications sent successfully");
		} catch (Exception e) {
			logger.error("Failed to send cancellation notifications: {}", e.getMessage());
			// Don't throw the exception as cancellation is still valid
		}
	}
}