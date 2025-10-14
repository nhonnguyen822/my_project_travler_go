package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRegionRepository extends JpaRepository<Region, Long> { 
    // methods here
}