package com.example.tourtravelserver.service;

import com.example.tourtravelserver.entity.Role;

import java.util.List;
import java.util.Optional;

public interface IRoleService {
    List<Role> findAll();
    

    Optional<Role> findById(Long id);

    void remove(Long id);
    Optional<Role> findByName(String name);

    Role save(Role role);
}
