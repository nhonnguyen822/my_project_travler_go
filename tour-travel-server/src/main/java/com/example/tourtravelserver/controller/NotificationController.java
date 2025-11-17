//
//package com.example.tourtravelserver.controller;
//
//import com.example.tourtravelserver.entity.Notification;
//import com.example.tourtravelserver.service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/notifications")
//@RequiredArgsConstructor
//public class NotificationController {
//
//    private final NotificationService notificationService;
//
//    @GetMapping
//    public ResponseEntity<List<Notification>> getNotifications(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
//        Long userId = Long.parseLong(user.getUsername());
//        return ResponseEntity.ok(notificationService.getAllNotifications(userId));
//    }
//
//    @GetMapping("/unread")
//    public ResponseEntity<List<Notification>> getUnreadNotifications(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
//        Long userId = Long.parseLong(user.getUsername());
//        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
//    }
//
//    @GetMapping("/unread-count")
//    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
//        Long userId = Long.parseLong(user.getUsername());
//        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
//    }
//
//    @PutMapping("/{id}/read")
//    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
//        return ResponseEntity.ok(notificationService.markAsRead(id));
//    }
//
//    @PutMapping("/mark-all-read")
//    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
//        Long userId = Long.parseLong(user.getUsername());
//        notificationService.markAllAsRead(userId);
//        return ResponseEntity.ok().build();
//    }
//}