package com.example.tourtravelserver.service;

import com.example.tourtravelserver.dto.TourDetailResponse;
import com.example.tourtravelserver.dto.TourRequest;
import com.example.tourtravelserver.entity.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ITourService {
    TourDetailResponse getTourDetail(Long tourId);

    List<Tour> getToursByRegion(Long regionId);

    Optional<Tour> getTourById(Long id);

    List<Tour> getAllTours();

    TourDetailResponse updateTour(Long id, TourRequest updatedTour);

    Page<Tour> searchTours(int page, int size, String title, String destination, Double minPrice, Double maxPrice, String status);

    TourDetailResponse createTour(TourRequest request) throws IOException;

    List<Tour> getMostPopularTour();

    Optional<Tour> softDeleteAndGetTour(Long id);

    boolean restoreTour(Long id);

    int countActiveTourSchedules(Long tourId);
}