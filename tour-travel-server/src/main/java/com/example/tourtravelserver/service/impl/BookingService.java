package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.dto.BookingRequest;
import com.example.tourtravelserver.entity.Booking;
import com.example.tourtravelserver.entity.Payment;
import com.example.tourtravelserver.entity.TourSchedule;
import com.example.tourtravelserver.enums.BookingStatus;
import com.example.tourtravelserver.enums.PaymentMethod;
import com.example.tourtravelserver.enums.PaymentStatus;
import com.example.tourtravelserver.repository.IBookingRepository;

import com.example.tourtravelserver.repository.IPaymentRepository;
import com.example.tourtravelserver.repository.ITourRepository;
import com.example.tourtravelserver.repository.ITourScheduleRepository;
import com.example.tourtravelserver.service.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
    private final IBookingRepository bookingRepository;
    private final IPaymentRepository paymentRepository;
    private final ITourRepository tourRepository;
    private final ITourScheduleRepository tourScheduleRepository;

    @Override
    public void createPendingTransaction(Long bookingId, String txnRef, long amount) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new RuntimeException("Không tìm thấy booking với ID: " + bookingId);
        }

        Booking booking = optionalBooking.get();

        // Tạo bản ghi Payment mới
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTransactionCode(txnRef);
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setPaymentMethod(PaymentMethod.VN_PAY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);

        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);
    }

    @Override
    public void markBookingAsPaid(String txnRef, long amount, Map<String, String> extraParams) {
        Optional<Payment> optionalPayment = paymentRepository.findByTransactionCode(txnRef);
        if (optionalPayment.isEmpty()) {
            throw new RuntimeException("Không tìm thấy giao dịch với mã: " + txnRef);
        }

        Payment payment = optionalPayment.get();
        Booking booking = payment.getBooking();

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(BigDecimal.valueOf(amount));
        paymentRepository.save(payment);

        booking.setStatus(BookingStatus.CONFIRMED); // hoặc SUCCESS, tùy enum bạn định nghĩa
        bookingRepository.save(booking);
    }

    @Override
    public void markBookingAsFailed(String txnRef, String responseCode, Map<String, String> extraParams) {
        Optional<Payment> optionalPayment = paymentRepository.findByTransactionCode(txnRef);
        if (optionalPayment.isEmpty()) {
            throw new RuntimeException("Không tìm thấy giao dịch với mã: " + txnRef);
        }

        Payment payment = optionalPayment.get();
        Booking booking = payment.getBooking();

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public Booking createBooking(BookingRequest request) {
        // Lấy TourSchedule từ ID, nếu không có thì ném exception
        TourSchedule tourSchedule = tourScheduleRepository.findById(request.getTourScheduleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy TourSchedule với ID: " + request.getTourScheduleId()));

        // Xây dựng booking
        Booking booking = Booking.builder()
                .bookingDate(request.getBookingDate() != null ? request.getBookingDate() : LocalDateTime.now())
                .numberOfPeople(request.getNumberOfPeople())
                .adultCount(request.getAdultCount())
                .childCount(request.getChildCount())
                .babyCount(request.getBabyCount())
                .totalPrice(request.getTotalPrice() != null ? request.getTotalPrice() : BigDecimal.ZERO)
                .status(request.getStatus() != null ? BookingStatus.valueOf(request.getStatus()) : BookingStatus.PENDING)
                .tourSchedule(tourSchedule)
                .userId(request.getUserId())
                .build();

        return bookingRepository.save(booking);
    }
}