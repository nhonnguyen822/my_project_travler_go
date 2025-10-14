package com.example.tourtravelserver.service.vnpay;

import com.example.tourtravelserver.entity.Role;

import java.util.Optional;

public interface IRoleService {
    Optional<Role> findByName(String name);
    Role save(Role role);
}
