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
    private String paymentMethod; // example: CREDIT, LINEPAY, EC_PAY
    private String status;        // PENDING, SUCCESS, FAIL
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
