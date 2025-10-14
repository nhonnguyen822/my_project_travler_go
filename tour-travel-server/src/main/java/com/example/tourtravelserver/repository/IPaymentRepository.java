package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IPaymentRepository extends JpaRepository<Payment, Long> {
    @Query(value = "SELECT * FROM payments p WHERE p.transaction_code = :txnCode", nativeQuery = true)
    Optional<Payment> findByTransactionCode(@Param("txnCode") String txnCode);
    // methods here
}