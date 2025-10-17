package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ITourRepository extends JpaRepository<Tour, Long> {

    @Query(value = "SELECT * FROM tours WHERE id = :tourId", nativeQuery = true)
    Optional<Tour> findTourById(Long tourId);

    @Query(value = "SELECT * FROM tours WHERE region_id = :regionId", nativeQuery = true)
    List<Tour> findByRegionId(@Param("regionId") Long regionId);
}