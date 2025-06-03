package com.cab.book.service;

import org.springframework.stereotype.Service;

import com.cab.book.dto.NlpRequest;
import com.cab.book.dto.NlpResponse;
import com.cab.book.entity.Booking;
import com.cab.book.entity.User;
import com.cab.book.nlp.NlpClient;

@Service
public class NlpBookingService {
	private final NlpClient nlpClient;
    private final BookingService bookingService;

    public NlpBookingService(NlpClient nlpClient, BookingService bookingService) {
        this.nlpClient = nlpClient;
        this.bookingService = bookingService;
    }
    
    public Booking processNlpBooking(String text , User user ) throws Exception{
    	NlpResponse parsed =nlpClient.parseText(new NlpRequest(text));
    	
    	Booking booking= bookingService.createBooking(user, parsed.getPickupLocation(), parsed.getDropLocation(),parsed.getLocalTime() , parsed.getLocalDate());
    
    	return booking;
    	
    	
		
    	
    	
    }

}