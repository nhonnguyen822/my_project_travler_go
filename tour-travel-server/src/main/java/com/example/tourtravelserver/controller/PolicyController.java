package com.example.tourtravelserver.controller;

import com.example.tourtravelserver.entity.Policy;
import com.example.tourtravelserver.entity.ServiceItem;
import com.example.tourtravelserver.service.IPolicyService;
import com.example.tourtravelserver.service.IServiceTourItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {
    private final IPolicyService policyService;

    @GetMapping
    public ResponseEntity<?> getAllPolicy() {
        List<Policy> policies = policyService.getAll();
        if (policies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(policies, HttpStatus.OK);
    }
}
