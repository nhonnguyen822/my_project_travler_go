package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.entity.ItineraryActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IItineraryActivityRepository extends JpaRepository<ItineraryActivity, Long> {
    // methods here
}