package com.cab.book.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cab.book.entity.Booking;
import com.cab.book.entity.BookingOtp;
import com.cab.book.repository.BookingOtpRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {
    
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    
    @Autowired
    private BookingOtpRepository bookingOtpRepository;
    
    private static final int OTP_LENGTH = 6;
    
    @Value("${qr.code.directory:qr-codes}")
    private String qrCodeDirectory;
    
    public String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        logger.debug("Generated OTP: {}", otp.toString());
        return otp.toString();
    }
    
    public BookingOtp createOtpForBooking(Booking booking) {
        logger.info("Creating OTP for booking ID: {}", booking.getId());
        String otp = generateOtp();
        LocalDateTime validUntil = booking.getDate().atTime(booking.getTimeSlot().getSlotTime()).plusHours(1);
        
        BookingOtp bookingOtp = new BookingOtp(booking, otp, validUntil);
        logger.debug("Saving BookingOtp with OTP: {}, valid until: {}", otp, validUntil);
        return bookingOtpRepository.save(bookingOtp);
    }
    
    public Map<String, String> generateQrCode(String otp) throws WriterException {
        try {
            logger.info("Starting QR code generation for OTP");
            
            // Create QR code directory if it doesn't exist
            File directory = new File(qrCodeDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
                logger.info("Created directory for QR codes: {}", directory.getAbsolutePath());
            }
            
            // Configure QR code parameters
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            
            // Create QR code writer with configuration
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                otp,
                BarcodeFormat.QR_CODE,
                300,
                300,
                hints
            );
            
            // Generate file path for QR code
            String fileName = String.format("qr_code_%s_%d.png", otp, System.currentTimeMillis());
            Path filePath = Paths.get(qrCodeDirectory, fileName);
            
            // Save QR code to file
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);
            logger.info("Saved QR code to file: {}", filePath);
            
            // Also generate base64 for email embedding
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            
            // Return both file path and base64
            Map<String, String> result = new HashMap<>();
            result.put("filePath", filePath.toString());
            result.put("base64", base64Image);
            
            logger.info("Successfully generated QR code, saved to file and generated base64");
            return result;
            
        } catch (Exception e) {
            logger.error("Error generating QR code: {}", e.getMessage());
            logger.error("Detailed error: ", e);
            throw new RuntimeException("Error generating QR code: " + e.getMessage(), e);
        }
    }
    
    public boolean validateOtp(String otp) {
        logger.info("Validating OTP: {}", otp);
        return bookingOtpRepository.findByOtpAndIsUsedFalse(otp)
            .map(bookingOtp -> {
                if (LocalDateTime.now().isAfter(bookingOtp.getValidUntil())) {
                    logger.warn("OTP expired. Valid until: {}", bookingOtp.getValidUntil());
                    return false;
                }
                bookingOtp.setUsed(true);
                bookingOtpRepository.save(bookingOtp);
                logger.info("OTP validated successfully");
                return true;
            })
            .orElseGet(() -> {
                logger.warn("Invalid or already used OTP: {}", otp);
                return false;
            });
    }
} 