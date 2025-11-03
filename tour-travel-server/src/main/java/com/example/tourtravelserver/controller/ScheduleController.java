package com.example.tourtravelserver.controller;

import com.example.tourtravelserver.dto.TourScheduleDTO;
import com.example.tourtravelserver.dto.TourScheduleRequestDTO;
import com.example.tourtravelserver.entity.TourSchedule;
import com.example.tourtravelserver.service.ITourScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ITourScheduleService scheduleService;

    // ================================
    //  Lấy danh sách lịch trình theo tour
    // ================================
    @GetMapping("tour/{tourId}")
    public ResponseEntity<?> getSchedulesByTour(@PathVariable Long tourId) {
        List<TourSchedule> schedules = scheduleService.getSchedulesByTour(tourId);
        if (schedules.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }


    // ================================
    //  Tạo mới lịch trình cho tour
    // ================================
    @PostMapping("/{tourId}")
    public ResponseEntity<?> createSchedule(
            @PathVariable Long tourId,
            @RequestBody TourScheduleRequestDTO tourScheduleRequestDTO
    ) {
        try {
            System.out.println(tourScheduleRequestDTO);
            TourSchedule created = scheduleService.createSchedule(tourId, tourScheduleRequestDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ================================
    //  Cập nhật lịch trình
    // ================================
    @PatchMapping("{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody TourSchedule schedule
    ) {
        try {
            TourSchedule updated = scheduleService.updateSchedule(scheduleId, schedule);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping("/delete/{scheduleId}")
    public ResponseEntity<?> cancelSchedule(
            @PathVariable Long scheduleId
    ) {
        try {
            Optional<TourSchedule> tourSchedule = scheduleService.findById(scheduleId);
            if (tourSchedule.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            int countBookingsActive = scheduleService.countBookingsActiveByTourSchedule(scheduleId);
            if (countBookingsActive > 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Không thể hủy lịch trình đã có " + countBookingsActive + " booking đã được đặt"));
            }

            TourSchedule cancelledSchedule = scheduleService.deleteSchedule(scheduleId);
            return ResponseEntity.ok(Map.of(
                    "message", "Hủy lịch trình thành công",
                    "schedule", cancelledSchedule
            ));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}