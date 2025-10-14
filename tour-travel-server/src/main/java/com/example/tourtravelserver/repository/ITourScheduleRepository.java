package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.entity.TourSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITourScheduleRepository  extends JpaRepository<TourSchedule,Long>{
}
