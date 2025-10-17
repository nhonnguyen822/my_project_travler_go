package com.example.tourtravelserver.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tour_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceItem serviceItem;
}
