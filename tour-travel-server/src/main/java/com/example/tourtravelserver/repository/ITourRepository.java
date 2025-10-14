package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITourRepository extends JpaRepository<Tour, Long> { 
    // methods here
}