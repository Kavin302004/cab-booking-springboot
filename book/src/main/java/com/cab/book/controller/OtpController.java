package com.cab.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.cab.book.service.OtpService;
import com.cab.book.service.OtpValidationService;
import com.cab.book.repository.BookingOtpRepository;
import com.cab.book.entity.BookingOtp;
import java.util.Map;
import java.util.HashMap;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/otp")
public class OtpController {
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    private OtpValidationService otpValidationService;
    
    @Autowired
    private BookingOtpRepository bookingOtpRepository;
    
    @PostMapping("/validate")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> validateOtp(
            @RequestParam String otp,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User driver) {
        
        Map<String, Object> validationResult = otpValidationService.validateOtpForDriver(otp, driver.getUsername());
        
        if ("error".equals(validationResult.get("status"))) {
            return ResponseEntity.badRequest().body(validationResult);
        }
        
        return ResponseEntity.ok(validationResult);
    }
    
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getOtpForBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser) {
        return bookingOtpRepository.findByBooking_IdAndIsUsedFalse(bookingId)
            .map(bookingOtp -> {
                Map<String, Object> response = new HashMap<>();
                response.put("otp", bookingOtp.getOtp());
                response.put("validUntil", bookingOtp.getValidUntil());
                response.put("isUsed", bookingOtp.isUsed());
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }
} 