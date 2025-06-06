package com.cab.book.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cab.book.entity.Booking;
import com.cab.book.entity.Cab;
import com.cab.book.entity.CabStatus;
import com.cab.book.entity.TimeSlot;
import com.cab.book.entity.User;
import com.cab.book.repository.BookingRepository;
import com.cab.book.repository.CabRepository;
import com.cab.book.repository.TimeSlotRepository;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class BookingService {
	
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
	
	
	
	public Booking createBooking(User user,String pickup,String drop , LocalTime Time,LocalDate date) throws Exception {
		TimeSlot slot = timeSlotRepository.findBySlotTime(Time)
		        .orElseGet(() -> {
		            TimeSlot newSlot = new TimeSlot();
		            newSlot.setSlotTime(Time);  // Don't use constructor with ID
		            newSlot.setBooked(false);
		            return timeSlotRepository.save(newSlot);
		        });
		
		if(slot.isBooked()) {
			throw new Exception("Time Slot already booked");
			
		}
		
		Cab cab=findNearestAvailableCab(pickup,date,slot);
		
		if(cab==null) {
			throw new Exception("No available cabs for the selected time");
			
		}
		Booking booking = new Booking();
        booking.setUser(user);  // Critical: Set user relationship
        booking.setCab(cab);
        booking.setPickupLocation(pickup);
        booking.setDropLocation(drop);
        booking.setDate(date);
        booking.setTimeSlot(slot);

        // 4. Mark slot as booked
        slot.setBooked(true);
        cab.setStatus(CabStatus.BOOKED);
        
        
        timeSlotRepository.save(slot);
        cabRepository.save(cab);
        
        
		Booking saved=bookingRepository.save(booking);
		emailService.sendBookingConfirmation(user.getEmail(), saved);
		return saved;
		
		
	}
	public Cab findNearestAvailableCab(String pickup,LocalDate Date,TimeSlot slot) {
		List<Cab> availableCabs=cabService.getAvailableCabs();
		
		return availableCabs.stream()
							.filter(cab -> !bookingRepository.existsByCabAndDateAndTimeSlot(cab, Date, slot))
							.findFirst()
							.orElse(null);
		
	}
	
	public List<Booking> getBookingsByUserEmail(String email) {
		return bookingRepository.findByUserEmail(email);
	}
	
	public Booking getBookingsByIdAndUserEmail(Long id , String email) {
		return bookingRepository.findByIdAndUserEmail(id,email);
	}
	
	@Transactional
	public void cancelBooking(Long id, String email) {
	    Booking booking = getBookingsByIdAndUserEmail(id, email);
	    
	   emailService.sendCancellationConfirmation(
	    	    booking.getUser().getEmail(),
	    	    booking.getId(),
	    	    booking.getPickupLocation(),
	    	    booking.getDropLocation(),
	    	    booking.getDate(),
	    	    booking.getTimeSlot().getSlotTime(),
	    	    booking.getCab().getRegistrationNumber(),
	    	    booking.getCab().getDriver().getName()
	    	);

	    
	    // Free up the time slot
	    TimeSlot slot = booking.getTimeSlot();
	    slot.setBooked(false);
	    timeSlotRepository.save(slot);
	    
	    
	    Cab cab = booking.getCab();
	    cab.setStatus(CabStatus.AVAILABLE);
	    cabRepository.save(cab);
	    
	    

	    
	    bookingRepository.delete(booking);
	    
	    
	    
	    
	}
}