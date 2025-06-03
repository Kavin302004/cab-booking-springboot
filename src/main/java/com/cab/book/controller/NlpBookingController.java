package com.cab.book.controller;

import com.cab.book.dto.NlpRequest;
import com.cab.book.service.NlpBookingService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.cab.book.entity.User;

@RestController
@RequestMapping("/api/nlp-booking")

public class NlpBookingController {
	
    private final NlpBookingService nlpBookingService;

    public NlpBookingController(NlpBookingService bookingService) {
    	this.nlpBookingService=bookingService;
    }
    @PostMapping
    public ResponseEntity<?> bookFromText(
            @RequestBody NlpRequest request,
            @AuthenticationPrincipal User user) throws Exception {
    	
        return ResponseEntity.ok(nlpBookingService.processNlpBooking(request.getText(), user));
    	
    	
    }
}