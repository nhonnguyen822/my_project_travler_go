// service/NotificationService.java
package com.example.tourtravelserver.service;

import com.example.tourtravelserver.entity.Booking;
import com.example.tourtravelserver.entity.Notification;
import com.example.tourtravelserver.entity.User;
import com.example.tourtravelserver.repository.INotificationRepository;
import com.example.tourtravelserver.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final INotificationRepository notificationRepository;

    public void sendRealTimeNotification(Notification notification, Long userId) {
        try {
            // Lưu vào database
            Notification savedNotification = notificationRepository.save(notification);

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    savedNotification
            );

            log.info("✅ Real-time notification sent to user {}: {}", userId, notification.getTitle());
        } catch (Exception e) {
            log.error("❌ Failed to send real-time notification", e);
        }
    }

    public void notifyPaymentSuccess(Booking booking) {
        try {
            Notification notification = new Notification();
            notification.setTitle("💰 Thanh toán thành công");
            notification.setMessage(String.format(
                    "Khách hàng %s vừa thanh toán thành công cho booking #%d - %s",
                    booking.getUser().getName(),
                    booking.getId(),
                    booking.getTourSchedule().getTour().getTitle()
            ));
            notification.setType(Notification.NotificationType.valueOf("SUCCESS"));
            notification.setId(1L);
            notification.setBookingId(booking.getId());
            notification.setRelatedEntity("BOOKING");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRead(false);

            // Gửi real-time notification
            sendRealTimeNotification(notification, 1L); // Admin user ID

            log.info("📢 Payment success notification sent for booking #{}", booking.getId());
        } catch (Exception e) {
            log.error("❌ Failed to send payment success notification", e);
        }
    }
}