package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.entity.Policy;
import com.example.tourtravelserver.repository.IPolicyRepository;
import com.example.tourtravelserver.service.IPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService implements IPolicyService {
    private final IPolicyRepository policyRepository;

    @Override
    public List<Policy> getAll() {
        return policyRepository.findAll();
    }
}
