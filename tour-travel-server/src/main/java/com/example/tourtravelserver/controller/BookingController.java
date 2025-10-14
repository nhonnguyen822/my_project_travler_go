package com.example.tourtravelserver.controller;

import com.example.tourtravelserver.dto.BookingRequest;
import com.example.tourtravelserver.entity.Booking;
import com.example.tourtravelserver.service.impl.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest request) {
        System.out.println(request);
        return bookingService.createBooking(request);
    }

}
