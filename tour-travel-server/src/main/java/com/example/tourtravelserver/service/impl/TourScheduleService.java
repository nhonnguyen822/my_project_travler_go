package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.dto.TourScheduleRequestDTO;
import com.example.tourtravelserver.entity.Tour;
import com.example.tourtravelserver.entity.TourSchedule;
import com.example.tourtravelserver.enums.ScheduleStatus;
import com.example.tourtravelserver.repository.ITourRepository;
import com.example.tourtravelserver.repository.ITourScheduleRepository;
import com.example.tourtravelserver.service.ITourScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TourScheduleService implements ITourScheduleService {
    private final ITourScheduleRepository scheduleRepository;
    private final ITourRepository tourRepository;

    @Override
    public List<TourSchedule> getSchedulesByTour(Long tourId) {
        return scheduleRepository.findByTourId(tourId);
    }

    public TourSchedule createSchedule(Long tourId, TourScheduleRequestDTO dto) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy tour ID = " + tourId));

        TourSchedule schedule = new TourSchedule();
        schedule.setTour(tour);
        schedule.setStartDate(dto.getStartDate());
        schedule.setEndDate(dto.getEndDate());
        schedule.setAvailableSlots(dto.getAvailableSlots());
        schedule.setPrice(dto.getPrice());
        schedule.setChildPrice(dto.getChildPrice());
        schedule.setBabyPrice(dto.getBabyPrice());
        schedule.setStatus(dto.getStatus());

        return scheduleRepository.save(schedule);
    }

    @Override
    public TourSchedule updateSchedule(Long scheduleId, TourSchedule tourSchedule) {
        TourSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        schedule.setStartDate(tourSchedule.getStartDate());
        schedule.setEndDate(tourSchedule.getEndDate());
        schedule.setPrice(tourSchedule.getPrice());
        schedule.setChildPrice(tourSchedule.getChildPrice());
        schedule.setBabyPrice(tourSchedule.getBabyPrice());
        schedule.setAvailableSlots(tourSchedule.getAvailableSlots());
        schedule.setStatus(tourSchedule.getStatus());
        return scheduleRepository.save(schedule);
    }

    @Override
    public TourSchedule deleteSchedule(Long scheduleId) {
        TourSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        schedule.setStatus(ScheduleStatus.CANCELLED);
        return scheduleRepository.save(schedule);
    }

    @Override
    public ScheduleStatus calculateCurrentStatus(TourSchedule schedule) {
        if (schedule.getStatus() == ScheduleStatus.CANCELLED) {
            return ScheduleStatus.CANCELLED;
        }

        LocalDate today = LocalDate.now();
        if (today.isAfter(schedule.getEndDate())) {
            return ScheduleStatus.COMPLETED;
        } else {
            return ScheduleStatus.ACTIVE;
        }
    }

    @Override
    public boolean hasAvailableSlots(TourSchedule schedule) {
        return schedule.getAvailableSlots() > 0;
    }

    @Override
    public Optional<TourSchedule> findById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }

    @Override
    public int countBookingsActiveByTourSchedule(Long id) {
        return scheduleRepository.countBookingsActiveByTourSchedule(id);
    }
}
