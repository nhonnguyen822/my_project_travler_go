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
public class TourScheduleDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private ScheduleStatus status;
    private BigDecimal price;
    private BigDecimal childPrice;
    private BigDecimal babyPrice;
}
