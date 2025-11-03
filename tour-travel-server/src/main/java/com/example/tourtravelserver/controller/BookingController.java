package com.example.tourtravelserver.controller;

import com.example.tourtravelserver.dto.AdminBookingRequest;
import com.example.tourtravelserver.dto.AdminBookingResponse;
import com.example.tourtravelserver.dto.BookingRequest;
import com.example.tourtravelserver.dto.BookingResponse;
import com.example.tourtravelserver.entity.Booking;
import com.example.tourtravelserver.service.IBookingService;
import com.example.tourtravelserver.service.ITourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping("")
    public ResponseEntity<?> getAllBookings() {
        List<Booking> bookings = bookingService.findAll();
        if (bookings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createBookingByAdmin(
            @Valid @RequestBody AdminBookingRequest bookingRequest) {
        try {
            AdminBookingResponse booking = bookingService.createBookingByAdmin(bookingRequest);
            return ResponseEntity.ok(booking);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("❌ Lỗi tạo booking: " + e.getMessage());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        Optional<Booking> booking = bookingService.findById(id);
        if (booking.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @GetMapping("/verify/{bookingId}")
    public ResponseEntity<String> verifyBooking(@PathVariable Long bookingId) {
        Optional<Booking> booking = bookingService.findById(bookingId);
        if (booking.isPresent()) {
            return ResponseEntity.ok("✅ Vé hợp lệ - " + booking.get().getUser().getName());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Vé không hợp lệ");
        }
    }


    @GetMapping("/filter")
    public ResponseEntity<Page<BookingResponse>> getBookingsWithFilters(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String bookingCode,
            @RequestParam(required = false) String tourTitle,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookingDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy));

        Page<BookingResponse> bookings = bookingService.findAllWithFilters(
                userName, bookingCode, tourTitle, status, pageable);

        return ResponseEntity.ok(bookings);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<BookingResponse>> searchBookings(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookingDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy));

        Page<BookingResponse> bookings = bookingService.globalSearch(
                search, status, pageable);

        return ResponseEntity.ok(bookings);
    }
}
