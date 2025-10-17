package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.dto.PolicyDTO;
import com.example.tourtravelserver.entity.TourPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ITourPolicyRepository extends JpaRepository<TourPolicy,Long> {
    @Query("""
        SELECT new com.example.tourtravelserver.dto.PolicyDTO(
            p.id, p.name, p.type
        )
        FROM TourPolicy tp
        JOIN tp.policy p
        WHERE tp.tour.id = :tourId
    """)
    List<PolicyDTO> findPoliciesByTourId(Long tourId);
}
