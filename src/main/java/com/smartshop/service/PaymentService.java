package com.smartshop.service;

import java.util.List;

import com.smartshop.model.dto.PaymentDTO;

public interface PaymentService {

    // 建立付款紀錄（未付款狀態）
    PaymentDTO createPayment(String username, Long orderId, String paymentMethod);

    // 模擬付款成功
    PaymentDTO completePayment(Long paymentId);

    // 查詢使用者付款紀錄
    List<PaymentDTO> getPaymentHistory(String username);

    // 查詢單筆付款紀錄
    PaymentDTO getPaymentById(Long paymentId);
}
