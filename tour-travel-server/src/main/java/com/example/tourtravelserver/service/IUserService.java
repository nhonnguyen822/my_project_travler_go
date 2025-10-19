package com.example.tourtravelserver.service;

import com.example.tourtravelserver.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface IUserService {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);

    @Transactional
    void register(User user);

    void resendEmailVerification(User user);

    String updateAvatar(Long userId, String newAvatar) throws Exception;

}
