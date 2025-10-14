package com.example.tourtravelserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter

public class BookingRequest {
    private LocalDateTime bookingDate;
    private int numberOfPeople;
    private int adultCount;
    private int childCount;
    private int babyCount;
    private BigDecimal totalPrice;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private Long tourScheduleId;
    private Long userId;
}
