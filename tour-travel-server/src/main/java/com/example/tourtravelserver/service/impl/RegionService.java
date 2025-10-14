package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.service.IRegionService;
import com.example.tourtravelserver.repository.IRegionRepository;
import org.springframework.stereotype.Service;

@Service
public class RegionService implements IRegionService {
      private final IRegionRepository regionRepository;
      public RegionService (IRegionRepository regionRepository){
        this.regionRepository = regionRepository;
      }
    // implementation here
}