package com.cab.book.controller;

import com.cab.book.dto.NlpRequest;
import com.cab.book.service.NlpBookingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.cab.book.entity.User;
import com.cab.book.repository.UserRepository;

@RestController
@RequestMapping("/api/nlp-booking")

public class NlpBookingController {
	
    private final NlpBookingService nlpBookingService;
    
    @Autowired
    private UserRepository userRepository;

    public NlpBookingController(NlpBookingService bookingService) {
    	this.nlpBookingService=bookingService;
    }
    @PostMapping
    public ResponseEntity<?> bookFromText(
            @RequestBody NlpRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser) throws Exception {
    	User user = userRepository.findByEmail(securityUser.getUsername())
	            .orElseThrow(() -> new Exception(securityUser.getUsername()));
        return ResponseEntity.ok(nlpBookingService.processNlpBooking(request.getText(), user));
    	
    	
    }
}