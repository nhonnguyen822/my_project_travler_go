package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.dto.BookingProjection;
import com.example.tourtravelserver.entity.Booking;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

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

    @Query(value = """
            SELECT 
                b.id,
                b.booking_code as bookingCode,
                b.booking_date as bookingDate,
                b.number_of_people as numberOfPeople,
                b.adult_count as adultCount,
                b.child_count as childCount,
                b.baby_count as babyCount,
                b.total_price as totalPrice,
                b.status,
                u.name as userName,
                u.email as userEmail,
                u.phone as userPhone,
                u.avatar as userAvatar,
                t.title as tourTitle,
                t.duration as tourDuration,
                t.image as tourImage,
                t.base_price as tourBasePrice,
                t.destination as tourDestination,
                ts.start_date as startDate,
                ts.end_date as endDate,
                ts.price as schedulePrice,
                ts.child_price as childPrice,
                ts.baby_price as babyPrice
            FROM bookings b
            LEFT JOIN users u ON b.user_id = u.id
            LEFT JOIN tour_schedules ts ON b.tour_schedule_id = ts.id
            LEFT JOIN tours t ON ts.tour_id = t.id
            WHERE (:userName IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :userName, '%')))
              AND (:bookingCode IS NULL OR LOWER(b.booking_code) LIKE LOWER(CONCAT('%', :bookingCode, '%')))
              AND (:tourTitle IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :tourTitle, '%')))
              AND (:status IS NULL OR b.status = :status)
            ORDER BY b.booking_date DESC
            """,
            countQuery = """
                    SELECT COUNT(*) 
                    FROM bookings b
                    LEFT JOIN users u ON b.user_id = u.id
                    LEFT JOIN tour_schedules ts ON b.tour_schedule_id = ts.id
                    LEFT JOIN tours t ON ts.tour_id = t.id
                    WHERE (:userName IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :userName, '%')))
                      AND (:bookingCode IS NULL OR LOWER(b.booking_code) LIKE LOWER(CONCAT('%', :bookingCode, '%')))
                      AND (:tourTitle IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :tourTitle, '%')))
                      AND (:status IS NULL OR b.status = :status)
                    """,
            nativeQuery = true)
    Page<BookingProjection> findAllWithFiltersProjection(
            @Param("userName") String userName,
            @Param("bookingCode") String bookingCode,
            @Param("tourTitle") String tourTitle,
            @Param("status") String status,
            Pageable pageable
    );

    @Query(value = "SELECT COUNT(*) FROM bookings WHERE status = :status", nativeQuery = true)
    long countByStatusNative(@Param("status") String status);

    @Query(value = "SELECT COUNT(*) FROM bookings", nativeQuery = true)
    long countAllNative();

    @Query(value = "SELECT * FROM bookings WHERE booking_code = :bookingCode", nativeQuery = true)
    Optional<Booking> findByBookingCodeNative(@Param("bookingCode") String bookingCode);


    // IBookingRepository.java - THÊM METHOD MỚI
    @Query(value = """
            SELECT 
                b.id,
                b.booking_code as bookingCode,
                b.booking_date as bookingDate,
                b.number_of_people as numberOfPeople,
                b.adult_count as adultCount,
                b.child_count as childCount,
                b.baby_count as babyCount,
                b.total_price as totalPrice,
                b.status,
                u.name as userName,
                u.email as userEmail,
                u.phone as userPhone,
                u.avatar as userAvatar,
                t.title as tourTitle,
                t.duration as tourDuration,
                t.image as tourImage,
                t.base_price as tourBasePrice,
                t.destination as tourDestination,
                ts.start_date as startDate,
                ts.end_date as endDate,
                ts.price as schedulePrice,
                ts.child_price as childPrice,
                ts.baby_price as babyPrice
            FROM bookings b
            LEFT JOIN users u ON b.user_id = u.id
            LEFT JOIN tour_schedules ts ON b.tour_schedule_id = ts.id
            LEFT JOIN tours t ON ts.tour_id = t.id
            WHERE (:search IS NULL OR 
                   LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                   LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                   LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                   LOWER(b.booking_code) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                   LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                   LOWER(t.destination) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(b.status) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:status IS NULL OR b.status = :status)
            ORDER BY b.booking_date DESC
            """,
            countQuery = """
                    SELECT COUNT(*) 
                    FROM bookings b
                    LEFT JOIN users u ON b.user_id = u.id
                    LEFT JOIN tour_schedules ts ON b.tour_schedule_id = ts.id
                    LEFT JOIN tours t ON ts.tour_id = t.id
                    WHERE (:search IS NULL OR 
                           LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                           LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                           LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                           LOWER(b.booking_code) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                           LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                           LOWER(t.destination) LIKE LOWER(CONCAT('%', :search, '%')) OR
                           LOWER(b.status) LIKE LOWER(CONCAT('%', :search, '%')))
                      AND (:status IS NULL OR b.status = :status)
                    """,
            nativeQuery = true)
    Page<BookingProjection> findByGlobalSearch(
            @Param("search") String search,
            @Param("status") String status,
            Pageable pageable
    );
}


