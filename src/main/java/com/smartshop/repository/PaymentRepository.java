package com.smartshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartshop.model.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 依綠界交易編號查詢（唯一）
    Optional<Payment> findByTransactionId(String transactionId);

    // 依使用者查詢付款紀錄（多筆）
    List<Payment> findByUserId(Long userId);

    // 依訂單查詢唯一付款紀錄（可能為 null）
    Optional<Payment> findByOrderId(Long orderId);
}
