package com.smartshop.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    
    private List<OrderItemDTO> items;

    private PaymentDTO payment;
}
