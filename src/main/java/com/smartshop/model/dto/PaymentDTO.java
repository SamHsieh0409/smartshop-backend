package com.smartshop.model.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentDTO {

    private Long id;

    private Long orderId;

    private String method;       // credit_card, linepay, ecpay...

    private Double amount;       // 訂單金額

    private String status;       // PENDING / SUCCESS / FAILED

    private String transactionId; // 第三方金流回傳的交易編號

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt; // 付款成功時間（以前叫 paidAt）
}
