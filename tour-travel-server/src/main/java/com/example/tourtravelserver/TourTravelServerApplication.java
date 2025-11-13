package com.example.tourtravelserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TourTravelServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TourTravelServerApplication.class, args);
    }
}
