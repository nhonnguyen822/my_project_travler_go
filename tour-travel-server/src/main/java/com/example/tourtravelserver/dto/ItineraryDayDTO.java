package com.example.tourtravelserver.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryDayDTO {
    private Long id;
    private Integer dayIndex;          // rename dayNumber -> dayIndex cho khớp entity ItineraryDay
    private String title;
    private List<ActivityDTO> activities;

}
