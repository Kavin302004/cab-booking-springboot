package com.cab.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cab.book.entity.BookingOtp;
import com.cab.book.entity.Booking;
import java.util.Optional;

public interface BookingOtpRepository extends JpaRepository<BookingOtp, Long> {
    Optional<BookingOtp> findByBookingAndIsUsedFalse(Booking booking);
    Optional<BookingOtp> findByBooking_IdAndIsUsedFalse(Long bookingId);
    Optional<BookingOtp> findByOtpAndIsUsedFalse(String otp);
} 