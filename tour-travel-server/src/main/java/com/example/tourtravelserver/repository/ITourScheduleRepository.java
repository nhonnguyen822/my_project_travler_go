package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.dto.TourScheduleDTO;
import com.example.tourtravelserver.entity.TourSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ITourScheduleRepository extends JpaRepository<TourSchedule, Long> {
    @Query("SELECT new com.example.tourtravelserver.dto.TourScheduleDTO(s.id, s.startDate,s.endDate,s.status, s.price,s.childPrice,s.babyPrice,s.availableSlots) " +
            "FROM TourSchedule s " +
            "WHERE s.tour.id = :tourId " +
            "ORDER BY s.startDate ASC")
    List<TourScheduleDTO> findSchedulesByTourId(Long tourId);

    List<TourSchedule> findByTourId(Long tourId);

    @Query(value = "SELECT count(*) \n" +
            "FROM tour_schedules ts \n" +
            "JOIN bookings b ON ts.id = b.tour_schedule_id\n" +
            "WHERE ts.id = :id \n" +
            "  AND b.status =\"CONFIRMED\";", nativeQuery = true)
    int countBookingsActiveByTourSchedule(@Param("id") Long id);

    @Query("SELECT ts FROM TourSchedule ts JOIN Booking b ON b.tourSchedule.id = ts.id WHERE b.id = :bookingId")
    TourSchedule findTourScheduleByBookingId(@Param("bookingId") Long bookingId);

    Optional<TourSchedule> findByTourIdAndStartDate(Long tourId, LocalDate startDate);
}
