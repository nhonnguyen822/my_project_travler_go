package com.example.tourtravelserver.controller;

import com.example.tourtravelserver.service.GeoLocationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geo")
public class GeoController {

    private final GeoLocationService geoService;

    public GeoController(GeoLocationService geoService) {
        this.geoService = geoService;
    }

    @GetMapping("/location")
    public double[] getLocation(@RequestParam String name) {
        return geoService.getCoordinates(name);
    }
}
