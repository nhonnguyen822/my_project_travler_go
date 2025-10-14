package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.entity.Booking;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface IBookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO payments (booking_id, payment_date, payment_method, amount, status, transaction_code) " +
            "VALUES (:bookingId, :paymentDate, :paymentMethod, :amount, :status, :txnRef)", nativeQuery = true)
    void createPendingTransaction(
            @Param("bookingId") Long bookingId,
            @Param("txnRef") String txnRef,
            @Param("amount") BigDecimal amount,
            @Param("paymentDate") LocalDateTime paymentDate,
            @Param("paymentMethod") String paymentMethod,
            @Param("status") String status
    );

    @Modifying
    @Transactional
    @Query(value = "UPDATE payments p " +
            "JOIN bookings b ON b.booking_id = p.booking_id " +
            "SET p.status = 'SUCCESS', b.status = 'CONFIRMED' " +
            "WHERE p.transaction_code = :txnRef", nativeQuery = true)
    void markBookingAsPaid(@Param("txnRef") String txnRef);

    @Modifying
    @Transactional
    @Query(value = "UPDATE payments p " +
            "JOIN bookings b ON b.booking_id = p.booking_id " +
            "SET p.status = 'FAILED', b.status = 'PENDING' " +
            "WHERE p.transaction_code = :txnRef", nativeQuery = true)
    void markBookingAsFailed(@Param("txnRef") String txnRef);
}