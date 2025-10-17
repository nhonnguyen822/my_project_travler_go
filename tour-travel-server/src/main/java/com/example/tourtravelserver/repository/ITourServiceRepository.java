package com.example.tourtravelserver.repository;


import com.example.tourtravelserver.dto.ServiceDTO;

import com.example.tourtravelserver.entity.TourService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ITourServiceRepository extends JpaRepository<TourService,Long> {
    @Query("""
                SELECT new com.example.tourtravelserver.dto.ServiceDTO(
                    s.id, s.name, s.type
                )
                FROM TourService ts
                JOIN ts.serviceItem s
                WHERE ts.tour.id = :tourId
            """)
    List<ServiceDTO> findServicesByTourId(Long tourId);
}
