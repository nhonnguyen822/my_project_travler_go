package com.example.tourtravelserver.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourDetailResponse {
    private Long id;
    private String name;
    private Integer durationDays;
    private String description;
    private String highLight;
    private List<TourImageDTO> images;
    private List<ItineraryDayDTO> itineraryDays;
    private List<TourScheduleDTO> schedules;

    private List<PolicyDTO> policies;
    private List<ServiceDTO> services;
}
