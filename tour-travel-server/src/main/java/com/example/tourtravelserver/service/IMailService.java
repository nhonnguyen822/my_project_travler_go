package com.example.tourtravelserver.service;

import com.example.tourtravelserver.entity.Booking;
import jakarta.mail.MessagingException;

import java.io.InputStream;

public interface IMailService {
    void sendBookingConfirmation(Booking booking, String txnRef) throws Exception;

    void sendBookingPdfEmailAsync(String email, byte[] pdfBytes, String pdfName);

    void sendUserVerificationEmail(String email, String name, String token) throws MessagingException;

    void sendBookingWithTwoPdfsAsync(
            String email,
            byte[] ticketBytes, String ticketFilename,
            byte[] detailBytes, String detailFilename,
            Long bookingId,
            String subject,
            String messageBody
    );
}



