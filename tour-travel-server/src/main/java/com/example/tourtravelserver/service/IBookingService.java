package com.example.tourtravelserver.service;

import com.example.tourtravelserver.dto.AdminBookingRequest;
import com.example.tourtravelserver.dto.AdminBookingResponse;
import com.example.tourtravelserver.dto.BookingRequest;
import com.example.tourtravelserver.dto.BookingResponse;
import com.example.tourtravelserver.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IBookingService {

    void createPendingTransaction(Long bookingId, String txnRef, long amount);

    void markBookingAsPaid(String txnRef, long amount, Map<String, String> extraParams);

    void markBookingAsFailed(String txnRef, String responseCode, Map<String, String> extraParams);

    // methods here
    Booking createBooking(BookingRequest req);

    Optional<Booking> findById(Long id);

    List<Booking> findAll();

    Page<BookingResponse> findAllWithFilters(
            String userName,
            String bookingCode,
            String tourTitle,
            String status,
            Pageable pageable);

    Page<BookingResponse> globalSearch(String search, String status, Pageable pageable);
    AdminBookingResponse createBookingByAdmin(AdminBookingRequest request);

}