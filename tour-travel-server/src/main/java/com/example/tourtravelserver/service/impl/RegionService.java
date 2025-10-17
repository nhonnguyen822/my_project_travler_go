package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.entity.Region;
import com.example.tourtravelserver.entity.Tour;
import com.example.tourtravelserver.repository.IRegionRepository;
import com.example.tourtravelserver.repository.ITourRepository;
import com.example.tourtravelserver.service.IRegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService implements IRegionService {
    private final IRegionRepository regionRepository;
    private final ITourRepository tourRepository;


    @Override
    public List<Region> getAllRegions() {
        return regionRepository.findAll();

    }
}