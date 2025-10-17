package com.example.tourtravelserver.service;

import com.example.tourtravelserver.dto.BookingRequest;
import com.example.tourtravelserver.entity.Booking;

import java.util.Map;
import java.util.Optional;

public interface IBookingService {

    void createPendingTransaction(Long bookingId, String txnRef, long amount);

    void markBookingAsPaid(String txnRef, long amount, Map<String, String> extraParams);

    void markBookingAsFailed(String txnRef, String responseCode, Map<String, String> extraParams);
    // methods here
    Booking createBooking(BookingRequest req);

    Optional<Booking> findById(Long id);
}