package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPolicyRepository extends JpaRepository<Policy,Long> {
}
