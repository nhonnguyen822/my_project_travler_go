package com.example.tourtravelserver.controller;

import com.example.tourtravelserver.dto.TourDetailResponse;
import com.example.tourtravelserver.entity.Tour;
import com.example.tourtravelserver.service.ITourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {
    private final ITourService tourService;

    // Lấy chi tiết tour theo ID
    @GetMapping("/{tourId}")
    public ResponseEntity<TourDetailResponse> getTourDetail(@PathVariable Long tourId) {
        TourDetailResponse tourDetail = tourService.getTourDetail(tourId);
        if (tourDetail == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tourDetail, HttpStatus.OK);
    }

    @GetMapping("/region/{regionId}")
    public ResponseEntity<?> getToursByRegion(@PathVariable Long regionId) {
        List<Tour> toursByRegion = tourService.getToursByRegion(regionId);
        System.out.println(toursByRegion);
        if (toursByRegion.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(toursByRegion, HttpStatus.OK);
    }
}
