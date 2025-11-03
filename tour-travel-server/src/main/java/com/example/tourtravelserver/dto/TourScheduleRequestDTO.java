package com.example.tourtravelserver.dto;

import com.example.tourtravelserver.enums.ScheduleStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourScheduleRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer availableSlots;
    private BigDecimal price;
    private BigDecimal childPrice;
    private BigDecimal babyPrice;
    private ScheduleStatus status;
}
