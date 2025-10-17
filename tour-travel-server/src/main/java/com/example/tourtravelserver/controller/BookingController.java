package com.example.tourtravelserver.controller;

import com.example.tourtravelserver.dto.BookingRequest;
import com.example.tourtravelserver.entity.Booking;
import com.example.tourtravelserver.entity.Region;
import com.example.tourtravelserver.entity.Tour;
import com.example.tourtravelserver.service.IBookingService;
import com.example.tourtravelserver.service.ITourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final IBookingService bookingService;
    private final ITourService tourService;

    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest request) {
        System.out.println(request);
        return bookingService.createBooking(request);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        Optional<Booking> booking = bookingService.findById(id);
        if (booking.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }
}
