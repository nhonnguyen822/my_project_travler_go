package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.entity.Booking;
import com.example.tourtravelserver.entity.Payment;
import com.example.tourtravelserver.entity.TourSchedule;
import com.example.tourtravelserver.entity.User;
import com.example.tourtravelserver.enums.BookingStatus;
import com.example.tourtravelserver.enums.CustomerType;
import com.example.tourtravelserver.enums.PaymentStatus;
import com.example.tourtravelserver.repository.IBookingRepository;
import com.example.tourtravelserver.repository.IPaymentRepository;
import com.example.tourtravelserver.repository.ITourScheduleRepository;
import com.example.tourtravelserver.repository.IUserRepository;
import com.example.tourtravelserver.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IBookingRepository bookingRepository;
    private final IPaymentRepository paymentRepository;
    private final ITourScheduleRepository scheduleRepository;
    private final IUserRepository userRepository;
    private final NotificationService notificationService;

    public void handlePaymentResult(String txnRef, long amount, String service, Long bookingId, String status) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy booking #" + bookingId));

        Payment payment = paymentRepository.findByTransactionCode(txnRef)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch #" + txnRef));

        TourSchedule tourSchedule = booking.getTourSchedule();
        BigDecimal totalAmount = booking.getPaidAmount();
        BigDecimal paidAmount = BigDecimal.valueOf(amount);

        boolean success = "success".equalsIgnoreCase(status) || "00".equals(status);

        if (success) {
            BookingStatus bookingStatus;
            notificationService.notifyPaymentSuccess(booking);
            if (paidAmount.compareTo(totalAmount) >= 0) {
                bookingStatus = BookingStatus.PAID;
                System.out.printf("[VNPay] ✅ HOÀN THÀNH | Booking #%d | Đã thanh toán toàn bộ: %,.0f VND | Tổng giá trị: %,.0f VND%n",
                        bookingId, paidAmount.doubleValue(), totalAmount.doubleValue());
                BigDecimal totalAmountSpent = bookingRepository.getTotalSpentByUser(booking.getUser().getId());
                long totalSpentValue = totalAmountSpent != null ? totalAmountSpent.longValue() : 0L;
                CustomerType newCustomerType = determineCustomerType(totalSpentValue);
                Optional<User> user = userRepository.findById(booking.getUser().getId());
                if (user.isPresent()) {
                    user.get().setCustomerType(newCustomerType);
                    userRepository.save(user.get());
                }
            } else {
                bookingStatus = BookingStatus.DEPOSIT_PAID;
                double percentage = paidAmount.divide(totalAmount, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
                System.out.printf("[VNPay] ✅ ĐẶT CỌC | Booking #%d | Đã thanh toán: %,.0f VND (%,.1f%%) | Tổng giá trị: %,.0f VND%n",
                        bookingId, paidAmount.doubleValue(), percentage, totalAmount.doubleValue());
            }
            scheduleRepository.save(tourSchedule);
            booking.setStatus(bookingStatus);
            booking.setPaidAmount(paidAmount);

        } else {
            booking.setStatus(BookingStatus.PENDING);
            System.out.printf("[VNPay] ❌ THẤT BẠI | Booking #%d | Số tiền: %,d VND | Tổng giá trị: %,.0f VND%n",
                    bookingId, amount, totalAmount.doubleValue());
        }
        payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setAmount(paidAmount);
        paymentRepository.save(payment);
        bookingRepository.save(booking);
    }

    private int getSpendingLevel(long totalAmountSpent) {
        if (totalAmountSpent >= CustomerType.DIAMOND.getMinTotalSpent()) return 7;
        if (totalAmountSpent >= CustomerType.PLATINUM.getMinTotalSpent()) return 6;
        if (totalAmountSpent >= CustomerType.VIP.getMinTotalSpent()) return 5;
        if (totalAmountSpent >= CustomerType.GOLD.getMinTotalSpent()) return 4;
        if (totalAmountSpent >= CustomerType.SILVER.getMinTotalSpent()) return 3;
        if (totalAmountSpent >= CustomerType.REGULAR.getMinTotalSpent()) return 2;
        return 1;
    }

    private CustomerType determineCustomerType(long totalAmountSpent) {
        return switch (getSpendingLevel(totalAmountSpent)) {
            case 7 -> CustomerType.DIAMOND;
            case 6 -> CustomerType.PLATINUM;
            case 5 -> CustomerType.VIP;
            case 4 -> CustomerType.GOLD;
            case 3 -> CustomerType.SILVER;
            case 2 -> CustomerType.REGULAR;
            case 1 -> CustomerType.NEW;
            default -> CustomerType.NEW;
        };
    }
}
