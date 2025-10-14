package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.service.IItineraryActivityService;
import com.example.tourtravelserver.repository.IItineraryActivityRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItineraryActivityService implements IItineraryActivityService {
      private final IItineraryActivityRepository tourItineraryRepository;

}