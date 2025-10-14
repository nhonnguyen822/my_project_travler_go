package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.service.ITourService;
import com.example.tourtravelserver.repository.ITourRepository;
import org.springframework.stereotype.Service;

@Service

public class TourService implements ITourService {
      private final ITourRepository tourRepository;
      public TourService (ITourRepository tourRepository){
        this.tourRepository = tourRepository;
      }
    // implementation here
}