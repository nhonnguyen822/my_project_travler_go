package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.dto.ItineraryDayDTO;
import com.example.tourtravelserver.entity.ItineraryDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IItineraryDayRepository extends JpaRepository<ItineraryDay, Long> {
    @Query("""
        SELECT new com.example.tourtravelserver.dto.ItineraryDayDTO(
            d.id,
            d.dayIndex,
            d.title,
            null
        )
        FROM ItineraryDay d
        WHERE d.tour.id = :tourId
        ORDER BY d.dayIndex ASC
    """)
    List<ItineraryDayDTO> findDaysByTourId(Long tourId);
}
