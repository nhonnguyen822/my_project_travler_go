package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.service.ITourImageService;
import com.example.tourtravelserver.repository.ITourImageRepository;
import org.springframework.stereotype.Service;

@Service
public class TourImageService implements ITourImageService {
      private final ITourImageRepository tourImageRepository;
      public TourImageService (ITourImageRepository tourImageRepository){
        this.tourImageRepository = tourImageRepository;
      }
    // implementation here
}