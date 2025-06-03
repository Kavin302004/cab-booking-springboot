package com.cab.book.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cab.book.entity.Booking;
import com.cab.book.entity.Cab;
import com.cab.book.entity.TimeSlot;
import com.cab.book.entity.User;
import com.cab.book.repository.BookingRepository;
import com.cab.book.repository.TimeSlotRepository;

@Service
public class BookingService {
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private TimeSlotRepository timeSlotRepository;
	
	@Autowired
	private CabService cabService;
	
	public Booking createBooking(User user,String pickup,String drop , LocalTime Time,LocalDate date) throws Exception {
		TimeSlot slot=timeSlotRepository.findBySlotTime(Time).orElseGet(()->timeSlotRepository.save(new TimeSlot(Time)));
		
		if(slot.isBooked()) {
			throw new Exception("Time Slot already booked");
			
		}
		
		Cab cab=findNearestAvailableCab(pickup,date,slot);
		
		if(cab==null) {
			throw new Exception("No available cabs for the selected time");
			
		}
		Booking booking = new Booking(user,cab,pickup,drop,date,slot);
		
		timeSlotRepository.save(slot);
		return bookingRepository.save(booking);
		
		
	}
	public Cab findNearestAvailableCab(String pickup,LocalDate Date,TimeSlot slot) {
		List<Cab> availableCabs=cabService.getAvailableCabs();
		
		return availableCabs.stream()
							.filter(cab -> !bookingRepository.existsByCabAndDateAndTimeSlot(cab, Date, slot))
							.findFirst()
							.orElse(null);
		
	}
	
}