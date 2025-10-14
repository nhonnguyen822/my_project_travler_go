package com.example.tourtravelserver.entity;

import com.example.tourtravelserver.enums.TourStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String duration;

    private String image;

    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    private TourStatus status;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;
    
}
