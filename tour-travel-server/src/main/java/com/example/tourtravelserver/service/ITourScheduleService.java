package com.example.tourtravelserver.service;

import com.example.tourtravelserver.dto.TourScheduleRequestDTO;
import com.example.tourtravelserver.entity.TourSchedule;
import com.example.tourtravelserver.enums.ScheduleStatus;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ITourScheduleService {
    List<TourSchedule> getSchedulesByTour(Long tourId);

    TourSchedule createSchedule(Long tourId, TourScheduleRequestDTO dto);

    TourSchedule updateSchedule(Long scheduleId, TourSchedule tourSchedule);

    TourSchedule deleteSchedule(Long scheduleId);

    ScheduleStatus calculateCurrentStatus(TourSchedule schedule);

    boolean hasAvailableSlots(TourSchedule schedule);

    Optional<TourSchedule> findById(Long scheduleId);
    int countBookingsActiveByTourSchedule(Long id);

}

