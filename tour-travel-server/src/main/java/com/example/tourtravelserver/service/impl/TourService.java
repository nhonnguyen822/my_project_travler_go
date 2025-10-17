package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.dto.*;
import com.example.tourtravelserver.entity.Tour;
import com.example.tourtravelserver.repository.*;
import com.example.tourtravelserver.service.ITourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourService implements ITourService {
    private final ITourRepository tourRepository;
    private final ITourImageRepository imageRepository;
    private final ITourScheduleRepository scheduleRepository;
    private final IItineraryDayRepository itineraryDayRepository;
    private final IItineraryActivityRepository activityRepository;
    private final ITourPolicyRepository tourPolicyRepository;
    private final ITourServiceRepository tourServiceRepository;

    @Override
    public List<Tour> getToursByRegion(Long regionId) {
        return tourRepository.findByRegionId(regionId);
    }


    @Override
    public TourDetailResponse getTourDetail(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        List<TourImageDTO> images = imageRepository.findImagesByTourId(tourId);
        List<TourScheduleDTO> schedules = scheduleRepository.findSchedulesByTourId(tourId);

        List<ItineraryDayDTO> itineraryDays = itineraryDayRepository.findDaysByTourId(tourId)
                .stream()
                .map(day -> {
                    List<ActivityDTO> activities = activityRepository.findActivitiesByDayId((day.getId()));
                    return ItineraryDayDTO.builder()
                            .dayIndex(day.getDayIndex())
                            .title(day.getTitle())
                            .activities(activities)
                            .build();
                }).toList();
        List<ServiceDTO> serviceList = tourServiceRepository.findServicesByTourId(tourId);
        List<PolicyDTO> policyList = tourPolicyRepository.findPoliciesByTourId(tourId);

        return TourDetailResponse.builder()
                .id(tour.getId())
                .name(tour.getTitle())
                .durationDays(Integer.parseInt(tour.getDuration()))
                .description(tour.getDescription())
                .highLight(tour.getHighLight())
                .images(images)
                .itineraryDays(itineraryDays)
                .schedules(schedules)
                .policies(policyList)
                .services(serviceList)
                .build();
    }
}
