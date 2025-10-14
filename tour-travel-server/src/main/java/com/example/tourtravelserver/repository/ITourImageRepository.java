package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.entity.TourImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITourImageRepository extends JpaRepository<TourImage, Long> { 
    // methods here
}