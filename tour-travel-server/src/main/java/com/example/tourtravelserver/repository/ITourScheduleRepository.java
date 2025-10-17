package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.dto.TourScheduleDTO;
import com.example.tourtravelserver.entity.TourSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ITourScheduleRepository  extends JpaRepository<TourSchedule,Long>{
    @Query("SELECT new com.example.tourtravelserver.dto.TourScheduleDTO(s.id, s.startDate, s.price,s.childPrice,s.babyPrice,s.availableSlots) " +
            "FROM TourSchedule s " +
            "WHERE s.tour.id = :tourId " +
            "ORDER BY s.startDate ASC")
    List<TourScheduleDTO> findSchedulesByTourId(Long tourId);
}
