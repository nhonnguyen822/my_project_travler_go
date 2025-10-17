package com.example.tourtravelserver.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "itinerary_days",
        indexes = {@Index(name = "idx_itinerary_day_tour_dayindex", columnList = "tour_id, day_index")})
public class ItineraryDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_index", nullable = false)
    private Integer dayIndex;


    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "tour_id")
    @JsonBackReference
    private Tour tour;

    @OneToMany(mappedBy = "itineraryDay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ItineraryActivity> activities;

}
