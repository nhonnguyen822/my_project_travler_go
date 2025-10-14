package com.example.tourtravelserver.entity;

import com.example.tourtravelserver.enums.BookingStatus;
import com.example.tourtravelserver.enums.PaymentMethod;
import com.example.tourtravelserver.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime bookingDate;
    private int numberOfPeople;
    private int adultCount;
    private int childCount;
    private int babyCount;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "tour_schedule_id")
    private TourSchedule tourSchedule;

}
