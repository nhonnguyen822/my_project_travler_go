package com.example.tourtravelserver.service;

import com.example.tourtravelserver.entity.Notification;

import java.util.List;

public interface INotificationService {
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Long countByUserIdAndIsReadFalse(Long userId);
}
