package com.cab.book.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.cab.book.entity.Booking;
import com.cab.book.entity.BookingOtp;
import com.cab.book.entity.Driver;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
	
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private OtpService otpService;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	public void sendBookingConfirmation(String toEmail, Booking booking) {
		try {
			logger.info("Starting to send booking confirmation email to: {}", toEmail);
			
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			
			// Generate OTP and QR code
			logger.debug("Generating OTP and QR code for booking ID: {}", booking.getId());
			BookingOtp bookingOtp = otpService.createOtpForBooking(booking);
			
			// Generate QR code and get file path
			Map<String, String> qrCode = otpService.generateQrCode(bookingOtp.getOtp());
			String qrCodePath = qrCode.get("filePath");
			logger.debug("QR code generated at: {}", qrCodePath);
			
			helper.setTo(toEmail);
			helper.setSubject("Booking Confirmation #" + booking.getId());
			
			StringBuilder contentBuilder = new StringBuilder();
			contentBuilder.append("<!DOCTYPE html>");
			contentBuilder.append("<html><head><meta charset='UTF-8'></head><body>");
			contentBuilder.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
			
			// Booking confirmation header
			contentBuilder.append("<div style='background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 5px;'>");
			contentBuilder.append("<h2 style='margin:0;'>Booking Confirmed!</h2>");
			contentBuilder.append("</div>");
			
			// Booking details section
			contentBuilder.append("<div style='background-color: #f8f9fa; padding: 20px; margin-top: 20px; border-radius: 5px;'>");
			contentBuilder.append("<h3 style='color: #333; margin-top: 0;'>Booking Details</h3>");
			contentBuilder.append("<ul style='list-style-type: none; padding-left: 0;'>");
			contentBuilder.append("<li style='margin: 10px 0;'><b>Booking ID:</b> ").append(booking.getId()).append("</li>");
			contentBuilder.append("<li style='margin: 10px 0;'><b>Pickup:</b> ").append(booking.getPickupLocation()).append("</li>");
			contentBuilder.append("<li style='margin: 10px 0;'><b>Drop:</b> ").append(booking.getDropLocation()).append("</li>");
			contentBuilder.append("<li style='margin: 10px 0;'><b>Date:</b> ").append(booking.getDate()).append("</li>");
			contentBuilder.append("<li style='margin: 10px 0;'><b>Time:</b> ").append(booking.getTimeSlot().getSlotTime()).append("</li>");
			contentBuilder.append("<li style='margin: 10px 0;'><b>Cab:</b> ").append(booking.getCab().getRegistrationNumber()).append("</li>");
			contentBuilder.append("<li style='margin: 10px 0;'><b>Driver:</b> ").append(booking.getCab().getDriver().getName()).append("</li>");
			contentBuilder.append("</ul>");
			contentBuilder.append("</div>");
			
			// Authentication section
			contentBuilder.append("<div style='background-color: #e3f2fd; padding: 20px; margin-top: 20px; border-radius: 5px; text-align: center;'>");
			contentBuilder.append("<h3 style='color: #1976d2; margin-top: 0;'>Authentication Details</h3>");
			contentBuilder.append("<p style='font-size: 18px; margin: 10px 0;'><strong>OTP: </strong>");
			contentBuilder.append("<span style='color: #1976d2;'>").append(bookingOtp.getOtp()).append("</span></p>");
			
			// QR Code section
			contentBuilder.append("<div style='margin: 20px 0;'>");
			contentBuilder.append("<p><strong>Scan QR Code:</strong></p>");
			contentBuilder.append("<img src='cid:qr-code' alt='Booking QR Code' ");
			contentBuilder.append("style='width:250px; height:250px; display:block; margin:10px auto; border: 1px solid #ddd; border-radius: 5px;'/>");
			contentBuilder.append("</div>");
			contentBuilder.append("</div>");
			
			// Footer
			contentBuilder.append("<div style='margin-top: 20px; text-align: center; color: #666;'>");
			contentBuilder.append("<p><i>Please show the QR code or OTP to your driver when boarding the cab.</i></p>");
			contentBuilder.append("<p>Thank you for using our service!</p>");
			contentBuilder.append("</div>");
			
			contentBuilder.append("</div></body></html>");
			
			helper.setText(contentBuilder.toString(), true);
			
			// Attach the QR code file
			FileSystemResource qrResource = new FileSystemResource(qrCodePath);
			helper.addInline("qr-code", qrResource);
			
			logger.debug("Attempting to send email...");
			mailSender.send(message);
			logger.info("Successfully sent booking confirmation email to: {}", toEmail);
			
		} catch (MessagingException e) {
			logger.error("MessagingException while sending email to {}: {}", toEmail, e.getMessage());
			logger.error("Detailed error: ", e);
			throw new RuntimeException("Failed to create email message: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Unexpected error while sending email to {}: {}", toEmail, e.getMessage());
			logger.error("Detailed error: ", e);
			throw new RuntimeException("Error sending booking confirmation email: " + e.getMessage(), e);
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
			logger.info("Starting to send cancellation confirmation email to: {}", toEmail);
			
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

			helper.setText(content, true);
			
			logger.debug("Attempting to send cancellation email...");
			mailSender.send(message);
			logger.info("Successfully sent cancellation email to: {}", toEmail);
			
		} catch(MessagingException e) {
			logger.error("MessagingException while sending cancellation email to {}: {}", toEmail, e.getMessage());
			logger.error("Detailed error: ", e);
			throw new RuntimeException("Failed to create cancellation email message: " + e.getMessage(), e);
		} catch(Exception e) {
			logger.error("Unexpected error while sending cancellation email to {}: {}", toEmail, e.getMessage());
			logger.error("Detailed error: ", e);
			throw new RuntimeException("Error sending cancellation email: " + e.getMessage(), e);
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

	public void sendBookingCancellation(String toEmail, Booking booking) {
		try {
			Context context = new Context();
			context.setVariable("booking", booking);
			context.setVariable("date", booking.getDate().format(DATE_FORMATTER));
			context.setVariable("time", booking.getTimeSlot().getSlotTime().format(TIME_FORMATTER));
			
			String emailContent = templateEngine.process("booking-cancellation", context);
			
			sendEmail(toEmail, "Booking Cancelled - Ride #" + booking.getId(), emailContent);
			logger.info("Booking cancellation email sent to {}", toEmail);
			
		} catch (Exception e) {
			logger.error("Failed to send booking cancellation email: {}", e.getMessage());
		}
	}
	
	public void sendRideStartedNotification(String toEmail, Booking booking) {
		try {
			Context context = new Context();
			context.setVariable("booking", booking);
			context.setVariable("date", booking.getDate().format(DATE_FORMATTER));
			context.setVariable("time", booking.getTimeSlot().getSlotTime().format(TIME_FORMATTER));
			context.setVariable("driver", booking.getCab().getDriver());
			
			String emailContent = templateEngine.process("ride-started", context);
			
			sendEmail(toEmail, "Ride Started - Ride #" + booking.getId(), emailContent);
			logger.info("Ride started notification sent to {}", toEmail);
			
		} catch (Exception e) {
			logger.error("Failed to send ride started notification: {}", e.getMessage());
		}
	}
	
	public void sendDriverBookingNotification(Booking booking) {
		try {
			Driver driver = booking.getCab().getDriver();
			String driverEmail = driver.getEmail();
			
			Context context = new Context();
			context.setVariable("booking", booking);
			context.setVariable("date", booking.getDate().format(DATE_FORMATTER));
			context.setVariable("time", booking.getTimeSlot().getSlotTime().format(TIME_FORMATTER));
			context.setVariable("customer", booking.getUser());
			
			String emailContent = templateEngine.process("driver-booking-notification", context);
			
			sendEmail(driverEmail, "New Booking Assigned - Ride #" + booking.getId(), emailContent);
			logger.info("Driver booking notification sent to {}", driverEmail);
			
		} catch (Exception e) {
			logger.error("Failed to send driver booking notification: {}", e.getMessage());
		}
	}
	
	public void sendDriverCancellationNotification(Booking booking) {
		try {
			Driver driver = booking.getCab().getDriver();
			String driverEmail = driver.getEmail();
			
			Context context = new Context();
			context.setVariable("booking", booking);
			context.setVariable("date", booking.getDate().format(DATE_FORMATTER));
			context.setVariable("time", booking.getTimeSlot().getSlotTime().format(TIME_FORMATTER));
			context.setVariable("customer", booking.getUser());
			
			String emailContent = templateEngine.process("driver-booking-cancellation", context);
			
			sendEmail(driverEmail, "Booking Cancelled - Ride #" + booking.getId(), emailContent);
			logger.info("Driver cancellation notification sent to {}", driverEmail);
			
		} catch (Exception e) {
			logger.error("Failed to send driver cancellation notification: {}", e.getMessage());
		}
	}

	private void sendEmail(String to, String subject, String content) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(content, true);
		
		mailSender.send(message);
	}
}