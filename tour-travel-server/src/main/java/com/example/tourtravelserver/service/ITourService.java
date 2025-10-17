package com.example.tourtravelserver.service;

import com.example.tourtravelserver.dto.TourDetailResponse;
import com.example.tourtravelserver.entity.Tour;

import java.util.List;

public interface ITourService {
    TourDetailResponse getTourDetail(Long tourId);
    List<Tour> getToursByRegion(Long regionId);
}