package com.cab.book.service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Service;

import com.cab.book.dto.NlpRequest;
import com.cab.book.dto.NlpResponse;
import com.cab.book.entity.Booking;
import com.cab.book.entity.User;
import com.cab.book.nlp.NlpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class NlpBookingService {
	private final NlpClient nlpClient;
    private final BookingService bookingService;
    private static final Logger log = LoggerFactory.getLogger(NlpBookingService.class);

    public NlpBookingService(NlpClient nlpClient, BookingService bookingService) {
        this.nlpClient = nlpClient;
        this.bookingService = bookingService;
    }
    
    public Booking processNlpBooking(String text , User user ) throws Exception{
    	NlpResponse parsed =nlpClient.parseText(new NlpRequest(text));
    	log.info("Python Service Response: {}", parsed);
    	if (parsed.getLocalTime() == null) {
            throw new IllegalArgumentException("Time cannot be null from NLP service");
        }
    	
    	Booking booking= bookingService.createBooking(user, parsed.getPickupLocation(), parsed.getDropLocation(),parsed.getLocalTime() , parsed.getLocalDate());
    
    	return booking;
    	
    	
		
    	
    	
    }

}