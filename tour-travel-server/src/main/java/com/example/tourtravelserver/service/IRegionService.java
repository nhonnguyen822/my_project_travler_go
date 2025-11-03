package com.example.tourtravelserver.service;

import com.example.tourtravelserver.entity.Region;
import com.example.tourtravelserver.entity.Tour;

import java.util.List;
import java.util.Optional;

public interface IRegionService {

    List<Region> getAllRegions();

    Optional<Region> findById(Long id);

}