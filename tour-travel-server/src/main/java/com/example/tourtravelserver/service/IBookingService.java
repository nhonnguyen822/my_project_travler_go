package com.example.tourtravelserver.service;

import com.example.tourtravelserver.dto.*;
import com.example.tourtravelserver.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IBookingService {
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


    BookingResponse updateBookingStatus(Long bookingId, String status, String reason);
    AdminBookingResponse updateBookingDetails(Long bookingId, AdminBookingRequest updateRequest);

    List<StatusOption> getAllStatuses();

    Page<BookingResponse> getCancelledBookings(String userName, String bookingCode, String tourTitle, Pageable pageable);

    Page<BookingResponse> searchCancelledBookings(String searchTerm, Pageable pageable);

    long getCancelledBookingsCount();

    BookingResponse updateBookingPayment(Long bookingId, AdminPaymentRequest paymentRequest);
}