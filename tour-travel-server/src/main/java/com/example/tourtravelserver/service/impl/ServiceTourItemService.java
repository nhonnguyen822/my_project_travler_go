package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.entity.ServiceItem;
import com.example.tourtravelserver.repository.IServiceItemRepository;
import com.example.tourtravelserver.service.IServiceTourItemService;
import io.jsonwebtoken.impl.lang.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceTourItemService implements IServiceTourItemService {
    private final IServiceItemRepository serviceItemRepository;

    @Override
    public List<ServiceItem> getServicesTourItems() {
        return serviceItemRepository.findAll();
    }
}
