package com.cab.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cab.book.entity.*;
import com.cab.book.repository.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OtpValidationService {
    private static final Logger logger = LoggerFactory.getLogger(OtpValidationService.class);
    
    @Autowired
    private BookingOtpRepository bookingOtpRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private CabAvailabilityRepository cabAvailabilityRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Transactional
    public Map<String, Object> validateOtpForDriver(String otp, String driverEmail) {
        logger.info("Validating OTP: {} for driver: {}", otp, driverEmail);
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find the driver
            Driver driver = driverRepository.findByEmail(driverEmail)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
            
            // Find the OTP
            Optional<BookingOtp> bookingOtpOpt = bookingOtpRepository.findByOtpAndIsUsedFalse(otp);
            
            if (bookingOtpOpt.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Invalid or already used OTP");
                return response;
            }
            
            BookingOtp bookingOtp = bookingOtpOpt.get();
            Booking booking = bookingOtp.getBooking();
            
            // Verify if OTP has expired
            if (LocalDateTime.now().isAfter(bookingOtp.getValidUntil())) {
                response.put("status", "error");
                response.put("message", "OTP has expired");
                return response;
            }
            
            // Verify if the driver is assigned to the booking's cab
            if (!booking.getCab().getDriver().getId().equals(driver.getId())) {
                response.put("status", "error");
                response.put("message", "You are not authorized to validate this booking");
                logger.warn("Driver {} attempted to validate booking assigned to driver {}", 
                    driver.getId(), booking.getCab().getDriver().getId());
                return response;
            }
            
            // Update cab availability for this date
            Cab cab = booking.getCab();
            CabAvailability cabAvailability = cabAvailabilityRepository
                .findByCabAndDate(cab, booking.getDate())
                .orElse(new CabAvailability(cab, booking.getDate(), CabStatus.AVAILABLE));
            
            cabAvailability.setStatus(CabStatus.BOOKED);
            cabAvailabilityRepository.save(cabAvailability);
            
            // Mark OTP as used
            bookingOtp.setUsed(true);
            bookingOtpRepository.save(bookingOtp);
            
            // Send ride started notification to customer
            try {
                logger.debug("Sending ride started notification to customer...");
                emailService.sendRideStartedNotification(booking.getUser().getEmail(), booking);
            } catch (Exception e) {
                logger.error("Failed to send ride started notification: {}", e.getMessage());
                // Don't throw the exception as validation is still valid
            }
            
            // Return success response with booking details
            response.put("status", "success");
            response.put("message", "OTP validated successfully");
            response.put("bookingId", booking.getId());
            response.put("pickupLocation", booking.getPickupLocation());
            response.put("dropLocation", booking.getDropLocation());
            response.put("date", booking.getDate().toString());
            response.put("timeSlot", booking.getTimeSlot().getSlotTime().toString());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error validating OTP: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "Error validating OTP: " + e.getMessage());
            return response;
        }
    }
} 