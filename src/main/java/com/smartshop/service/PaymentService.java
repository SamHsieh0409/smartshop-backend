package com.smartshop.service;

import com.smartshop.model.dto.PaymentDTO;

import java.util.List;

public interface PaymentService {

    // 產生綠界表單（自動 submit）
    String generateEcpayForm(String username, Long orderId);

    // NotifyURL 回呼處理（綠界背景通知）
    void processEcpayCallback(String merchantTradeNo, int rtnCode, Integer amount);

    // 查詢付款紀錄
    List<PaymentDTO> getPaymentHistory(String username);

    PaymentDTO getPaymentById(Long paymentId);

}
