package com.example.tourtravelserver.controller;

import com.example.tourtravelserver.entity.Region;
import com.example.tourtravelserver.entity.ServiceItem;
import com.example.tourtravelserver.repository.IServiceItemRepository;
import com.example.tourtravelserver.service.IRegionService;
import com.example.tourtravelserver.service.IServiceTourItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/service-tour")
@RequiredArgsConstructor
public class ServiceTourController {
    private final IServiceTourItemService serviceTourItemService;

    @GetMapping
    public ResponseEntity<?> getAllServiceTours() {
        List<ServiceItem> serviceItems = serviceTourItemService.getServicesTourItems();
        if (serviceItems.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(serviceItems, HttpStatus.OK);
    }



}

