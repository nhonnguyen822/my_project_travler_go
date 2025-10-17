package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.dto.TourImageDTO;
import com.example.tourtravelserver.entity.TourImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ITourImageRepository extends JpaRepository<TourImage, Long> {
    @Query("SELECT new com.example.tourtravelserver.dto.TourImageDTO(t.imageUrl) " +
            "FROM TourImage t " +
            "WHERE t.tour.id = :tourId " +
            "ORDER BY t.id ASC")
    List<TourImageDTO> findImagesByTourId(Long tourId);
}