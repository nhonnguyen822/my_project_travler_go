package com.example.tourtravelserver.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDTO {
    private LocalTime time;
    private String title;
    private String details;
    private String imageUrl;
    private Integer position;
}
