package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.entity.Booking;
import com.example.tourtravelserver.entity.Payment;
import com.example.tourtravelserver.enums.BookingStatus;
import com.example.tourtravelserver.enums.PaymentStatus;
import com.example.tourtravelserver.repository.IBookingRepository;
import com.example.tourtravelserver.repository.IPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IBookingRepository bookingRepository;
    private final IPaymentRepository paymentRepository;

    /**
     * ✅ Xử lý kết quả thanh toán trả về từ VNPay
     */
    public void handlePaymentResult(String txnRef, long amount, String service, Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy booking #" + bookingId));

        Payment payment = paymentRepository.findByTransactionCode(txnRef)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch #" + txnRef));

        // Cập nhật trạng thái payment
        boolean success = "success".equalsIgnoreCase(status)
                || "00".equals(status); // Một số cổng trả về mã 00 là thành công

        payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        paymentRepository.save(payment);

        // Cập nhật trạng thái booking
        booking.setStatus(success ? BookingStatus.CONFIRMED : BookingStatus.PENDING);
        bookingRepository.save(booking);

        System.out.printf("[VNPay] %s | Booking #%d | TxnRef: %s | Amount: %,d VND%n",
                success ? "✅ Thành công" : "❌ Thất bại",
                bookingId, txnRef, amount);
    }
}
