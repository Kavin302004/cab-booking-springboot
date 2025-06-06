package com.cab.book.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cab.book.entity.Booking;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	
	private final JavaMailSender mailSender;
	
	public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
	
    public void sendBookingConfirmation(String toEmail, Booking booking) {
    	try {
    	MimeMessage message = mailSender.createMimeMessage();
    	MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        message.setSubject("Booking Confirmation #" + booking.getId());
        String content = "<p style='font-size:14px;'>Your booking is <b>Successful</b>.</p>"
                + "<p><strong>Booking Details:</strong></p>"
                + "<ul>"
                + "<li><b>Booking ID:</b> " + booking.getId() + "</li>"
                + "<li><b>Pickup:</b> " + booking.getPickupLocation() + "</li>"
                + "<li><b>Drop:</b> " + booking.getDropLocation() + "</li>"
                + "<li><b>Date:</b> " + booking.getDate() + "</li>"
                + "<li><b>Time:</b> " + booking.getTimeSlot().getSlotTime() + "</li>"
                + "<li><b>Cab:</b> " + booking.getCab().getRegistrationNumber() + "</li>"
                + "<li><b>Driver:</b> " + booking.getCab().getDriver().getName() + "</li>"
                + "</ul>"
                + "<p>Thank you for using our service.</p>";

        helper.setText(content, true);  // true = send as HTML

        mailSender.send(message);
        
        
    	}
    	catch(Exception e) {
    		
    	}
    }
    public void sendCancellationConfirmation(
            String toEmail,
            Long bookingId,
            String pickup,
            String drop,
            LocalDate date,
            LocalTime time,
            String cabNumber,
            String driverName
    ) {
    	try {
    	MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Booking Cancellation #" + bookingId);

        String content = "<p style='font-size:14px;'>Your booking has been <b>cancelled</b>.</p>"
                + "<p><strong>Booking Details:</strong></p>"
                + "<ul>"
                + "<li><b>Booking ID:</b> " + bookingId + "</li>"
                + "<li><b>Pickup:</b> " + pickup + "</li>"
                + "<li><b>Drop:</b> " + drop + "</li>"
                + "<li><b>Date:</b> " + date + "</li>"
                + "<li><b>Time:</b> " + time + "</li>"
                + "<li><b>Cab:</b> " + cabNumber + "</li>"
                + "<li><b>Driver:</b> " + driverName + "</li>"
                + "</ul>"
                + "<p>Thank you for using our service.</p>";

        helper.setText(content, true);  // true = send as HTML

        mailSender.send(message);
        
    	}
    	catch(Exception e) {
    		
    	}
    }

    private String createBookingConfirmationContent(Booking booking) {
        return String.format(
            "Your booking has been confirmed!\n\n" +
            "Booking ID: %d\n" +
            "Pickup: %s\n" +
            "Drop: %s\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Cab: %s\n" +
            "Driver: %s",
            booking.getId(),
            booking.getPickupLocation(),
            booking.getDropLocation(),
            booking.getDate(),
            booking.getTimeSlot().getSlotTime(),
            booking.getCab().getRegistrationNumber(),
            booking.getCab().getDriver().getName()
        );
    }

}